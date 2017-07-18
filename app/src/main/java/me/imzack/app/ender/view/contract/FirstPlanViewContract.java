package me.imzack.app.ender.view.contract;

public interface FirstPlanViewContract extends BaseViewContract {

    void showInitialView();//TODO 加参数 boolean shouldShowEnterAnimation

    void onDetectedEmptyContent();

    void onFirstPlanCreated();
}
