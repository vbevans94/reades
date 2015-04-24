package ua.org.cofriends.reades.ui.basic.tools;

/**
 * Author vbevans94.
 */
public class Positioned<T> {

    private final int position;

    private final T item;

    public Positioned(int position, T item) {
        this.position = position;
        this.item = item;
    }

    public int getPosition() {
        return position;
    }

    public T getItem() {
        return item;
    }
}
