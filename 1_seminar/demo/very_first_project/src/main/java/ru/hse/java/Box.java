package ru.hse.java;

import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class Box<T> {
    protected T value;
    protected Supplier<T> factory;


    public Box(T value) { this.value = value; }

    public Box(T value, Supplier<T> factory) {
        this.value = value;
        this.factory = factory;
    }


    public T newInstance() {
        return factory != null ? factory.get() : null;
    }

    public T get() {
        if (isPresent()) return value;
        throw new NoSuchElementException();
    }


    public void set(T value) { this.value = value; }


    public boolean isPresent() { return value != null; }

    static class NumberBox<X extends Number> extends Box<X> {
        public NumberBox(X value) {
            super(value);
        }

        NumberBox<X> sumWith(NumberBox<X> other) {
            return (NumberBox<X>) (new NumberBox<>(Double.valueOf(this.value.doubleValue() + other.value.doubleValue())));
        }
    }

    static class IntegerBox extends NumberBox<Integer> {
        public IntegerBox(Integer value) {
            super(value);
        }

        @Override
        public Integer get() {
            return super.get();
        }

        public int getAsInt() {
            return value;
        }

    }

    static class BuilderBox {
        BuilderBox setX(int x) {
            // this.x = x;
            return this;
        }
    }

    static class Box2<V, S extends Box2<V, S>> {
        V value;
        S set(V value) {
            this.value = value;
            return (S) this;
        }
    }

    static class StringBox extends Box2<String, StringBox> {
        void processValue() {/*...*/ };
    }


    public static void main(String[] args) {

        new StringBox().set("hello").processValue();

    }
}

