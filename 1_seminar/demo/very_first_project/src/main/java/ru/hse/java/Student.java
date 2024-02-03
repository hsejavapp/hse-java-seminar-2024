package ru.hse.java;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ФИО, валидация, тесты.
 * АльмаМатер студента - это средняя школа, из которой он выпустился
 */
public class Student {

    private static final Pattern NAME_MASK = Pattern.compile("[А-Яа-я ]+");

    static boolean nameIsValid(String name) {
        return NAME_MASK.matcher(name).matches();
    }


    public Student(String name, AlmaMater alma, ZonedDateTime graduation) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(alma);
        Objects.requireNonNull(graduation);
        if (!nameIsValid(name)) {
            throw new IllegalArgumentException(name + " has not valid by " + NAME_MASK + " pattern.");
        }
        this.name = name;
        this.alma = alma;
        this.graduation = graduation;
    }

    public String getName() {
        return name;
    }

    public AlmaMater getAlmaMater() {
        return alma;
    }

    public ZonedDateTime getGraduation() {
        return graduation;
    }

    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student stud = (Student) obj;

        return Objects.equals(name, stud.name) &&
                Objects.equals(alma, stud.alma) &&
                Objects.equals(graduation, stud.graduation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, alma, graduation);
    }

    private final String name;
    private final AlmaMater alma;
    private final ZonedDateTime graduation;
}
