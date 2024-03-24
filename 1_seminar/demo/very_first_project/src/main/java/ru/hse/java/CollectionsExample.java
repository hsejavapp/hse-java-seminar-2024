package ru.hse.java;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.openjdk.jol.info.GraphLayout;

import java.lang.management.ThreadMXBean;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

/**
 * @author EgorLitvinenko
 * */
public class CollectionsExample {

    record Dish(String name,
                CollectionsExample.Dish.Type type,
                int calories,
                CollectionsExample.Dish.CaloricLevel caloricLevel)
    {
        enum CaloricLevel {NORMAL, DIET}

        enum Type {FISH, MEAT, OTHER}
    }

    public static void main(String[] args) throws Exception {
        System.out.println("in : " + args);
        main(args);

        LongAdder sum = new LongAdder();
        int count = 1_000_000;
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            CompletableFuture.runAsync(() -> {
                sum.add(ThreadLocalRandom.current().nextLong());
                if (ThreadLocalRandom.current().nextDouble() < 0.7) {
                    try {
                        Thread.sleep(10);
                    } catch (Throwable t) {}
                }
                latch.countDown();
            }, ForkJoinPool.commonPool());
        }
        latch.await();
        System.out.println(sum.sum());



//        var menu = List.of(
//                new Dish("Sharlotka", Dish.Type.OTHER, 300, Dish.CaloricLevel.NORMAL),
//                new Dish("Varenya kuriza", Dish.Type.MEAT, 200, Dish.CaloricLevel.DIET),
//                new Dish("Kebab kuriza", Dish.Type.MEAT, 200, Dish.CaloricLevel.DIET),
//                new Dish("Yaichniza", Dish.Type.OTHER, 400, Dish.CaloricLevel.DIET),
//                new Dish("Losos", Dish.Type.FISH, 250, Dish.CaloricLevel.DIET),
//                new Dish("Bolshoy buterbrod (kuriza)", Dish.Type.OTHER, 250, Dish.CaloricLevel.NORMAL)
//        );
//
//        var dishTypeCount = menu.stream()
//                .filter(dish -> dish.calories > 200)
//                .collect(
//                        groupingBy(dish -> {
//                            if (dish.name.contains("kuriza")) {
//                                return "kuriza";
//                            } else {
//                                return dish.type.name();
//                            }
//                        }, counting())
//                );
//        System.out.println(dishTypeCount);
//
//
//
//        var dietDishes =  menu.stream()
//                .collect(
//                        groupingBy(Dish::type,
//                            collectingAndThen(minBy(comparingInt(dish -> {
//                                        if (dish.calories > 300) {
//                                            throw new IllegalArgumentException("300");
//                                        }
//                                        return dish.calories();
//                                    })),
//                            opt -> {
//                                throw new IllegalArgumentException("ok");
//                            }))
//                );
//        System.out.println(dietDishes);
//
//
//        var result = Stream.iterate(
//                        new int[]{0, 1},
//                        a -> new int[]{a[1], a[0] + a[1]})
//                .parallel()
//                .takeWhile(x -> {
////                    System.out.println(String.format("x[0] = %d, x[1] = %d, sum = %d", x[0], x[1], x[0] + x[1]));
//                    return x[0] + x[1] > 0;
//                })
//                .peek(x -> {
//                    if (x[0] > 10_000) {
//                        throw new IllegalStateException("x");
//                    }
//                })
//                .mapToInt(a -> {
////                    System.out.println("inside stream");
//                    return a[1];
//                })
//                .dropWhile(a -> a < 1_000_000)
//                .mapToObj(a -> a)
//                .collect(Collectors.toMap(a -> a, a -> a * a));
//        System.out.println(result);
//        System.out.println(701408733 + 1134903170);
//        List<Integer> list = new ArrayList<>(2);
//        list.add(1);
//        list.add(2);
//        System.out.println(GraphLayout.parseInstance(list).totalSize());
//        Integer mean = null;
//
//        Predicate<Integer> predicate = x -> x > mean;
//        var greaterThenMean = list.parallelStream().filter(predicate).toList();
////        var greaterThenMean = new ArrayList<>(list.size());
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i) > mean) {
//                greaterThenMean.add(list.get(i));
//            }
//        }
//
//        Arrays.stream(new int[] {1});
//
//        IntList fastUtil = new IntArrayList(list);
//        org.eclipse.collections.api.list.primitive.IntList eclipse = new
//                org.eclipse.collections.impl.list.mutable.primitive.IntArrayList(list.stream().mapToInt(a -> a).toArray());
//        System.out.println(GraphLayout.parseInstance(fastUtil).totalSize());
//        System.out.println(GraphLayout.parseInstance(eclipse).totalSize());
//



    }
}
