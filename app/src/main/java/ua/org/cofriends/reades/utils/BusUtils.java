package ua.org.cofriends.reades.utils;

import de.greenrobot.event.EventBus;

public class BusUtils {

    public static void post(Object event) {
        EventBus.getDefault().post(event);
    }

    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    public static class Event<T> {

        private final T object;

        public Event(T object) {
            this.object = object;
        }

        public T getData() {
            return object;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "object=" + object +
                    '}';
        }
    }
}
