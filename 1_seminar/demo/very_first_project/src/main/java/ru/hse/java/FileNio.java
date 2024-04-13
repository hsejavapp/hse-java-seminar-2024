package ru.hse.java;

import java.io.IOException;
import java.nio.file.*;

/**
 * @author Egor Litvinenko
 */
public class FileNio {

    public static void main(String[] args) throws IOException {
        Files.list(
                Path.of("/home/egor/Documents/projects/hse/hse-java-seminar-2024/1_seminar/demo/very_first_project/src/main/java/ru/hse/java"))
                .forEach(System.out::println);

        var path = Paths.get(
                "/home/egor/Documents/projects/hse/hse-java-seminar-2024/1_seminar/demo/very_first_project/src/main/java/ru/hse/java"
        );

        System.out.println(path.getFileName());
        System.out.println(path.resolve("SimpleObjectPool.java"));
        System.out.println(Files.list(path).filter(fileName -> fileName.endsWith(".java")).toList());
        System.out.println(Files.list(path).filter(fileName -> fileName.startsWith("Concu")).toList());

        new Thread(
                new Runnable() {
                    final WatchKey watchKey = path.register(FileSystems.getDefault().newWatchService(), StandardWatchEventKinds.ENTRY_MODIFY);
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
                            watchKey.pollEvents().stream().forEach(event -> {
                                System.out.println(event);
                            });
                        }
                    }
                }
        ).start();



    }

}
