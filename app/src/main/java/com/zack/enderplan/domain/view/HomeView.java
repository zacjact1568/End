package com.zack.enderplan.domain.view;

public interface HomeView {

    void showInitialView(String ucPlanCount);

    void onUcPlanCountUpdated(String newUcPlanCount);

    void onPlanCreated(String content);

    void onPlanDeleted(String content);

    void onCloseDrawer();

    void onPressBackKey();

    void onShowDoubleClickToast();
}
