package ru.hse.java;

import java.time.ZonedDateTime;
import java.util.Objects;

public class Graduation implements AlmaMater {
    private ZonedDateTime graduationYear = null;
    private String schoolName;

    public Graduation(ZonedDateTime graduationYear, String schoolName) {
        Objects.requireNonNull(graduationYear);
        this.graduationYear = graduationYear;
        this.schoolName = schoolName;
    }

    @Override
    public ZonedDateTime getDateOfGraduation() {
        return graduationYear;
    }

    public String getSchoolName() {
        return schoolName;
    }
}
