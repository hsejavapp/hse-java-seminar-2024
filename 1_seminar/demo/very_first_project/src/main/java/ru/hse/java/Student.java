package ru.hse.java;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.time.ZonedDateTime;

/**
 * ФИО, валидация, тесты.
 * АльмаМатер студента - это средняя школа, из которой он выпустился
 * */
public class Student {
	private String full_name;
    private ZonedDateTime graduation;
	
	private static final Pattern NAME_MASK = Pattern.compile("[А-Яа-я ]+");

    static boolean nameIsValid(String name) {
        return NAME_MASK.matcher(name).matches();
    }
	
	public Student(String full_name, ZonedDateTime graduation) {
        Objects.requireNonNull(full_name);
        Objects.requireNonNull(graduation);
        if (!nameIsValid(full_name)) {
            throw new IllegalArgumentException(full_name + " has not valid by " + NAME_MASK + " pattern.");
        }
        this.full_name = full_name;
        this.graduation = graduation;
    }
	
	public String getName() {
        return full_name;
    }

    public ZonedDateTime getGraduation() {
        return graduation;
    }

    public void setName(String full_name) {
        this.full_name = full_name;
    }
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;

        return Objects.equals(full_name, student.full_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(full_name);
    }

    @Override
    public String toString() {
        return "Full name:" + full_name;
    }
}
