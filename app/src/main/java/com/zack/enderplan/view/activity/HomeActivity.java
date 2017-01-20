package com.zack.enderplan.view.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.injector.component.DaggerHomeComponent;
import com.zack.enderplan.injector.module.HomePresenterModule;
import com.zack.enderplan.view.fragment.BaseListFragment;
import com.zack.enderplan.view.fragment.MyPlansFragment;
import com.zack.enderplan.view.fragment.AllTypesFragment;
import com.zack.enderplan.presenter.HomePresenter;
import com.zack.enderplan.view.contract.HomeViewContract;
import com.zack.enderplan.common.Constant;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity implements HomeViewContract {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab_create)
    FloatingActionButton mCreateFab;
    @BindView(R.id.navigator)
    NavigationView mNavigator;
    @BindView(R.id.layout_drawer)
    DrawerLayout mDrawerLayout;

    @Inject
    HomePresenter mHomePresenter;

    private TextView mUcPlanCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomePresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerHomeComponent.builder()
                .homePresenterModule(new HomePresenterModule(this))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHomePresenter.notifyStartingUpCompleted();
        //如果放到setInitialView中，toolbar的title不会改变
        showFragment(savedInstanceState == null ? Constant.MY_PLANS : savedInstanceState.getString(Constant.CURRENT_FRAGMENT, Constant.MY_PLANS));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String currentFragment;
        if (isFragmentShowing(Constant.MY_PLANS)) {
            currentFragment = Constant.MY_PLANS;
        } else {
            currentFragment = Constant.ALL_TYPES;
        }
        outState.putString(Constant.CURRENT_FRAGMENT, currentFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHomePresenter.detach();
    }

    @Override
    public void onBackPressed() {
        mHomePresenter.notifyBackPressed(
                mDrawerLayout.isDrawerOpen(GravityCompat.START),
                isFragmentShowing(Constant.MY_PLANS)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                SettingsActivity.start(this);
                break;
            case R.id.action_about:
                AboutActivity.start(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showInitialView(String ucPlanCount) {
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mUcPlanCountText = ButterKnife.findById(mNavigator.getHeaderView(0), R.id.text_uc_plan_count);

        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigator.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_my_plans:
                        showFragment(Constant.MY_PLANS);
                        break;
                    case R.id.nav_all_types:
                        showFragment(Constant.ALL_TYPES);
                        break;
                    case R.id.nav_settings:
                        enterActivity(Constant.SETTINGS);
                        break;
                    case R.id.nav_about:
                        enterActivity(Constant.ABOUT);
                        break;
                    default:
                        break;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        mUcPlanCountText.setText(ucPlanCount);
        mUcPlanCountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHomePresenter.notifyUcPlanCountTextClicked();
            }
        });
    }

    @Override
    public void changeUcPlanCount(String ucPlanCount) {
        mUcPlanCountText.setText(ucPlanCount);
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void changeFabVisibility(boolean isVisible) {
        ObjectAnimator.ofFloat(mCreateFab, "translationY", mCreateFab.getTranslationY(), isVisible ? 0f : Util.convertDpToPx(Constant.FAB_COORDINATE) + mCreateFab.getHeight() / 2f)
                .setDuration(200)
                .start();
    }

    @Override
    public void showFragment(String tag) {
        if (!isFragmentShowing(tag)) {
            BaseListFragment fragment;
            switch (tag) {
                case Constant.MY_PLANS:
                    fragment = new MyPlansFragment();
                    break;
                case Constant.ALL_TYPES:
                    fragment = new AllTypesFragment();
                    break;
                default:
                    throw new IllegalArgumentException("The argument tag cannot be " + tag);
            }
            fragment.setOnListScrolledListener(new BaseListFragment.OnListScrolledListener() {
                @Override
                public void onListScrolled(int variation) {
                    mHomePresenter.notifyListScrolled(variation);
                }
            });
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment, fragment, tag).commit();
        }
        //在切换相同fragment时，下面的语句是不必要的
        int titleResId, fabResId, navViewCheckedItemId;
        switch (tag) {
            case Constant.MY_PLANS:
                titleResId = R.string.title_fragment_my_plans;
                fabResId = R.drawable.ic_add_black_24dp;
                navViewCheckedItemId = R.id.nav_my_plans;
                break;
            case Constant.ALL_TYPES:
                titleResId = R.string.title_fragment_all_types;
                fabResId = R.drawable.ic_playlist_add_black_24dp;
                navViewCheckedItemId = R.id.nav_all_types;
                break;
            default:
                throw new IllegalArgumentException("The argument tag cannot be " + tag);
        }
        mToolbar.setTitle(titleResId);
        mCreateFab.setImageResource(fabResId);
        mCreateFab.setTranslationY(0f);
        mNavigator.setCheckedItem(navViewCheckedItemId);
    }

    @Override
    public void onPressBackKey() {
        super.onBackPressed();
    }

    @Override
    public void enterActivity(String tag) {
        switch (tag) {
            case Constant.GUIDE:
                GuideActivity.start(this);
                break;
            case Constant.SETTINGS:
                SettingsActivity.start(this);
                break;
            case Constant.ABOUT:
                AboutActivity.start(this);
                break;
        }
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.fab_create})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_create:
                if (isFragmentShowing(Constant.MY_PLANS)) {
                    PlanCreationActivity.start(this);
                } else {
                    TypeCreationActivity.start(this);
                }
                break;
        }
    }

    private boolean isFragmentShowing(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag) != null;
    }
}
