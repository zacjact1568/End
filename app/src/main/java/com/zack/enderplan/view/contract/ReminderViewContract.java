package com.zack.enderplan.view.contract;

public interface ReminderViewContract extends BaseViewContract {

    void showInitialView(String content, boolean hasDeadline, String deadline);

    void showToast(String msg);

    void enterPlanDetail(int position);
}
