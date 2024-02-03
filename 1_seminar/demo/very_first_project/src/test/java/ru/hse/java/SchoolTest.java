package ru.hse.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Test, which checks student group has all alma maters with graduation date
 */
public class SchoolTest {

    @Test
    public void testNameIsValid() {
        String name = "И";
        Assertions.assertTrue(School.nameIsValid(name));
    }

    @Test
    public void testNameIsInvalid() {
        String name = "I";
        Assertions.assertFalse(School.nameIsValid(name));
    }

    public boolean checkAllStudentsHaveAlmaMaters(List<Student> students) {
        return students.stream().noneMatch(a -> a.getDateOfGraduation() == null);
    }

    @Test
    public void testAlmaMaterOneOfTheStudentsInSchoolIsNull() {
        // инициализация школы
        ZonedDateTime date1 = ZonedDateTime.of(2025, 1, 1, 1, 1, 1, 0, ZoneId.systemDefault());
        ZonedDateTime date2 = ZonedDateTime.of(2025, 1, 1, 1, 1, 1, 0, ZoneId.systemDefault());
        Student student1 = new Student("Ли", date1);
        Student student2 = new Student("Лиля", date2);
        Student student3 = new Student("Лилиана", null);
        School school = new School("HSE", List.of(student1, student2, student3));
        Assertions.assertFalse(checkAllStudentsHaveAlmaMaters(school.getStudents()));
    }

    @Test
    public void testAlmaMatersOfAllStudentsInSchoolIsOk() {
        // инициализация школы
        ZonedDateTime date1 = ZonedDateTime.of(2025, 1, 1, 1, 1, 1, 0, ZoneId.systemDefault());
        ZonedDateTime date2 = ZonedDateTime.of(2025, 1, 1, 1, 1, 1, 0, ZoneId.systemDefault());
        Student student1 = new Student("Ли", date1);
        Student student2 = new Student("Лиля", date2);
        School school = new School("HSE", List.of(student1, student2));

        Assertions.assertTrue(checkAllStudentsHaveAlmaMaters(school.getStudents()));
    }

}
