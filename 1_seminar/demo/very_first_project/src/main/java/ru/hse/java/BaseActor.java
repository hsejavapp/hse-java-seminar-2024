package ru.hse.java;

import org.jctools.queues.MpscChunkedArrayQueue;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

/**
 * @author Egor Litvinenko
 */
public class BaseActor<T> implements Runnable {

    private final MpscChunkedArrayQueue<T> mailbox;
    private final Executor executor;
    private final AtomicBoolean scheduled = new AtomicBoolean(false);
    private Consumer<T> action;

    public BaseActor(Executor executor) {
        this.mailbox = new MpscChunkedArrayQueue<>(2, 2_000_000);
        this.executor = executor;
    }

    public void setAction(Consumer<T> action) {
        this.action = action;
    }

    public void enqueue(T data) {
        mailbox.offer(data);
        if (scheduled.compareAndSet(false, true)) {
            executor.execute(this);
        }
    }

    @Override
    public void run() {
        T data;
        while ((data = mailbox.poll()) != null) {
            action.accept(data);
        }
        scheduled.set(false);
        if (!mailbox.isEmpty() && scheduled.compareAndSet(false, true)) {
            executor.execute(this);
        }
    }

    public static void main(String[] args) throws Exception {
        Executor executor1 = Executors.newFixedThreadPool(2);
        threadPoolExample(executor1); // Counter in pong:  32700000 -> [fjp] 147300009
//        actorExample(executor1);    // Counter in ping:  22370000 -> [fjp]  48700000 -> [change queue] 60500000
//        exchangeExample(executor1); // Counter in ping: 116260000
        Thread.sleep(10_000);
        System.exit(0);
    }

    static void threadPoolExample(Executor executor1) throws Exception {
        final AtomicInteger atomicInteger = new AtomicInteger();
        final Runnable action = () -> {
            atomicInteger.incrementAndGet();
            if (atomicInteger.get() % 100000 == 0) {
                System.out.println("Counter in pong: " + atomicInteger.get());
            }
        };
        final AtomicReference<Runnable> pingRef = new AtomicReference<>();
        final Runnable pong = () -> {
            action.run();
            executor1.execute(pingRef.get());
        };
        final Runnable ping = () -> {
            action.run();
            executor1.execute(pong);
        };
        pingRef.set(ping);
        executor1.execute(ping);
        executor1.execute(pong);
    }

    static void exchangeExample(Executor executor1) throws Exception {
        Exchanger<AtomicInteger> exchanger = new Exchanger<>();
        AtomicInteger counter = new AtomicInteger();
        Runnable ping = () -> {
            AtomicInteger c = counter;
            try {
                while (true) {
                    c.incrementAndGet();
                    if (counter.get() % 10000 == 0) {
                        System.out.println("Counter in ping: " + counter.get());
                    }
                    c = exchanger.exchange(c);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Runnable pong = () -> {
            AtomicInteger c = counter;
            try {
                while (true) {
                    c.incrementAndGet();
                    if (counter.get() % 10000 == 0) {
                        System.out.println("Counter in ping: " + counter.get());
                    }
                    c = exchanger.exchange(c);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        executor1.execute(ping);
        executor1.execute(pong);
    }

    static void actorExample(Executor executor1) {
        // Counter in pong: 22370000
        BaseActor<AtomicInteger> pong = new BaseActor<>(executor1);
        BaseActor<AtomicInteger> ping = new BaseActor<>(executor1);
        final AtomicInteger atomicInteger = new AtomicInteger();
        pong.setAction(counter -> {
            counter.incrementAndGet();
            if (counter.get() % 100000 == 0) {
                System.out.println("Counter in pong: " + counter.get());
            }
            ping.enqueue(counter);
        });
        ping.setAction(counter -> {
            counter.incrementAndGet();
            if (counter.get() % 100000 == 0) {
                System.out.println("Counter in ping: " + counter.get());
            }
            pong.enqueue(counter);
        });
        ping.enqueue(atomicInteger);

        BaseActor<Long> pingInterval = new BaseActor<>(executor1);
        pingInterval.setAction(startNanos -> {
            System.out.println("pinged: " + TimeUnit.NANOSECONDS.toMicros((System.nanoTime() - startNanos)));
        });
        new Thread(() -> {
            {
                while (true) {
                    long startNanos = System.nanoTime();
                    pingInterval.enqueue(startNanos);
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
                }
            }
        }).start();
    }

}
