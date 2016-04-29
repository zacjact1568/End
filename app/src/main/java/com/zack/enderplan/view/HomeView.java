package com.zack.enderplan.view;

public interface HomeView {

    void updateDrawerHeaderContent(String ucPlanCountStr, String ucPlanDscptStr);

    void onPlanCreated(String content);

    void onPlanDeleted(String content);
}
