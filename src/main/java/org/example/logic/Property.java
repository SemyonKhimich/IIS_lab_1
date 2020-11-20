package org.example.logic;

public class Property<T, U> {
    private final T key;
    private final U value;

    public Property(T key, U value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public U getValue() {
        return value;
    }
}
