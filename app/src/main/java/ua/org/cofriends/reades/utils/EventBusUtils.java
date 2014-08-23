package ua.org.cofriends.reades.utils;

import de.greenrobot.event.EventBus;

public class EventBusUtils {

    public static EventBus getBus() {
        return EventBus.getDefault();
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
