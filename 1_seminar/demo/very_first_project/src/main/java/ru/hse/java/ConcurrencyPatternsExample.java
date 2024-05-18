package ru.hse.java;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.Striped;
import com.lmax.disruptor.EventTranslatorThreeArg;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.jctools.maps.NonBlockingHashMapLong;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * @author egorlitvinenko.ru
 * */
public class ConcurrencyPatternsExample {

    static final Executor executor = new ForkJoinPool(8,
            ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            null, true);
    private volatile Object state = null;
    private final AtomicReference<CompletableFuture<Object>> dedup = new AtomicReference<>(null);


    record Entity(long id, String name) {

    }

    interface Db {
        CompletableFuture<Entity> getEntity(long id);
        CompletableFuture<Boolean> insert(long id, Entity entity);
    }

    static abstract class AbstractDb implements Db {
        protected Field[] fields;
        {
            fields = this.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.getClass().getSimpleName() + "[\n");
//            for (var field : fields) {
//                try {
//                    sb.append(field.get(this)).append(",");
//                } catch (Throwable ee) {}
//            }
//            sb.replace(sb.length() - 1, sb.length(), "]");
            return sb.toString();
        }
    }

    public static class DbWithConcurrentMap implements Db {
        final ConcurrentMap<Long, Entity> data = new ConcurrentHashMap<>();

        @Override
        public CompletableFuture<Entity> getEntity(long id) {
            return CompletableFuture.completedFuture(data.get(id));
        }

        @Override
        public CompletableFuture<Boolean> insert(long id, Entity entity) {
            data.put(id, entity);
            return CompletableFuture.completedFuture(Boolean.TRUE);
        }
    }

    public static class DbWithNonBlockingMap implements Db {
        final NonBlockingHashMapLong<Entity> data = new NonBlockingHashMapLong<>();

        @Override
        public CompletableFuture<Entity> getEntity(long id) {
            return CompletableFuture.completedFuture(data.get(id));
        }

        @Override
        public CompletableFuture<Boolean> insert(long id, Entity entity) {
            data.put(id, entity);
            return CompletableFuture.completedFuture(Boolean.TRUE);
        }
    }

    /**
     * */
    @NotThreadSafe
    public static class DbWithThreadLocalWrites implements Db {
        final Map<Thread, Long2ObjectOpenHashMap<Entity>> threads = new ConcurrentHashMap<>();
        final InheritableThreadLocal<Long2ObjectOpenHashMap<Entity>> locals = new InheritableThreadLocal<>() {
            @Override
            protected Long2ObjectOpenHashMap<Entity> initialValue() {
                var val = new Long2ObjectOpenHashMap<Entity>();
                var old = threads.putIfAbsent(Thread.currentThread(), val);
                return old == null ? val : old;
            }
        };

        @Override
        public CompletableFuture<Entity> getEntity(long id) {
            for (var map : threads.values()) {
                var entity = map.get(id);
                if (entity != null) {
                    return CompletableFuture.completedFuture(entity);
                }
            }
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletableFuture<Boolean> insert(long id, Entity entity) {
            locals.get().put(id, entity);
            return CompletableFuture.completedFuture(Boolean.TRUE);
        }
    }

    public static class DbWithReentrantLock implements Db {
        final Map<Long, Entity> data = new HashMap<>();
        final ReentrantLock lock = new ReentrantLock();

        @Override
        public CompletableFuture<Entity> getEntity(long id) {
            lock.lock();
            try {
                return CompletableFuture.completedFuture(data.get(id));
            } finally {
                lock.unlock();
            }
        }

        @Override
        public CompletableFuture<Boolean> insert(long id, Entity entity) {
            lock.lock();
            try {
                data.put(id, entity);
                return CompletableFuture.completedFuture(Boolean.TRUE);
            } finally {
                lock.unlock();
            }
        }
    }

    public static class DbWithStripedLock implements Db {
        final Map<Long, Entity> data = new HashMap<>();
        final Striped<ReadWriteLock> striped = Striped.readWriteLock(32);

        @Override
        public CompletableFuture<Entity> getEntity(long id) {
            var lock = striped.get(Long.hashCode(id));
            lock.readLock().lock();
            try {
                return CompletableFuture.completedFuture(data.get(id));
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public CompletableFuture<Boolean> insert(long id, Entity entity) {
            var lock = striped.get(Long.hashCode(id));
            lock.writeLock().lock();
            try {
                data.put(id, entity);
                return CompletableFuture.completedFuture(Boolean.TRUE);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    public static class DbWithReentrantRWLock implements Db {
        final Map<Long, Entity> data = new HashMap<>();
        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        @Override
        public CompletableFuture<Entity> getEntity(long id) {
            lock.readLock().lock();
            try {
                return CompletableFuture.completedFuture(data.get(id));
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public CompletableFuture<Boolean> insert(long id, Entity entity) {
            lock.writeLock().lock();
            try {
                data.put(id, entity);
                return CompletableFuture.completedFuture(Boolean.TRUE);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    public static class DbWithStampedLock implements Db {
        final Map<Long, Entity> data = new HashMap<>();
        final StampedLock lock = new StampedLock();

        @Override
        public CompletableFuture<Entity> getEntity(long id) {
            var stamp = lock.tryOptimisticRead();
            if (stamp != 0) {
                Entity result = data.get(id);
                if (lock.validate(stamp)) {
                    return CompletableFuture.completedFuture(result);
                }
            }
            stamp = lock.readLock();
            try {
                return CompletableFuture.completedFuture(data.get(id));
            } finally {
                lock.unlockRead(stamp);
            }
        }

        @Override
        public CompletableFuture<Boolean> insert(long id, Entity entity) {
            var stamp = lock.writeLock();
            try {
                data.put(id, entity);
                return CompletableFuture.completedFuture(Boolean.TRUE);
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

    public static class SyncDb extends AbstractDb {
        final Map<Long, Entity> data = Collections.synchronizedMap(new Long2ObjectOpenHashMap<>());

        @Override
        public CompletableFuture<Entity> getEntity(long id) {
            return CompletableFuture.completedFuture(data.get(id));
        }

        @Override
        public CompletableFuture<Boolean> insert(long id, Entity entity) {
            data.put(id, entity);
            return CompletableFuture.completedFuture(Boolean.TRUE);
        }
    }

    public static class DbWithDisruptor implements Db {
        static final class BufferEvent {
            long key;
            Entity entity;
            CompletableFuture<?> promise;

            boolean isRead() {
                return entity == null;
            }

            void writeAndClear(Entity entity) {
                ((CompletableFuture<Entity>) promise).complete(entity);
                key = 0;
                promise = null;
            }

            void completeAndClear() {
                ((CompletableFuture<Boolean>) promise).complete(Boolean.TRUE);
                key = 0;
                entity = null;
                promise = null;
            }
        }
        static final EventTranslatorThreeArg<BufferEvent, Long, Entity, CompletableFuture<?>>
                TRANSLATOR = (bufferEvent, l, aLong, entity, future) -> {
            bufferEvent.key = aLong;
            bufferEvent.entity = entity;
            bufferEvent.promise = future;
        };
        final Disruptor<BufferEvent> disruptor;
        /**
         * Read/Writes always from single thread inside disruptor.
         * */
        final Map<Long, Entity> data = new Long2ObjectOpenHashMap<>();

        {
            int bufferSize = 1024;
            disruptor = new Disruptor<>(
                    BufferEvent::new,
                    bufferSize,
                    DaemonThreadFactory.INSTANCE);

            disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
                if (event.isRead()) {
                    event.writeAndClear(data.get(event.key));
                } else {
                    data.put(event.key, event.entity);
                    event.completeAndClear();
                }
            });
            disruptor.start();

        }

        @Override
        public CompletableFuture<Entity> getEntity(long id) {
            CompletableFuture<Entity> future;
            disruptor.publishEvent(TRANSLATOR, id, null, future = new CompletableFuture<>());
            return future;
        }

        @Override
        public CompletableFuture<Boolean> insert(long id, Entity entity) {
            CompletableFuture<Boolean> future;
            disruptor.publishEvent(TRANSLATOR, id, entity, future = new CompletableFuture<>());
            return future;
        }
    }

    public static class DbWithActor implements Db {
        final Executor executor = Executors.newWorkStealingPool(2);
        final BaseActor<CompletableFuture<?>> dbActor = new BaseActor<>(executor);

        static final class ReadOp extends CompletableFuture<Entity> {
            private final long id;

            public ReadOp(long id) {
                this.id = id;
            }
        }

        static final class WriteOp extends CompletableFuture<Boolean> {
            private final long id;
            private final Entity entity;

            public WriteOp(long id, Entity entity) {
                this.id = id;
                this.entity = entity;
            }
        }
        /**
         * Read/Writes always from single thread inside disruptor.
         * */
        final Map<Long, Entity> data = new Long2ObjectOpenHashMap<>();

        {
            dbActor.setAction(future -> {
                switch (future) {
                    case ReadOp readOp -> {
                        var entity = data.get(readOp.id);
                        readOp.complete(entity);
                    }
                    case WriteOp writeOp -> {
                        data.put(writeOp.id, writeOp.entity);
                        writeOp.complete(Boolean.TRUE);
                    }
                    default -> future.completeExceptionally(new IllegalArgumentException("Unknown type of " + future));
                }
            });
        }

        @Override
        public CompletableFuture<Entity> getEntity(long id) {
            CompletableFuture<Entity> future = new ReadOp(id);
            dbActor.enqueue(future);
            return future;
        }

        @Override
        public CompletableFuture<Boolean> insert(long id, Entity entity) {
            CompletableFuture<Boolean> future = new WriteOp(id, entity);
            dbActor.enqueue(future);
            return future;
        }
    }

    private boolean expiredResult(Integer result) {
        return ((Integer) result) == 0;
    }

    CompletableFuture<Integer> calcResult() {
        b.incrementAndGet();
        return CompletableFuture.supplyAsync(() -> {
            if (ThreadLocalRandom.current().nextDouble() < 0.01) {
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
            }
            return Integer.valueOf(state == null ? 0 : state.hashCode());
        }, executor);
    }

    private final AtomicInteger a = new AtomicInteger();
    private final AtomicInteger b = new AtomicInteger();
    private final AtomicInteger c = new AtomicInteger();
    CompletableFuture<Object> updateState() {
        a.incrementAndGet();
        CompletableFuture<Object> future;
        do {
            future = dedup.get();
            if (future != null) {
                return future;
            }
        } while (!dedup.compareAndSet(null, future = new CompletableFuture<>()));
        final var futureFinal = future;
        Thread.ofVirtual().start(() -> {
            if (state == null) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
                c.incrementAndGet();
                state = new Object();
            }
            futureFinal.complete(state);
        });
        return futureFinal;
    }


    CompletableFuture<Integer> callService() throws Exception {
        return calcResult()
                .thenCompose(response -> {
                    if (expiredResult(response)) {
                        return updateState().thenCompose(ignore -> calcResult());
                    }
                    return CompletableFuture.completedFuture(response);
                });
    }

    public static void main(String[] args) throws Exception {

//        SyncDb, 225.5 ms, 75
//        ru.hse.java.ConcurrencyPatternsExample$DbWithReentrantLock@728938a9, 135.2 ms, 45
//        ru.hse.java.ConcurrencyPatternsExample$DbWithReentrantRWLock@3d646c37, 134.3 ms, 44
//        ru.hse.java.ConcurrencyPatternsExample$DbWithStampedLock@5a10411, 93.46 ms, 31
//        ru.hse.java.ConcurrencyPatternsExample$DbWithConcurrentMap@306a30c7, 90.48 ms, 30
//        ru.hse.java.ConcurrencyPatternsExample$DbWithStripedLock@2cdf8d8a, 129.0 ms, 42
//        ru.hse.java.ConcurrencyPatternsExample$DbWithNonBlockingMap@23ab930d, 69.49 ms, 23
//        ru.hse.java.ConcurrencyPatternsExample$DbWithDisruptor@1e81f4dc, 118.7 ms, 39
//        ru.hse.java.ConcurrencyPatternsExample$DbWithActor@5577140b, 83.24 ms, 27

        var candidates = List.of(
                SyncDb.class,
                DbWithReentrantLock.class,
                DbWithReentrantRWLock.class,
                DbWithStampedLock.class,
                DbWithConcurrentMap.class,
                DbWithStripedLock.class,
                DbWithNonBlockingMap.class,
//                DbWithThreadLocalWrites.class,
                DbWithDisruptor.class,
                DbWithActor.class);

        for (int c = 0; c < candidates.size(); c++) {

            var db = candidates.get(c).newInstance();

//            double sum = 0.d;
            int attempt = 3;
            Stopwatch stopwatch = Stopwatch.createStarted();
            for (int j = 0; j < attempt; j++) {
                var count = 100000;
                var readThreshold = 0.1;
                var databaseSize = 1000;
                var latch = new CountDownLatch(count);
                for (int i = 0; i < count; i++) {
                    if (ThreadLocalRandom.current().nextDouble() < readThreshold) {
                        CompletableFuture.runAsync(() -> {
                            db.getEntity(ThreadLocalRandom.current().nextLong(databaseSize));
                            latch.countDown();
                        }, executor);
                    } else {
                        var name = "Random" + i;
                        CompletableFuture.runAsync(() -> {
                            long id;
                            db.insert(id = ThreadLocalRandom.current().nextLong(databaseSize), new Entity(id, name));
                            latch.countDown();
                        }, executor);
                    }
                }
                latch.await();
            }
            stopwatch.stop();
            System.out.println(db);
            System.out.println(stopwatch);
            System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS) / attempt);
        }
    }



    public static void callServiceExample(String[] args) throws Exception {
        ConcurrencyPatternsExample example = new ConcurrencyPatternsExample();
        System.out.println(example.callService());

        List<CompletableFuture<Integer>> gets = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            gets.add(example.callService());
        }
        var allFutures = CompletableFuture.allOf(gets.toArray(CompletableFuture[]::new));
        allFutures.join();
        System.out.println("updateState = " + example.a.get());
        System.out.println("stateUpdated = " + example.c.get());
        System.out.println("calcResult = " + example.b.get());
        System.out.println(gets.get(ThreadLocalRandom.current().nextInt(gets.size())).get());
    }

    public static void reentrantLock(String[] args) {
        ReentrantLock rlock = new ReentrantLock();
        rlock.lock();
        try {
            // do
        } finally {
            rlock.unlock();
        }
    }

    public static void reentrantReadWriteLock(String[] args) {
        ReentrantReadWriteLock rlock = new ReentrantReadWriteLock(true);
        rlock.readLock().lock();
        try {
            // do
        } finally {
            rlock.readLock().unlock();
        }

        rlock.writeLock().lock();
        try {
            // do
        } finally {
            rlock.writeLock().unlock();
        }
    }

    private static StampedLock lock = new StampedLock();
    public static <T> T optimisticRead(boolean idempotent, Supplier<T> doRead) {
        T res = null;
        long stamp = idempotent ? lock.tryOptimisticRead() : 0L;
        if (stamp != 0L) {
            res = doRead.get();
        }
        if (stamp == 0L || !lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                res = doRead.get();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return res;
    }

    public static void writeIfConditionTrue(BooleanSupplier readEntityPrecondition, Runnable writeEntity) {
        long stamp = lock.readLock();
        try {
            while (readEntityPrecondition.getAsBoolean()) {
                long ws = lock.tryConvertToWriteLock(stamp);
                if (ws != 0L) {
                    stamp = ws;
                    writeEntity.run();
                    break;
                } else {
                    lock.unlockRead(stamp);
                    stamp = lock.writeLock();
                }
            }
        } finally {
            lock.unlock(stamp);
        }
    }


}
