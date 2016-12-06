package com.zack.enderplan.domain.view;

public interface ReminderView {

    void showInitialView(String content);

    void showToast(String msg);

    void enterPlanDetail(int position);

    void exitReminder();
}
