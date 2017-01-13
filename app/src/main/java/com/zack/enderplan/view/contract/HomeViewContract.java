package com.zack.enderplan.view.contract;

public interface HomeViewContract extends BaseViewContract {

    void showInitialView(String ucPlanCount);

    void changeUcPlanCount(String ucPlanCount);

    void closeDrawer();

    void changeFabVisibility(boolean isVisible);

    void showFragment(String tag);

    void onPressBackKey();

    void enterActivity(String tag);

    void showToast(String msg);
}
