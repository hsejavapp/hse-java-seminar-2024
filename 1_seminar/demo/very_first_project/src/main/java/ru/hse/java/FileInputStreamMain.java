package ru.hse.java;

import org.jctools.queues.SpscGrowableArrayQueue;
import org.jctools.queues.atomic.SpscAtomicArrayQueue;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * @author Egor Litvinenko
 */
public class FileInputStreamMain {

    public static void main(String[] args) throws Exception {
        var path = Paths.get("/home/egor/Documents/projects/hse/hse-java-seminar-2024/1_seminar/demo/" +
                "very_first_project/src/main/java/ru/hse/java/ByteBufferPoolExample.java");

//        try (var inputStream = new FileInputStream(path.toFile());
//             var outputStream = new FileOutputStream(Paths.get(path + ".copy").toFile()))
//        {
//            inputStream.transferTo(outputStream);
////            int read;
////            while ((read = inputStream.read(buffer)) > 0) {
////                outputStream.write(buffer, 0, read);
////            }
//            outputStream.flush();
//        }


        int capacity = 1024;
        var pool = new SimpleObjectPool<>(10, 1000,
                () -> ByteBuffer.allocate(capacity),
                ByteBuffer::clear);
        ByteBuffer end = ByteBuffer.allocate(1);
//        final ConcurrentLinkedQueue<ByteBuffer> buffers = new ConcurrentLinkedQueue<>();
        final SpscGrowableArrayQueue<ByteBuffer> buffers = new SpscGrowableArrayQueue<>(16, 8192);
        new Thread(() -> {
            try (var inputStream = new FileInputStream(path.toFile())) {
                int read;
                var bb = pool.borrow();
                while ((read = inputStream.read(bb.array())) > 0) {
                    bb.limit(read);
                    buffers.offer(bb);
                    bb = pool.borrow();
                }
            } catch (Throwable throwable) {
            } finally {
                buffers.add(end);
            }
        }).start();

        new Thread(() -> {
            try (var output = new FileOutputStream(Paths.get(path + ".copy").toFile())) {
                do {
                    ByteBuffer buffer;
                    while ((buffer = buffers.poll()) != null) {
                        if (buffer == end) {
                            output.flush();
                            return;
                        }
                        output.write(buffer.array(), 0, buffer.limit());
                        pool.offer(buffer);
                    }
                    LockSupport.parkNanos(10);
                } while (!Thread.currentThread().isInterrupted());
            } catch (Throwable throwable) {
            }
        }).start();
    }

}
