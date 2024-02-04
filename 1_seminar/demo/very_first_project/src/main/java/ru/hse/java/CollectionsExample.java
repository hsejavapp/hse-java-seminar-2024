package ru.hse.java;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.openjdk.jol.info.GraphLayout;

import java.util.ArrayList;
import java.util.List;

public class CollectionsExample {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>(2);
        list.add(1);
        list.add(2);
        System.out.println(GraphLayout.parseInstance(list).totalSize());

        IntList fastUtil = new IntArrayList(list);
        org.eclipse.collections.api.list.primitive.IntList eclipse = new
                org.eclipse.collections.impl.list.mutable.primitive.IntArrayList(list.stream().mapToInt(a -> a).toArray());
        System.out.println(GraphLayout.parseInstance(fastUtil).totalSize());
        System.out.println(GraphLayout.parseInstance(eclipse).totalSize());




    }
}
