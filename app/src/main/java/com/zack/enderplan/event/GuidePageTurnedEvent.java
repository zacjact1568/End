package com.zack.enderplan.event;

/** 向导界面中的翻页事件 */
public class GuidePageTurnedEvent {

    public static final int PAGE_WELCOME = 0;
    public static final int PAGE_FIRST_PLAN = 1;

    private int page;

    public GuidePageTurnedEvent(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }
}
