package com.zack.enderplan.view.contract;

import com.zack.enderplan.view.adapter.AboutPagerAdapter;

public interface AboutViewContract extends BaseViewContract {

    void showInitialView(String versionName, AboutPagerAdapter aboutPagerAdapter);

    void onAppBarScrolled(float headerLayoutAlpha);

    void onAppBarScrolledToCriticalPoint(String toolbarTitle);

    void backToTop();

    void pressBack();
}
