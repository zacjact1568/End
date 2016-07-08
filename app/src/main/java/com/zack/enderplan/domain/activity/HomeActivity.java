package com.zack.enderplan.domain.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.fragment.MyPlansFragment;
import com.zack.enderplan.domain.fragment.AllTypesFragment;
import com.zack.enderplan.domain.fragment.CreateTypeDialogFragment;
import com.zack.enderplan.interactor.presenter.HomePresenter;
import com.zack.enderplan.domain.view.HomeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity implements HomeView,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "HomeActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private TextView ucPlanCountText;
    private HomePresenter mHomePresenter;

    private static final String TAG_ALL_TYPES = "all_types";
    private static final String TAG_MY_PLANS = "my_plans";
    private static final int REQ_CODE_CREATE_PLAN = 0;
    public static final int REQ_CODE_PLAN_DETAIL = 1;//TODO try to let it private
    private static final int CR_ANIM_DURATION = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        View navHeaderView = navView.getHeaderView(0);
        ucPlanCountText = ButterKnife.findById(navHeaderView, R.id.text_uc_plan_count);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case REQ_CODE_CREATE_PLAN:
                mHomePresenter.notifyPlanCreated();
                break;
            case REQ_CODE_PLAN_DETAIL:
                switch (resultCode) {
                    case RESULT_PLAN_DETAIL_CHANGED:
                        //能接收到这个resultCode，那么计划属性必定有更改
                        mHomePresenter.notifyPlanDetailChanged(
                                data.getIntExtra("position", 0),
                                data.getStringExtra("plan_code"),
                                data.getBooleanExtra("is_type_of_plan_changed", false),
                                data.getBooleanExtra("is_plan_status_changed", false)
                        );
                        break;
                    case RESULT_PLAN_DELETED:
                        mHomePresenter.notifyPlanDeleted(
                                data.getIntExtra("position", 0),
                                data.getStringExtra("plan_code"),
                                data.getStringExtra("content"),
                                data.getBooleanExtra("is_completed", false)
                        );
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mHomePresenter.notifyBackPressed(
                drawerLayout.isDrawerOpen(GravityCompat.START),
                getSupportFragmentManager().findFragmentByTag(TAG_ALL_TYPES) == null
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
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
            case R.id.action_about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_my_plans:
                if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                    getSupportFragmentManager().popBackStack();
                }
                break;
            case R.id.nav_all_types:
                if (getSupportFragmentManager().findFragmentByTag(TAG_ALL_TYPES) == null) {
                    makeCircularRevealAnimationOnFab(R.drawable.ic_playlist_add_white_24dp);
                    toolbar.setTitle(R.string.title_fragment_all_types);
                    AllTypesFragment allTypesFragment = new AllTypesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, allTypesFragment, TAG_ALL_TYPES).addToBackStack(null).commit();
                    getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                        @Override
                        public void onBackStackChanged() {
                            if (getSupportFragmentManager().findFragmentByTag(TAG_ALL_TYPES) == null) {
                                makeCircularRevealAnimationOnFab(R.drawable.ic_add_white_24dp);
                                toolbar.setTitle(R.string.title_fragment_my_plans);
                                navView.setCheckedItem(R.id.nav_my_plans);
                                getSupportFragmentManager().removeOnBackStackChangedListener(this);
                            }
                        }
                    });
                }
                break;
            case R.id.nav_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
            case R.id.nav_about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                break;
            default:
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void makeCircularRevealAnimationOnFab(int imageRes) {
        Animator disappearanceAnim = ViewAnimationUtils.createCircularReveal(fab, fab.getWidth() / 2, fab.getHeight() / 2, fab.getWidth() / 2, 0);
        disappearanceAnim.setDuration(CR_ANIM_DURATION);
        disappearanceAnim.start();
        fab.setImageResource(imageRes);
        Animator appearanceAnim = ViewAnimationUtils.createCircularReveal(fab, fab.getWidth() / 2, fab.getHeight() / 2, 0, fab.getWidth() / 2);
        appearanceAnim.setDuration(CR_ANIM_DURATION);
        appearanceAnim.start();
    }

    @Override
    public void showInitialView(String ucPlanCount) {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        ucPlanCountText.setText(ucPlanCount);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MyPlansFragment(), TAG_MY_PLANS).commit();
    }

    @Override
    public void onUcPlanCountUpdated(String newUcPlanCount) {
        ucPlanCountText.setText(newUcPlanCount);
    }

    @Override
    public void onPlanCreated(String content) {
        //显示SnackBar
        String text = content + " " + getResources().getString(R.string.created_prompt);
        Snackbar.make(frameLayout, text, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onPlanDeleted(String content) {
        String text = content + " " + getResources().getString(R.string.deleted_prompt);
        Snackbar.make(frameLayout, text, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCloseDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onPressBackKey() {
        super.onBackPressed();
    }

    @Override
    public void onShowDoubleClickToast() {
        Toast.makeText(this, R.string.toast_double_click_exit, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showWelcome() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.fab)
    public void onClick() {
        if (getSupportFragmentManager().findFragmentByTag(TAG_ALL_TYPES) != null) {
            CreateTypeDialogFragment dialog = new CreateTypeDialogFragment();
            dialog.show(getFragmentManager(), "create_type");
            return;
        }
        Intent intent = new Intent(this, CreatePlanActivity.class);
        startActivityForResult(intent, REQ_CODE_CREATE_PLAN);
    }
}
