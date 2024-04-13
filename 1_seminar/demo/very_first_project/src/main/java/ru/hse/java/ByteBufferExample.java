package ru.hse.java;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Egor Litvinenko
 */
public class ByteBufferExample {

    public static void main(String[] args) {
        // _________________ WRITE _________________

        Map<String, String> map = Map.of("1", "2");
        for (var entry : map.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            System.out.println("key = " + key + ", val = " + value);
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        System.out.println("Initial state: ");
        System.out.println("capacity = " + buffer.capacity());
        System.out.println("limit = " + buffer.limit());
        System.out.println("mark = " + buffer.mark());
        System.out.println("position = " + buffer.position());

        for (int i = 0; i < 10; i++) {
            buffer.putInt(i); // 4 bytes
            if (i == 5) {
                buffer.mark();
            }
        }

        System.out.println("Written 40 bytes: ");
        System.out.println("capacity = " + buffer.capacity());
        System.out.println("limit = " + buffer.limit());
        System.out.println("position = " + buffer.position());

        // _________________ READ FROM MARK (TOO MANY) _________________
//        buffer.reset();
//        System.out.println("Read from mark");
//        while (buffer.hasRemaining()) {
//            int i = buffer.getInt();
//            System.out.println("i = " + i);
//        }

        // _________________ READ WRITTEN BYTES _________________
        System.out.println("capacity = " + buffer.capacity());
        System.out.println("limit = " + buffer.limit());
        System.out.println("position = " + buffer.position());
//        buffer.limit(40);
        buffer.flip();
        System.out.println("Read written bytes");
        while (buffer.hasRemaining()) {
            int i = buffer.getInt();
            System.out.println("i = " + i);
        }
    }

}
