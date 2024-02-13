package ru.hse.java;

import java.time.ZonedDateTime;

/**
 * Реализовать методы
 * */
@FunctionalInterface
public interface AlmaMater {

    ZonedDateTime getDateOfGraduation(Student student);

}
