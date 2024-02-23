package ru.hse.java;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Реализовать методы
 */
public class AlmaMater {
    private static final Pattern AlmaMaterName_MASK = Pattern.compile("[А-Яа-я№0-9 ]+");

    static boolean AlmaMaterNameIsValid(String almaMaterName) {
        return AlmaMaterName_MASK.matcher(almaMaterName).matches();
    }

    static boolean DateOfGraduationIsValid(ZonedDateTime graduationDate) {
        return graduationDate.isBefore(ZonedDateTime.now());
    }

    private String almaMaterName;
    private ZonedDateTime graduationDate;

    public AlmaMater(String almaMaterName) {
        this(almaMaterName, null);
    }

    public AlmaMater(String almaMaterName, ZonedDateTime graduationDate) {
        Objects.requireNonNull(almaMaterName);
        Objects.requireNonNull(graduationDate);

        if (!AlmaMater.AlmaMaterNameIsValid(almaMaterName)) {
            throw new IllegalArgumentException(almaMaterName + " not valid by " + AlmaMaterName_MASK + " pattern.");
        }

        if (!AlmaMater.DateOfGraduationIsValid(graduationDate)) {
            throw new IllegalArgumentException(graduationDate + " not valid: Date must be in the past.");
        }

        this.almaMaterName = almaMaterName;
        this.graduationDate = graduationDate;
    }

    public boolean isValid() {
        return almaMaterName != null && AlmaMater.AlmaMaterNameIsValid(almaMaterName) &&
                graduationDate != null && AlmaMater.DateOfGraduationIsValid(graduationDate);
    }

    public String getAlmaMaterName() {
        return almaMaterName;
    }

    public void setAlmaMaterName(String almaMaterName) {
        this.almaMaterName = almaMaterName;
    }

    public ZonedDateTime getDateOfGraduation() {
        return graduationDate;
    }

    public void setDateOfGraduation(ZonedDateTime graduationDate) {
        this.graduationDate = graduationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlmaMater almaMater = (AlmaMater) o;
        return Objects.equals(almaMaterName, almaMater.almaMaterName) &&
                Objects.equals(graduationDate, almaMater.graduationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(almaMaterName, graduationDate);
    }

    @Override
    public String toString() {
        return "AlmaMater{" +
                "AlmaMaterName='" + almaMaterName + '\'' +
                ", graduationDate=" + graduationDate +
                '}';
    }

}
