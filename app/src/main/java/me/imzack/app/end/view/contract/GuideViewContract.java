package me.imzack.app.end.view.contract;

import me.imzack.app.end.view.adapter.GuidePagerAdapter;

public interface GuideViewContract extends BaseViewContract {

    void showInitialView(GuidePagerAdapter guidePagerAdapter);

    void onPageSelected(boolean isFirstPage, boolean isLastPage);

    void navigateToPage(int page);

    void exitWithResult(boolean isNormally);
}
