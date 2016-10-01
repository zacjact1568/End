package com.zack.enderplan.event;

/** 向导结束事件 */
public class GuideEndedEvent extends BaseEvent {

    /** 是否正常结束 */
    private boolean isEndNormally;

    public GuideEndedEvent(String eventSource, boolean isEndNormally) {
        super(eventSource);
        this.isEndNormally = isEndNormally;
    }

    public boolean isEndNormally() {
        return isEndNormally;
    }
}
