package ru.hse.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Test, which checks student group has all almamaters with graduation date
 */
public class SchoolTest {

    @Test
    public void testValidStudentsCmp() {
        AlmaMater almaMater1 = new AlmaMater("Школа №1", ZonedDateTime.now().minusYears(5));
        AlmaMater almaMater2 = new AlmaMater("Школа №1", ZonedDateTime.now().minusYears(3));

        List<Student> students_for_school = List.of(new Student("Иванов", "Иван", "Иванович", almaMater1));
        List<Student> students_for_cmp = List.of(new Student("Иванов", "Иван", "Иванович", almaMater2));

        School school = new School("HSE", students_for_school);
        Assertions.assertNotEquals(students_for_cmp, school.getStudents());
    }

    @Test
    public void testInvalidSchoolName() {
        List<Student> students = List.of(
                new Student("Иванов", "Иван", "Иванович",
                        new AlmaMater("Школа №1", ZonedDateTime.now().minusYears(5)))
        );
        Assertions.assertThrows(IllegalArgumentException.class, () -> new School("", students));
    }
}
