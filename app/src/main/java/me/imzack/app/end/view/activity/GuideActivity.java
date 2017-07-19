package me.imzack.app.end.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import me.imzack.app.end.App;

import me.imzack.app.end.R;
import me.imzack.app.end.injector.component.DaggerGuideComponent;
import me.imzack.app.end.injector.module.GuidePresenterModule;
import me.imzack.app.end.view.adapter.GuidePagerAdapter;
import me.imzack.app.end.view.contract.GuideViewContract;
import me.imzack.app.end.presenter.GuidePresenter;
import me.imzack.app.end.view.widget.CircleColorView;
import me.imzack.app.end.view.widget.EnhancedViewPager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuideActivity extends BaseActivity implements GuideViewContract {

    @BindView(R.id.pager_guide)
    EnhancedViewPager mGuidePager;
    @BindView(R.id.btn_start)
    CircleColorView mStartButton;
    @BindView(R.id.btn_end)
    CircleColorView mEndButton;

    @Inject
    GuidePresenter mGuidePresenter;

    public static void start(Activity activity) {
        activity.startActivityForResult(new Intent(activity, GuideActivity.class), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGuidePresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerGuideComponent.builder()
                .guidePresenterModule(new GuidePresenterModule(this, getSupportFragmentManager()))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onBackPressed() {
        mGuidePresenter.notifyBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGuidePresenter.detach();
    }

    @Override
    public void showInitialView(GuidePagerAdapter guidePagerAdapter) {
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        mGuidePager.setAdapter(guidePagerAdapter);
        mGuidePager.setScrollingEnabled(false);
        mGuidePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mGuidePresenter.notifyPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        onPageSelected(true, guidePagerAdapter.getCount() == 1);
    }

    @Override
    public void onPageSelected(boolean isFirstPage, boolean isLastPage) {
        mStartButton.setVisibility(isFirstPage ? View.GONE : View.VISIBLE);
        mEndButton.setVisibility(isFirstPage && isLastPage ? View.GONE : View.VISIBLE);
        mEndButton.setInnerIcon(getDrawable(isLastPage ? R.drawable.ic_check_black_24dp : R.drawable.ic_arrow_forward_black_24dp));
    }

    @Override
    public void navigateToPage(int page) {
        mGuidePager.setCurrentItem(page);
    }

    @Override
    public void exitWithResult(boolean isNormally) {
        setResult(isNormally ? RESULT_OK : RESULT_CANCELED);
        super.exit();
    }

    @OnClick({R.id.btn_start, R.id.btn_end})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                mGuidePresenter.notifyNavigationButtonClicked(true, mGuidePager.getCurrentItem());
                break;
            case R.id.btn_end:
                mGuidePresenter.notifyNavigationButtonClicked(false, mGuidePager.getCurrentItem());
                break;
        }
    }
}
