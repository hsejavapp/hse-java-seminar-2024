package ru.hse.java;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ФИО, валидация, тесты.
 * АльмаМатер студента - это средняя школа, из которой он выпустился
 */
public class Student {
    private static final Pattern Initials_MASK = Pattern.compile("[А-Яа-я-]+");

    static boolean InitialIsValid(String initial) {
        return Initials_MASK.matcher(initial).matches();
    }

    private String lastName;
    private String firstName;
    private String middleName;
    private AlmaMater almaMater;

    public Student(String lastName, String firstName, String middleName) {
        this(lastName, firstName, middleName, null);
    }

    public Student(String lastName, String firstName, String middleName, AlmaMater almaMater) {
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(middleName);
        Objects.requireNonNull(almaMater);

        if (!Student.InitialIsValid(lastName)) {
            throw new IllegalArgumentException(lastName + " not valid by " + Initials_MASK + " pattern.");
        }
        if (!Student.InitialIsValid(firstName)) {
            throw new IllegalArgumentException(firstName + " not valid by " + Initials_MASK + " pattern.");
        }
        if (!Student.InitialIsValid(middleName)) {
            throw new IllegalArgumentException(middleName + " not valid by " + Initials_MASK + " pattern.");
        }


        if (!AlmaMater.AlmaMaterNameIsValid(almaMater.getAlmaMaterName())) {
            throw new IllegalArgumentException(almaMater.getAlmaMaterName() + " not valid by class AlmaMater pattern.");
        }

        if (!AlmaMater.DateOfGraduationIsValid(almaMater.getDateOfGraduation())) {
            throw new IllegalArgumentException(almaMater.getDateOfGraduation() + " not valid: Date must be in the past.");
        }

        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.almaMater = almaMater;
    }

    public boolean isValid() {
        return lastName != null && Student.InitialIsValid(lastName) &&
                firstName != null && Student.InitialIsValid(firstName) &&
                middleName != null && Student.InitialIsValid(middleName) &&
                almaMater != null && almaMater.isValid();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public AlmaMater getAlmaMater() {
        return almaMater;
    }

    public void setAlmaMater(AlmaMater almaMater) {
        this.almaMater = almaMater;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(lastName, student.lastName) &&
                Objects.equals(firstName, student.firstName) &&
                Objects.equals(middleName, student.middleName) &&
                Objects.equals(almaMater, student.almaMater);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastName, firstName, middleName);
    }

    @Override
    public String toString() {
        return "Student{" +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", almaMater='" + almaMater + '\'' +
                '}';
    }
}
