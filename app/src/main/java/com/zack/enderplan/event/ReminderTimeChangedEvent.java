package com.zack.enderplan.event;

public class ReminderTimeChangedEvent {

    public int changingPosition;

    public ReminderTimeChangedEvent(int changingPosition) {
        this.changingPosition = changingPosition;
    }
}
