package com.zack.enderplan.view;

public interface HomeView {

    void updateDrawerHeaderContent(String ucPlanCountStr);

    void onPlanCreated(String content);

    void onPlanDeleted(String content);

    void onCloseDrawer();

    void onPressBackKey();

    void onShowDoubleClickToast();
}
