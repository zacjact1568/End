package me.imzack.app.end.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import me.imzack.app.end.App;

import me.imzack.app.end.R;
import me.imzack.app.end.injector.component.DaggerHomeComponent;
import me.imzack.app.end.injector.module.HomePresenterModule;
import me.imzack.app.end.view.fragment.BaseListFragment;
import me.imzack.app.end.view.fragment.MyPlansFragment;
import me.imzack.app.end.view.fragment.AllTypesFragment;
import me.imzack.app.end.presenter.HomePresenter;
import me.imzack.app.end.view.contract.HomeViewContract;
import me.imzack.app.end.common.Constant;

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

    private TextView mPlanCountText;
    private TextView mPlanCountDscptText;

    public static void start(Context context) {
        context.startActivity(new Intent(context, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

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
        getMenuInflater().inflate(R.menu.menu_home, menu);
        (menu.findItem(R.id.action_search)).getIcon().setTint(Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                enterActivity(isFragmentShowing(Constant.MY_PLANS) ? Constant.PLAN_SEARCH : Constant.TYPE_SEARCH);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                //正常结束引导
                break;
            case RESULT_CANCELED:
                //中途退出引导
                exit();
                break;
        }
    }

    @Override
    public void showInitialView(String planCount, int textSize, String planCountDscpt) {
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        View navHeader = mNavigator.getHeaderView(0);
        mPlanCountText = ButterKnife.findById(navHeader, R.id.text_plan_count);
        mPlanCountDscptText = ButterKnife.findById(navHeader, R.id.text_plan_count_dscpt);

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

        changeDrawerHeaderDisplay(planCount, textSize, planCountDscpt);
    }

    @Override
    public void changePlanCount(String planCount, int textSize) {
        mPlanCountText.setText(planCount);
        mPlanCountText.setTextSize(textSize);
    }

    @Override
    public void changeDrawerHeaderDisplay(String planCount, int textSize, String planCountDscpt) {
        changePlanCount(planCount, textSize);
        mPlanCountDscptText.setText(planCountDscpt);
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
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
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment, fragment, tag).commit();
        }
        //在切换相同fragment时，下面的语句是不必要的
        int titleResId, navViewCheckedItemId;
        switch (tag) {
            case Constant.MY_PLANS:
                titleResId = R.string.title_fragment_my_plans;
                navViewCheckedItemId = R.id.nav_my_plans;
                break;
            case Constant.ALL_TYPES:
                titleResId = R.string.title_fragment_all_types;
                navViewCheckedItemId = R.id.nav_all_types;
                break;
            default:
                throw new IllegalArgumentException("The argument tag cannot be " + tag);
        }
        mToolbar.setTitle(titleResId);
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
            case Constant.PLAN_SEARCH:
                PlanSearchActivity.start(this);
                break;
            case Constant.TYPE_SEARCH:
                TypeSearchActivity.start(this);
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
