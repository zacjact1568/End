package com.zack.enderplan.domain.view;

public interface HomeView {

    void updateDrawerHeaderContent(String ucPlanCountStr);

    void onPlanCreated(String content);

    void onPlanDeleted(String content);

    void onCloseDrawer();

    void onPressBackKey();

    void onShowDoubleClickToast();
}
