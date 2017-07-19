package me.imzack.app.end.view.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pixelcan.inkpageindicator.InkPageIndicator;
import me.imzack.app.end.App;

import me.imzack.app.end.R;
import me.imzack.app.end.injector.component.DaggerAboutComponent;
import me.imzack.app.end.injector.module.AboutPresenterModule;
import me.imzack.app.end.presenter.AboutPresenter;
import me.imzack.app.end.view.adapter.AboutPagerAdapter;
import me.imzack.app.end.view.contract.AboutViewContract;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity implements AboutViewContract {

    @BindView(R.id.layout_app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.layout_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
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
                .aboutPresenterModule(new AboutPresenterModule(this, getSupportFragmentManager(), (SensorManager) getSystemService(SENSOR_SERVICE)))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAboutPresenter.notifyRegisteringSensorListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAboutPresenter.notifyUnregisteringSensorListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //将位置恢复放在这，就看不到移动的过程了
        mAboutPresenter.notifyResetingViewTranslation();
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

        //注释掉这一句使AppBar可折叠
        ((AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams()).setScrollFlags(0);

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
    public void translateViewWhenIncline(boolean shouldTranslateX, float translationX, boolean shouldTranslateY, float translationY) {
        if (!shouldTranslateX && !shouldTranslateY) return;
        Path path = new Path();
        //start point
        path.moveTo(mHeaderLayout.getTranslationX(), mHeaderLayout.getTranslationY());
        //end point
        if (shouldTranslateX && shouldTranslateY) {
            //x和y均合适
            path.lineTo(translationX, translationY);
        } else if (shouldTranslateX) {
            //x合适，y不合适
            path.lineTo(translationX, mHeaderLayout.getTranslationY());
        } else {
            //x不合适，y合适
            path.lineTo(mHeaderLayout.getTranslationX(), translationY);
        }
        ObjectAnimator.ofFloat(mHeaderLayout, "translationX", "translationY", path).setDuration(80).start();
    }

    @Override
    public void resetViewTranslation() {
        mHeaderLayout.setTranslationX(0f);
        mHeaderLayout.setTranslationY(0f);
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
