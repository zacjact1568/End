package com.zack.enderplan.activity;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.manager.TypeManager;
import com.zack.enderplan.widget.PlanAdapter;
import com.zack.enderplan.bean.Plan;

import java.util.List;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DateTimePickerDialogFragment.OnDateTimeChangedListener,
        CreateTypeDialogFragment.OnTypeCreatedListener {

    private FloatingActionButton fab;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView uncompletedPlan;
    private TextView uncompletedPlanDescription;
    private FrameLayout frameLayout;
    private TypeManager typeManager;
    private RemindedReceiver remindedReceiver;

    private static final String CLASS_NAME = "HomeActivity";
    private static final String TAG_ALL_TYPES = "all_types";
    private static final String TAG_ALL_PLANS = "all_plans";
    private static final int REQ_CODE_CREATE_PLAN = 0;
    private static final int CR_ANIM_DURATION = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);

        View navigationHeaderView = navigationView.getHeaderView(0);
        uncompletedPlan = (TextView) navigationHeaderView.findViewById(R.id.uncompleted_plan);
        uncompletedPlanDescription = (TextView) navigationHeaderView.findViewById(R.id.uncompleted_plan_description);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        typeManager = TypeManager.getInstance();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportFragmentManager().findFragmentByTag(TAG_ALL_TYPES) != null) {
                    CreateTypeDialogFragment dialog = new CreateTypeDialogFragment();
                    dialog.show(getFragmentManager(), "create_type");
                    return;
                }
                Intent intent = new Intent(HomeActivity.this, CreatePlanActivity.class);
                startActivityForResult(intent, REQ_CODE_CREATE_PLAN);
            }
        });

        AllPlansFragment allPlansFragment = new AllPlansFragment();
        getFragmentManager().beginTransaction().replace(R.id.frame_layout, allPlansFragment, TAG_ALL_PLANS).commit();
        allPlansFragment.setOnUncompletedPlanCountChangedListener(new AllPlansFragment.OnUncompletedPlanCountChangedListener() {
            @Override
            public void onUncompletedPlanCountChanged(int newUncompletedPlanCount) {
                updateDrawerHeaderContent(newUncompletedPlanCount);
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.zack.enderplan.ACTION_REMINDED");
        intentFilter.setPriority(0);
        remindedReceiver = new RemindedReceiver();
        registerReceiver(remindedReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(remindedReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case REQ_CODE_CREATE_PLAN:
                AllPlansFragment allPlansFragment = (AllPlansFragment) getFragmentManager().findFragmentByTag(TAG_ALL_PLANS);
                int newUncompletedPlanCount = allPlansFragment.getUncompletedPlanCount() + 1;
                updateDrawerHeaderContent(newUncompletedPlanCount);
                allPlansFragment.setUncompletedPlanCount(newUncompletedPlanCount);

                Plan newPlan = data.getParcelableExtra("plan_detail");
                allPlansFragment.getPlanList().add(0, newPlan);

                PlanAdapter planAdapter = allPlansFragment.getPlanAdapter();
                //To resolve a bug of RecyclerView.
                //Check if the planAdapter is empty when onPlanCreate execute.
                if (planAdapter.getItemCount() == 1) {
                    planAdapter.notifyDataSetChanged();
                } else {
                    planAdapter.notifyItemInserted(0);
                }

                if (newPlan.getReminderTime() != 0) {
                    ReminderManager manager = new ReminderManager(this);
                    manager.setAlarm(newPlan.getPlanCode(), newPlan.getReminderTime());
                }

                typeManager.updatePlanCountOfEachType(newPlan.getTypeCode(), 1);

                String text = newPlan.getContent() + " " + getResources().getString(R.string.created_prompt);
                Snackbar.make(frameLayout, text, Snackbar.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            case R.id.nav_home:
                if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                    getSupportFragmentManager().popBackStack();
                }
                break;
            case R.id.nav_all_types:
                if (getSupportFragmentManager().findFragmentByTag(TAG_ALL_TYPES) == null) {
                    makeCircularRevealAnimationOnFab(R.drawable.ic_playlist_add_white_24dp);
                    AllTypesFragment allTypesFragment = new AllTypesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, allTypesFragment, TAG_ALL_TYPES).addToBackStack(null).commit();
                    getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                        @Override
                        public void onBackStackChanged() {
                            if (getSupportFragmentManager().findFragmentByTag(TAG_ALL_TYPES) == null) {
                                makeCircularRevealAnimationOnFab(R.drawable.ic_add_white_24dp);
                                navigationView.setCheckedItem(R.id.nav_home);
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

    @Override
    public void onDateTimeSelected(long newTimeInMillis) {
        AllPlansFragment allPlansFragment = (AllPlansFragment) getFragmentManager().findFragmentByTag(TAG_ALL_PLANS);
        int planItemClickPosition = allPlansFragment.getPlanItemClickPosition();
        allPlansFragment.getPlanList().get(planItemClickPosition).setReminderTime(newTimeInMillis);
        allPlansFragment.getPlanAdapter().notifyItemChanged(planItemClickPosition);
        //TODO 设定提醒，存储数据
    }

    @Override
    public void onDateTimeRemoved() {
        AllPlansFragment allPlansFragment = (AllPlansFragment) getFragmentManager().findFragmentByTag(TAG_ALL_PLANS);
        int planItemClickPosition = allPlansFragment.getPlanItemClickPosition();
        allPlansFragment.getPlanList().get(planItemClickPosition).setReminderTime(0);
        allPlansFragment.getPlanAdapter().notifyItemChanged(planItemClickPosition);
        //TODO 取消提醒，存储数据
    }

    private void updateDrawerHeaderContent(int uncompletedPlanCount) {
        uncompletedPlan.setText(String.valueOf(uncompletedPlanCount));
        uncompletedPlanDescription.setText(getResources().getString(uncompletedPlanCount == 0 ?
                R.string.plan_uncompleted_none : R.string.plan_uncompleted_exist));
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
    public void onTypeCreated(Type type) {
        AllTypesFragment fragment = (AllTypesFragment) getSupportFragmentManager().findFragmentByTag(TAG_ALL_TYPES);
        List<Type> typeList = fragment.getTypeList();
        typeList.add(type);
        fragment.getTypeAdapter().notifyItemInserted(typeList.size() - 1);
    }

    class RemindedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String planCode = intent.getStringExtra("plan_code");
            AllPlansFragment allPlansFragment = (AllPlansFragment) getFragmentManager().findFragmentByTag(TAG_ALL_PLANS);
            List<Plan> planList = allPlansFragment.getPlanList();
            for (int i = 0; i < planList.size(); i++) {
                Plan plan = planList.get(i);
                if (plan.getPlanCode().equals(planCode)) {
                    //TODO 改成只遍历有reminder的plan
                    plan.setReminderTime(0);
                    allPlansFragment.getPlanAdapter().notifyItemChanged(i);
                    break;
                }
            }
        }
    }
}
