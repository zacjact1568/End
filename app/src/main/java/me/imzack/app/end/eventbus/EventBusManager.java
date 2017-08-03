package me.imzack.app.end.eventbus;

import org.greenrobot.eventbus.EventBus;

public class EventBusManager {

    private EventBus mEventBus;

    private static EventBusManager ourInstance = new EventBusManager();

    private EventBusManager() {
        mEventBus = EventBus.builder().addIndex(new EventBusIndex()).build();
    }

    public static EventBusManager getInstance() {
        return ourInstance;
    }

    public EventBus getEventBus() {
        return mEventBus;
    }
}
