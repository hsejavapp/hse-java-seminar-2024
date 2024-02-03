package ru.hse.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Test, which checks student group has all almamaters with graduation date
 * */
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

    @Test
    public void testStudentsHaveSameAlma() {
        AlmaMater gymn = new AlmaMater("Москва", "Гимназия", 1);
        final Set<Student> EXAMPLE = new HashSet<>(Set.of(
                new Student("Катя", gymn, ZonedDateTime.now()),
                new Student("Вася", gymn, ZonedDateTime.now()),
                new Student("Петя", gymn, ZonedDateTime.now()),
                new Student("Настя", gymn, ZonedDateTime.now())));
        for (var stud : EXAMPLE) {
            Assertions.assertEquals(stud.getAlmaMater(), gymn);
        }
    }

    @Test
    public void testStudentsHaveDifferentAlmas() {
        AlmaMater gymn = new AlmaMater("Москва", "Гимназия", 1);
        AlmaMater lyceum = new AlmaMater("Москва", "Лицей", 1);
        final Set<Student> EXAMPLE = new HashSet<>(Set.of(
                new Student("Катя", gymn, ZonedDateTime.now()),
                new Student("Вася", gymn, ZonedDateTime.now()),
                new Student("Петя", lyceum, ZonedDateTime.now()),
                new Student("Настя", lyceum, ZonedDateTime.now())));
        Assertions.assertFalse(EXAMPLE.stream().allMatch((s) -> s.getAlmaMater().equals(gymn)));
    }

    @Test
    public void testStudentsHaveSameGradYear() {
        AlmaMater gymn = new AlmaMater("Москва", "Гимназия", 1);
        AlmaMater lyceum = new AlmaMater("Москва", "Лицей", 1);
        final Set<Student> EXAMPLE = new HashSet<>(Set.of(
                new Student("Катя", gymn, LocalDateTime.parse("2019-05-31T12:15:30").atZone(ZoneId.of("Europe/Moscow"))),
                new Student("Вася", gymn, LocalDateTime.parse("2019-05-31T12:15:30").atZone(ZoneId.of("Europe/Moscow"))),
                new Student("Петя", lyceum, LocalDateTime.parse("2019-05-31T12:15:30").atZone(ZoneId.of("Europe/Moscow"))),
                new Student("Настя", lyceum, LocalDateTime.parse("2019-05-31T12:15:30").atZone(ZoneId.of("Europe/Moscow")))));
        Assertions.assertTrue(EXAMPLE.stream().allMatch((s) -> s.getGraduation().getYear() == 2019));
    }

    @Test
    public void testStudentsHaveDifferentGradYear() {
        AlmaMater gymn = new AlmaMater("Москва", "Гимназия", 1);
        AlmaMater lyceum = new AlmaMater("Москва", "Лицей", 1);
        final Set<Student> EXAMPLE = new HashSet<>(Set.of(
                new Student("Катя", gymn, LocalDateTime.parse("2019-05-31T12:15:30").atZone(ZoneId.of("Europe/Moscow"))),
                new Student("Вася", gymn, LocalDateTime.parse("2020-05-31T12:15:30").atZone(ZoneId.of("Europe/Moscow"))),
                new Student("Петя", lyceum, LocalDateTime.parse("2021-05-31T12:15:30").atZone(ZoneId.of("Europe/Moscow"))),
                new Student("Настя", lyceum, LocalDateTime.parse("2022-05-31T12:15:30").atZone(ZoneId.of("Europe/Moscow")))));
        Assertions.assertFalse(EXAMPLE.stream().allMatch((s) -> s.getGraduation().getYear() == 2019));
    }
}
