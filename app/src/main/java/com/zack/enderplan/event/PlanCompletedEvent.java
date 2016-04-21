package com.zack.enderplan.event;

public class PlanCompletedEvent {

    public int position;

    public PlanCompletedEvent(int position) {
        this.position = position;
    }
}
