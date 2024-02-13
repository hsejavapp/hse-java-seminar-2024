package ru.hse.java;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.time.ZonedDateTime;

public class School implements AlmaMater {
    private static final Pattern NAME_MASK = Pattern.compile("[А-Яа-я ]+");
    static boolean nameIsValid(String name) {
        return NAME_MASK.matcher(name).matches();
    }
    // fie
    private String name;
    private List<Student> students;

    public School(String name) {
        this(name, List.of());
    }

    public School(String name, List<Student> students) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(students);
        if (students.stream().anyMatch(a -> !School.nameIsValid(a.getName()))) {
            throw new IllegalArgumentException(students + " has not valid by " + NAME_MASK + " pattern.");
        }
        this.name = name;
        this.students = students;


        Predicate<String> added = students::add;
        if (added.test("1")) {
            System.out.println("added");
        }
        Consumer<String> actionAdd = students::add;
        actionAdd.accept("2");
        students.sort((s1, s2) -> (s1.length() - s2.length()));
        students.sort(Comparator.comparingInt(String::length));
        students.sort(String::compareToIgnoreCase);
        students.sort(Comparator.naturalOrder());
    }

    public String getName() {
        return name;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        School school = (School) o;

        return Objects.equals(name, school.name) &&
                Objects.equals(students, school.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "School{" +
                "name='" + name + '\'' +
                ", students=" + students +
                '}';
    }
	
	public ZonedDateTime getDateOfGraduation(Student student) {
		return student.getGraduation();
	}
}