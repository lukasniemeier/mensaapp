package de.lukasniemeier.mensa.ui.adapter;

/**
 * Created on 19.09.13.
 */
public class CardState<T> {
    private final T value;
    private boolean isTurned;

    public CardState(T value) {
        this(value, false);
    }

    public CardState(T value, boolean turned) {
        this.value = value;
        isTurned = turned;
    }

    public boolean isTurned() {
        return isTurned;
    }

    public void turn() {
        isTurned = !isTurned;
    }

    public T getValue() {
        return value;
    }
}
