package ru.hse.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

public class StudentTest {

    @Test
    public void testValidStudent() {
        ZonedDateTime graduationDate = ZonedDateTime.now().plusYears(4);
        AlmaMater almaMater = new Graduation(graduationDate, "1");

        Student student = new Student("Иван Иванов", almaMater);

        Assertions.assertEquals("Иван Иванов", student.getFullName());
        Assertions.assertEquals(almaMater, student.getAlmaMater());
        Assertions.assertEquals(graduationDate, student.getAlmaMater().getDateOfGraduation());
    }

    @Test
    public void testInvalidFullName() {
        AlmaMater almaMater = new Graduation(ZonedDateTime.now().plusYears(4), "1");

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Student("", almaMater));
    }

    @Test
    public void testNullFullName() {
        AlmaMater almaMater = new Graduation(ZonedDateTime.now().plusYears(4), "1");

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Student(null, almaMater));
    }

    @Test
    public void testNullAlmaMater() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Student("Иван Иванов", null));
    }
}
