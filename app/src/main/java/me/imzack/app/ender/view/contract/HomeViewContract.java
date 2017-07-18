package me.imzack.app.ender.view.contract;

public interface HomeViewContract extends BaseViewContract {

    void showInitialView(String planCount, int textSize, String planCountDscpt);

    void changePlanCount(String planCount, int textSize);

    void changeDrawerHeaderDisplay(String planCount, int textSize, String planCountDscpt);

    void closeDrawer();

    void showFragment(String tag);

    void onPressBackKey();

    void enterActivity(String tag);

    void showToast(String msg);
}
