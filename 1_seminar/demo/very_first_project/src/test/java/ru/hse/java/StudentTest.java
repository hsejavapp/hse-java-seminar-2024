package ru.hse.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

public class StudentTest {
    @Test
    public void testStudentsHaveSameGradYear() {
        final Set<Student> EXAMPLE = new HashSet<>(Set.of(
                new Student("Паша", LocalDateTime.parse("2024-01-01T12:00:00").atZone(ZoneId.of("Europe/Moscow"))),
                new Student("Саша", LocalDateTime.parse("2024-01-01T12:00:00").atZone(ZoneId.of("Europe/Moscow"))),
                new Student("Ваня", LocalDateTime.parse("2024-01-01T12:00:00").atZone(ZoneId.of("Europe/Moscow"))),
                new Student("Катя", LocalDateTime.parse("2024-01-01T12:00:00").atZone(ZoneId.of("Europe/Moscow")))));
        Assertions.assertTrue(EXAMPLE.stream().allMatch((s) -> s.getGraduation().getYear() == 2024));
    }

    @Test
    public void testStudentsHaveDifferentGradYear() {
        final Set<Student> EXAMPLE = new HashSet<>(Set.of(
                new Student("Паша", LocalDateTime.parse("2024-01-01T12:00:00").atZone(ZoneId.of("Europe/Moscow"))),
                new Student("Саша", LocalDateTime.parse("2023-01-01T12:00:00").atZone(ZoneId.of("Europe/Moscow"))),
                new Student("Ваня", LocalDateTime.parse("2023-01-01T12:00:00").atZone(ZoneId.of("Europe/Moscow"))),
                new Student("Катя", LocalDateTime.parse("2024-01-01T12:00:00").atZone(ZoneId.of("Europe/Moscow")))));
        Assertions.assertFalse(EXAMPLE.stream().allMatch((s) -> s.getGraduation().getYear() == 2024));
    }
}
