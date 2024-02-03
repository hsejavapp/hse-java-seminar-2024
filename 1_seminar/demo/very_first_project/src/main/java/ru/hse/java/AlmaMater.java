package ru.hse.java;

import java.util.Objects;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Реализовать методы
 */
public class AlmaMater {

    //    ZonedDateTime getDateOfGraduation(); // этот метод перенес в класс студента

    private static final String[] name_array = new String[]{"Гимназия", "Школа", "Лицей"};
    private static final Pattern CITY_MASK = Pattern.compile("[А-Яа-я ]+");

    static boolean nameIsValid(String name) {
        return Arrays.asList(name_array).contains(name);
    }

    static boolean cityIsValid(String city) {
        return CITY_MASK.matcher(city).matches();
    }

    public AlmaMater(String city, String name, Integer number) {
        Objects.requireNonNull(city);
        Objects.requireNonNull(name);
        Objects.requireNonNull(number);
        if (!nameIsValid(name)) {
            throw new IllegalArgumentException(name + "is not acceptable. Only \"Гимназия\", \"Школа\", \"Лицей\" are allowed");
        }
        if (!cityIsValid(name)) {
            throw new IllegalArgumentException( city + " has not valid by " + CITY_MASK + " pattern.");
        }
        this.name = name;
        this.city = city;
        this.number = number;
    }

    public String getFullName() {
        return name +  " №" + number.toString() + " г. " + city;
    }

    public String toString() {
        return getFullName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AlmaMater alma = (AlmaMater) obj;

        return Objects.equals(name, alma.name) &&
                Objects.equals(city, alma.city) &&
                Objects.equals(number, alma.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, city, number);
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public Integer getNumber() {
        return number;
    }

    private final String name;
    private final String city;
    private final Integer number;
}
