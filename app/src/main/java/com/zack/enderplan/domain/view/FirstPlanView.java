package com.zack.enderplan.domain.view;

public interface FirstPlanView {

    void showInitialView(boolean shouldShowEnterAnimation);

    void onDetectedEmptyContent();

    void onFirstPlanCreated();
}
