package com.zack.enderplan.view.contract;

public interface ReminderViewContract extends BaseViewContract {

    void showInitialView(String content);

    void showToast(String msg);

    void enterPlanDetail(int position);
}
