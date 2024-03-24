package ru.hse.java;

import java.io.DataOutput;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author egorlitvinenko.ru
 * */
public class ParallelComputeExample {

    private AtomicLong atomicField = new AtomicLong();
    private volatile long field = 1;
    private static final AtomicLongFieldUpdater<ParallelComputeExample> updater =
            AtomicLongFieldUpdater.newUpdater(ParallelComputeExample.class, "field");

    public static void main(String[] args) {

        ReentrantLock lock = new ReentrantLock();
        ReentrantReadWriteLock lock2 = new ReentrantReadWriteLock();
        CompletableFuture<Void> a = new CompletableFuture<>();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // большая работа
                a.complete(null);
            } catch (Exception e) {
                a.completeExceptionally(e);
            }
        });
        CompletableFuture
                .supplyAsync(() -> ThreadLocalRandom.current().nextInt())
                .thenComposeAsync(number -> {
                    return CompletableFuture.supplyAsync(() -> number * 2);
                }, ForkJoinPool.commonPool());

//        System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", "ru.hse.java.ParallelComputeExample.DefaultForkJoinPoolThreadFactory");
//        var counter = new AtomicInteger();
//        var ref = new ParallelComputeExample();
//        updater.compareAndSet(ref, 1, 2);
//
//        ref.atomicField.updateAndGet(prev -> {
//            prev--;
//            return prev > 0 ? prev : 0;
//        });
//
//        long prev, next;
//        do {
//            prev = ref.atomicField.get();
//
//            if (prev % 2 == 0) {
//                next = prev + 2;
//            } else {
//                next = prev + ThreadLocalRandom.current().nextInt(3);
//            }
//        } while (!ref.atomicField.compareAndSet(prev, prev + next));
//
//        counter.incrementAndGet(); // counter++
//
        AtomicLongArray array = new AtomicLongArray(100);
        var tf = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                var thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        };

        System.out.println(Runtime.getRuntime().availableProcessors());
        ExecutorService executor2 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), tf);
        Executors.newCachedThreadPool();
        ExecutorService executor3 = new ThreadPoolExecutor(1, 200,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10_000),
                tf);
        ExecutorService executor4 = Executors.newWorkStealingPool(8);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4, tf);

        var count = 1_000;
        var latch = new CountDownLatch(count);
        var phaser = new Phaser(1);
        var cExecutor = executor3;

        for (int i = 0; i < count; i++) {
//            cExecutor.submit(new Runnable() {
//                @Override
//                public void run() {
//                    var index = ThreadLocalRandom.current().nextInt(array.length());
//                    var x = array.get(index) + 1;
//                    array.set(index, x);
//                    System.out.println("pool count: " + ((ThreadPoolExecutor) cExecutor).getPoolSize());
//                    latch.countDown();
//                }
//            });

            phaser.register();
            CompletableFuture.supplyAsync(() -> {
                        var index = ThreadLocalRandom.current().nextInt(array.length());
                        var x = array.get(index) + 1;
                        array.set(index, x);
                        System.out.println("pool count: " + ((ThreadPoolExecutor) cExecutor).getPoolSize());
                        latch.countDown();
                        phaser.arriveAndDeregister();
                        return x;
                    },
                    cExecutor
            ).thenApplyAsync(x -> x + 2, cExecutor);
        }

//        latch.await();
        phaser.arriveAndAwaitAdvance();
        for (int i = 0; i < array.length(); i++) {
            System.out.println(array.get(i));
        }
        executor2.shutdown();
    }

    static class DefaultForkJoinPoolThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return null;
        }
    }

}
