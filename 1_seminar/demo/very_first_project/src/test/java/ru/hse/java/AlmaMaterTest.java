package ru.hse.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

public class AlmaMaterTest {

    @Test
    public void testAlmaMaterNameIsValid() {
        String validName = "Школа №1";
        Assertions.assertTrue(AlmaMater.AlmaMaterNameIsValid(validName));

        String invalidName = "School №1";
        Assertions.assertFalse(AlmaMater.AlmaMaterNameIsValid(invalidName));

        invalidName = "Школа #1";
        Assertions.assertFalse(AlmaMater.AlmaMaterNameIsValid(invalidName));
    }

    @Test
    public void testDateOfGraduationIsValid() {
        ZonedDateTime futureDate = ZonedDateTime.now().plusDays(1);
        Assertions.assertFalse(AlmaMater.DateOfGraduationIsValid(futureDate));

        ZonedDateTime pastDate = ZonedDateTime.now().minusDays(1);
        Assertions.assertTrue(AlmaMater.DateOfGraduationIsValid(pastDate));
    }
}
