package ua.org.cofriends.reades.utils;

public final class Events {

    public static class RemoveEvent extends BusUtils.Event<Object> {

        public RemoveEvent(Object object) {
            super(object);
        }
    }
}
