package ru.hse.java;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class School {
    private static final Pattern NAME_MASK = Pattern.compile("[А-Яа-яA-Za-z ]+");

    static boolean nameIsValid(String name) {
        return NAME_MASK.matcher(name).matches();
    }

    // fie
    private String name;
    private List<Student> students;

    public School(String name) {
        this(name, List.of());
    }

    public School(String name, List<Student> students) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(students);

        if (!School.nameIsValid(name)) {
            throw new IllegalArgumentException(name + " not valid by " + NAME_MASK + " pattern.");
        }

        if (students.stream().anyMatch(a -> !a.isValid())) {
            throw new IllegalArgumentException(students + " has not valid item by class Student pattern.");
        }

        this.name = name;
        this.students = students;
    }

    public String getName() {
        return name;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        School school = (School) o;

        return Objects.equals(name, school.name) &&
                Objects.equals(students, school.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "School{" +
                "name='" + name + '\'' +
                ", students=" + students +
                '}';
    }
}