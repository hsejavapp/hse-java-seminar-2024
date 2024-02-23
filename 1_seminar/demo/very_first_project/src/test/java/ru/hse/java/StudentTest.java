package ru.hse.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

public class StudentTest {

    @Test
    public void testValidStudent() {
        String lastName = "Иванов";
        String firstName = "Иван";
        String middleName = "Иванович";
        String almaMaterName = "Школа №1";
        ZonedDateTime graduationDate = ZonedDateTime.now().minusDays(1);
        AlmaMater almaMater = new AlmaMater(almaMaterName, graduationDate);

        Student student = new Student(lastName, firstName, middleName, almaMater);

        Assertions.assertTrue(student.isValid());
    }

    @Test
    public void testInvalidStudent() {
        String lastName = "Ivanov";
        String firstName = "Ivan";
        String middleName = "Ivanovich";
        String almaMaterName = "Школа №1";
        ZonedDateTime graduationDate = ZonedDateTime.now().minusDays(1);

        try {
            AlmaMater almaMater = new AlmaMater(almaMaterName, graduationDate);
            Student student = new Student(lastName, firstName, middleName, almaMater);
            Assertions.fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            Assertions.assertTrue(e.getMessage().contains("not valid by"));
        }

        lastName = "Иванов";
        firstName = "Иван";
        middleName = "Иванович";
        graduationDate = ZonedDateTime.now().plusDays(1);

        try {
            AlmaMater almaMater = new AlmaMater(almaMaterName, graduationDate);
            Student student = new Student(lastName, firstName, middleName, almaMater);
            Assertions.fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            Assertions.assertTrue(e.getMessage().contains("not valid: Date must be in the past"));
        }
    }
}
