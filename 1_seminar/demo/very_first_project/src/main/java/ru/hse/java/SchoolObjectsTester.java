package ru.hse.java;

import java.util.*;

public class SchoolObjectsTester {
    private static final Set<School> EXAMPLE = new HashSet<>(Set.of(
            new School("HSE", List.of()),
            new School("HSE", List.of()),
            new School("HSE", List.of()),
            new School("HSE", List.of())));
    private static Set<School> schools = new HashSet<>();
    private Set<School> schools2 = new HashSet<>();

    // repeat without equals() and hashCode() methods in the 'School' class
    static {
        schools.addAll(EXAMPLE);
        System.out.println("Static initializer");
    }

    {
        schools2.addAll(EXAMPLE);
        System.out.println("Object initializer");
    }


    public static void main(String[] args) {

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("Handled in thread UEH");
                e.printStackTrace();
            }
        });
        try {
            var school1 = new School("HSE", List.of());
            System.out.println("Our school: " + school1);
        } catch (NullPointerException iae) {
            iae.printStackTrace();
            System.out.println("Understand and sorry");
        }
    }
}