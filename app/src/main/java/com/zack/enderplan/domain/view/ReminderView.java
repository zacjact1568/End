package com.zack.enderplan.domain.view;

public interface ReminderView {

    void showInitialView(String contentStr, int titleBgColorInt);

    void onReminderDelayed(String nextReminderTime);

    void onReminderCanceled();

    void onPlanCompleted();
}
