package me.imzack.app.ender.view.contract;

import me.imzack.app.ender.view.adapter.GuidePagerAdapter;

public interface GuideViewContract extends BaseViewContract {

    void showInitialView(GuidePagerAdapter guidePagerAdapter);

    void onPageSelected(boolean isFirstPage, boolean isLastPage);

    void navigateToPage(int page);

    void exitWithResult(boolean isNormally);
}
