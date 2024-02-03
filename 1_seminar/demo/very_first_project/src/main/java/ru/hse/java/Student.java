package ru.hse.java;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ФИО, валидация, тесты.
 * АльмаМатер студента - это средняя школа, из которой он выпустился
 */
public class Student implements AlmaMater {
    private static final Pattern NAME_MASK = Pattern.compile("[А-Яа-я ]+");
    final private String name;
    final private ZonedDateTime dateOfGraduation;

    public Student(String name, ZonedDateTime dateOfGraduation) {
        Objects.requireNonNull(name);
        if (!Student.nameIsValid(name)) {
            throw new IllegalArgumentException(name + " has not valid by " + NAME_MASK + " pattern.");
        }
        this.name = name;
        this.dateOfGraduation = dateOfGraduation;
    }

    static boolean nameIsValid(String name) {
        return NAME_MASK.matcher(name).matches();
    }

    public String getName() {
        return name;
    }


    @Override
    public ZonedDateTime getDateOfGraduation() {
        return dateOfGraduation;
    }

    @Override
    public String toString() {
        return "Student{name='" + name + '\'' + '}';
    }
}
