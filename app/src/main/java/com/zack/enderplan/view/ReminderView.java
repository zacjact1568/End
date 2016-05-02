package com.zack.enderplan.view;

public interface ReminderView {

    void showInitialView(String contentStr, int titleBgColorInt);

    void onReminderDelayed(String nextReminderTime);

    void onReminderCanceled();

    void onPlanCompleted(String content);
}
