package com.zack.enderplan.view.contract;

import com.zack.enderplan.view.adapter.AboutPagerAdapter;

public interface AboutViewContract extends BaseViewContract {

    void showInitialView(String versionName, AboutPagerAdapter aboutPagerAdapter);

    void onAppBarScrolled(float headerLayoutAlpha);

    void onAppBarScrolledToCriticalPoint(String toolbarTitle);

    void translateViewWhenIncline(boolean shouldTranslateX, float translationX, boolean shouldTranslateY, float translationY);

    void resetViewTranslation();

    void backToTop();

    void pressBack();
}
