package ru.hse.java;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Egor Litvinenko
 */
public class ByteBufferPoolExample {

    static class Blackhole {
        void doSomething(ByteBuffer buffer, int capacity) {
            for (int i = 0; i + 8 < capacity; i += 8) {
                buffer.putLong(ThreadLocalRandom.current().nextLong());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Simple example:");
        simpleExample();
        System.out.println("Pooled example:");
        pooledExample();
    }

    static void pooledExample() {
        int capacity = 1024;
        var pool = new SimpleObjectPool<>(10, 1000,
                () -> ByteBuffer.allocate(capacity),
                ByteBuffer::clear);

        var blackhole = new Blackhole();
        var statistics = new DescriptiveStatistics();

        for (int i = 0; i < 100; i++) {
            var memoryBefore = Runtime.getRuntime().freeMemory();
            var bb = pool.borrow();
            try {
                blackhole.doSomething(bb, capacity);
            } finally {
                pool.offer(bb);
            }
            var memoryAfter = Runtime.getRuntime().freeMemory();
            statistics.addValue(memoryAfter - memoryBefore);
        }
        sout(statistics);
    }

    static void simpleExample() {
        int capacity = 1024;
        var blackhole = new Blackhole();
        var statistics = new DescriptiveStatistics();

        for (int i = 0; i < 100; i++) {
            var memoryBefore = Runtime.getRuntime().freeMemory();
            var bb = ByteBuffer.allocateDirect(capacity);
            blackhole.doSomething(bb, capacity);
            System.gc();
            var memoryAfter = Runtime.getRuntime().freeMemory();
            statistics.addValue(memoryAfter - memoryBefore);
        }

        sout(statistics);
    }

    static void sout(DescriptiveStatistics statistics) {
        System.out.println("Stastitics: ");
        System.out.println("Min = " + Double.valueOf(statistics.getMin()).longValue());
        System.out.println("Max = " + Double.valueOf(statistics.getMax()).longValue());
        System.out.println("Mean = " + Double.valueOf(statistics.getMean()).longValue());
    }

}
