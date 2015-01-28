package ua.org.cofriends.reades.utils;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class BusUtils {

    private static final Bus BUS = new Bus(ThreadEnforcer.ANY);
    private static final Handler UI_HANDLER = new UiTransmitter();

    public static void postToUi(Object event) {
        Message message = UI_HANDLER.obtainMessage();
        message.obj = event;
        UI_HANDLER.sendMessage(message);
    }

    public static void post(Object event) {
        BUS.post(event);
    }

    public static void register(Object subscriber) {
        BUS.register(subscriber);
    }

    public static void unregister(Object subscriber) {
        BUS.unregister(subscriber);
    }

    private static class UiTransmitter extends Handler {

        private UiTransmitter() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            BUS.post(msg.obj);
        }
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
