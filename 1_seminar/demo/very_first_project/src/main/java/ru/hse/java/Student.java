package ru.hse.java;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ФИО, валидация, тесты.
 * АльмаМатер студента - это средняя школа, из которой он выпустился
 * */
public class Student {

    private String fullName;
    private AlmaMater almaMater;

    private static final Pattern NAME_MASK = Pattern.compile("[А-Яа-я ]+");

    public Student(String fullName, AlmaMater almaMater) {
        if (isValidFullName(fullName)) {
            this.fullName = fullName;
        } else {
            throw new IllegalArgumentException("Invalid full name: " + fullName);
        }
        if (almaMater != null) {
            this.almaMater = almaMater;
        } else {
            throw new IllegalArgumentException("Null almaMater");
        }
    }

    public String getFullName() {
        return fullName;
    }

    public AlmaMater getAlmaMater() {
        return almaMater;
    }

    private boolean isValidFullName(String fullName) {
        return fullName != null && !fullName.trim().isEmpty() && NAME_MASK.matcher(fullName).matches();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;

        return Objects.equals(fullName, student.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName);
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + fullName + '\'' +
                ", almaMater=" + almaMater +
                '}';
    }
}
