package com.zack.enderplan.domain.activity;

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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.domain.fragment.BaseListFragment;
import com.zack.enderplan.domain.fragment.MyPlansFragment;
import com.zack.enderplan.domain.fragment.AllTypesFragment;
import com.zack.enderplan.interactor.presenter.HomePresenter;
import com.zack.enderplan.domain.view.HomeView;
import com.zack.enderplan.common.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity implements HomeView,
        NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private TextView ucPlanCountText;
    private HomePresenter mHomePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHomePresenter = new HomePresenter(this);
        mHomePresenter.setInitialView();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHomePresenter.notifyStartingUpCompleted();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHomePresenter.detachView();
    }

    @Override
    public void onBackPressed() {
        mHomePresenter.notifyBackPressed(
                drawerLayout.isDrawerOpen(GravityCompat.START),
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_my_plans:
                mHomePresenter.notifyShowingFragment(Constant.MY_PLANS, isFragmentShowing(Constant.MY_PLANS));
                break;
            case R.id.nav_all_types:
                mHomePresenter.notifyShowingFragment(Constant.ALL_TYPES, isFragmentShowing(Constant.ALL_TYPES));
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

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void showInitialView(String ucPlanCount) {
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        ucPlanCountText = ButterKnife.findById(mNavigationView.getHeaderView(0), R.id.text_uc_plan_count);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        ucPlanCountText.setText(ucPlanCount);

        showFragment(Constant.MY_PLANS, R.string.title_fragment_my_plans, R.drawable.ic_add_white_24dp);
    }

    @Override
    public void onUcPlanCountUpdated(String newUcPlanCount) {
        ucPlanCountText.setText(newUcPlanCount);
    }

    @Override
    public void onCloseDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void changeFabVisibility(boolean isVisible) {
        ObjectAnimator.ofFloat(fab, "translationY", fab.getTranslationY(), isVisible ? 0f : Util.convertDpToPx(Constant.COORDINATE_FAB) + fab.getHeight() / 2f)
                .setDuration(200)
                .start();
    }

    @Override
    public void showFragment(String tag, int titleResId, int fabResId) {
        BaseListFragment fragment;
        int navViewCheckedItemId;
        switch (tag) {
            case Constant.MY_PLANS:
                fragment = new MyPlansFragment();
                navViewCheckedItemId = R.id.nav_my_plans;
                break;
            case Constant.ALL_TYPES:
                fragment = new AllTypesFragment();
                navViewCheckedItemId = R.id.nav_all_types;
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
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, tag).commit();
        toolbar.setTitle(titleResId);
        fab.setImageResource(fabResId);
        fab.setTranslationY(0f);
        mNavigationView.setCheckedItem(navViewCheckedItemId);
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
    public void exitHome() {
        finish();
    }

    @Override
    public void showToast(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                if (isFragmentShowing(Constant.MY_PLANS)) {
                    CreatePlanActivity.start(this);
                } else {
                    CreateTypeActivity.start(this);
                }
                break;
        }
    }

    private boolean isFragmentShowing(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag) != null;
    }
}
