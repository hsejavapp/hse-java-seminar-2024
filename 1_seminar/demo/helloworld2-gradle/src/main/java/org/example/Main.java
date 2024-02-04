package org.example;

import org.openjdk.jol.info.GraphLayout;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        var myMap = new HashMap<>() {
            static {
                System.out.println("Static init Main$1");
            }
            {
                put("a", "b");
                put("a", "c");
            }
        };
        System.out.println(GraphLayout.parseInstance(myMap).totalSize());

        final var immutableMap = Map.of("a", "b", "b", "c");

        System.out.println(myMap.getClass());
        System.out.println(GraphLayout.parseInstance(immutableMap).totalSize());
        System.out.println(immutableMap.getClass());
    }
}