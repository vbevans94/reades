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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Positioned<?> that = (Positioned<?>) o;

        if (position != that.position) return false;
        return !(item != null ? !item.equals(that.item) : that.item != null);

    }

    @Override
    public int hashCode() {
        int result = position;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Positioned{" +
                "position=" + position +
                ", item=" + item +
                '}';
    }
}
