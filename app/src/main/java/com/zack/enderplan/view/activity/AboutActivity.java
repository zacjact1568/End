package com.zack.enderplan.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.injector.component.DaggerAboutComponent;
import com.zack.enderplan.injector.module.AboutPresenterModule;
import com.zack.enderplan.presenter.AboutPresenter;
import com.zack.enderplan.view.adapter.AboutPagerAdapter;
import com.zack.enderplan.view.contract.AboutViewContract;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity implements AboutViewContract {

    @BindView(R.id.layout_app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.layout_header)
    LinearLayout mHeaderLayout;
    @BindView(R.id.text_version)
    TextView mVersionText;
    @BindView(R.id.pager_about)
    ViewPager mAboutPager;
    @BindView(R.id.indicator_about)
    InkPageIndicator mAboutIndicator;

    @Inject
    AboutPresenter mAboutPresenter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAboutPresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerAboutComponent.builder()
                .aboutPresenterModule(new AboutPresenterModule(this, getSupportFragmentManager()))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAboutPresenter.detach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mAboutPresenter.notifyBackPressed();
    }

    @Override
    public void showInitialView(String versionName, AboutPagerAdapter aboutPagerAdapter) {
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupActionBar();

        mAppBarLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mAppBarLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                mAboutPresenter.notifyPreDrawingAppBar(mAppBarLayout.getTotalScrollRange());
                return false;
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mAboutPresenter.notifyAppBarScrolled(verticalOffset);
            }
        });

        mVersionText.setText(versionName);
        mAboutPager.setAdapter(aboutPagerAdapter);
        mAboutIndicator.setViewPager(mAboutPager);
    }

    @Override
    public void onAppBarScrolled(float headerLayoutAlpha) {
        mHeaderLayout.setAlpha(headerLayoutAlpha);
    }

    @Override
    public void onAppBarScrolledToCriticalPoint(String toolbarTitle) {
        mToolbar.setTitle(toolbarTitle);
    }

    @Override
    public void backToTop() {
        mAppBarLayout.setExpanded(true);
    }

    @Override
    public void pressBack() {
        super.onBackPressed();
    }
}
