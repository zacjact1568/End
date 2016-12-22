package com.zack.enderplan.view.contract;

public interface FirstPlanViewContract extends BaseViewContract {

    void showInitialView(boolean shouldShowEnterAnimation);

    void onDetectedEmptyContent();

    void onFirstPlanCreated();
}
