package com.zack.enderplan.presenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.zack.enderplan.R;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.SystemUtil;
import com.zack.enderplan.view.adapter.AboutPagerAdapter;
import com.zack.enderplan.view.contract.AboutViewContract;
import com.zack.enderplan.view.fragment.AboutFragment;
import com.zack.enderplan.view.fragment.LibrariesFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AboutPresenter extends BasePresenter {

    private static final int APP_BAR_STATE_EXPANDED = 1;
    private static final int APP_BAR_STATE_INTERMEDIATE = 0;
    private static final int APP_BAR_STATE_COLLAPSED = -1;

    private AboutPagerAdapter mAboutPagerAdapter;
    private int mAppBarMaxRange;
    private float mLastHeaderAlpha = 1f;
    private int mAppBarState = APP_BAR_STATE_EXPANDED;

    private AboutViewContract mAboutViewContract;

    @Inject
    AboutPresenter(AboutViewContract aboutViewContract, FragmentManager fragmentManager) {
        mAboutViewContract = aboutViewContract;

        mAboutPagerAdapter = new AboutPagerAdapter(fragmentManager, getAboutPages());
    }

    @Override
    public void attach() {
        mAboutViewContract.showInitialView(SystemUtil.getVersionName(), mAboutPagerAdapter);
    }

    @Override
    public void detach() {
        mAboutViewContract = null;
    }

    public void notifyPreDrawingAppBar(int appBarMaxRange) {
        mAppBarMaxRange = appBarMaxRange;
    }

    public void notifyAppBarScrolled(int offset) {

        if (mAppBarMaxRange == 0) return;

        int absOffset = Math.abs(offset);
        float headerAlpha = 1f - absOffset * 1.3f / mAppBarMaxRange;
        if (headerAlpha < 0) headerAlpha = 0;

        if ((headerAlpha == 0 || mLastHeaderAlpha == 0) && headerAlpha != mLastHeaderAlpha) {
            mAboutViewContract.onAppBarScrolledToCriticalPoint(headerAlpha == 0 ? ResourceUtil.getString(R.string.title_activity_about) : " ");
            mLastHeaderAlpha = headerAlpha;
        }

        mAboutViewContract.onAppBarScrolled(headerAlpha);

        if (absOffset == 0) {
            mAppBarState = APP_BAR_STATE_EXPANDED;
        } else if (absOffset == mAppBarMaxRange) {
            mAppBarState = APP_BAR_STATE_COLLAPSED;
        } else {
            mAppBarState = APP_BAR_STATE_INTERMEDIATE;
        }
    }

    public void notifyBackPressed() {
        if (mAppBarState == APP_BAR_STATE_EXPANDED) {
            mAboutViewContract.pressBack();
        } else {
            mAboutViewContract.backToTop();
        }
    }

    private List<Fragment> getAboutPages() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new AboutFragment());
        fragmentList.add(new LibrariesFragment());
        return fragmentList;
    }
}
