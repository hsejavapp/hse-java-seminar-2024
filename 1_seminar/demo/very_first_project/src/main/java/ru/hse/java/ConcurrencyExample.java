package ru.hse.java;

import org.openjdk.jol.vm.VM;

import java.lang.management.ThreadMXBean;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author egorlitvinenko.ru
 * */
public class ConcurrencyExample {

    static class SheepCounter implements Runnable {
        static boolean asleep;
        private int sheeps = 0;
        public void fallAsleep() {
            this.asleep = true;
        }
        @Override
        public void run() {
            System.out.println("Begin");
            while (!asleep) {
                countSheep();
            }
            System.out.println("Done");
        }

        public void countSheep() {
            sheeps++;
        }
    }

    public static void main(String[] args) throws Exception {
        var sc = new SheepCounter();
        var t = new Thread(sc);
        t.start();
        Thread.sleep(1000);
        sc.fallAsleep();
        t.join();
        System.out.println(sc.sheeps);



//        final AtomicInteger atomic = new AtomicInteger();
//        final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1_000_000);
//        final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 4, 1_0000, TimeUnit.MILLISECONDS, queue);
//
//        var cnt = 1_000_000;
//        final CountDownLatch couint = new CountDownLatch(2 * cnt);
//        var thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    System.out.println(VM.current().details());
//                    try {
//                        Thread.sleep(1_000);
//                    } catch (Throwable e) {}
//                }
//            }
//        });
//        thread.setPriority(Thread.MAX_PRIORITY);
//
//        for (int i = 0; i < 1_000_000; i++) {
//            executor.submit(new Runnable() {
//                @Override
//                public void run() {
//                    atomic.incrementAndGet();
//                    couint.countDown();
//                }
//            });
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (true) {
//                        System.out.println(VM.current().details());
//                        try {
//                            Thread.sleep(1_000);
//                        } catch (Throwable e) {}
//                    }
//                }
//            }).start();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    synchronized (atomic) {
//                        atomic.incrementAndGet();
//                    }
//                    couint.countDown();
//                }
//            }).start();
//        }
//        System.out.println(atomic.get());
//        executor.shutdownNow();
    }

//    --- Execution profile ---
//    Total samples       : 85
//
//            --- 5898765 ns (34.86%), 25 samples
//  [ 0] java.util.concurrent.atomic.AtomicInteger
//  [ 1] ru.hse.java.ConcurrencyExample$1.run
//  [ 2] java.lang.Thread.runWith
//  [ 3] java.lang.Thread.run
//
//--- 5807983 ns (34.32%), 22 samples
//  [ 0] java.util.concurrent.atomic.AtomicInteger
//  [ 1] ru.hse.java.ConcurrencyExample$2.run
//  [ 2] java.lang.Thread.runWith
//  [ 3] java.lang.Thread.run
//
//--- 5215388 ns (30.82%), 38 samples
//  [ 0] java.lang.Thread
//
//    ns  percent  samples  top
//  ----------  -------  -------  ---
//          11706748   69.18%       47  java.util.concurrent.atomic.AtomicInteger
//     5215388   30.82%       38  java.lang.Thread


}
