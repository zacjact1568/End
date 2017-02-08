package com.zack.enderplan.view.contract;

import com.zack.enderplan.view.adapter.GuidePagerAdapter;

public interface GuideViewContract extends BaseViewContract {

    void showInitialView(GuidePagerAdapter guidePagerAdapter);

    void onPageSelected(boolean isFirstPage, boolean isLastPage);

    void navigateToPage(int page);

    void exitWithResult(boolean isNormally);
}
