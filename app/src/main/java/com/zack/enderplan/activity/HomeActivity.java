package com.zack.enderplan.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.widget.PlanAdapter;
import com.zack.enderplan.bean.Plan;

import java.util.List;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DateTimePickerDialogFragment.OnDateTimeChangedListener {

    private DrawerLayout drawerLayout;
    private TextView uncompletedPlan;
    private TextView uncompletedPlanDescription;
    private FrameLayout frameLayout;
    private AllPlansFragment allPlansFragment;
    private RemindedReceiver remindedReceiver;

    private static final String CLASS_NAME = "HomeActivity";
    private static final int REQ_CODE_CREATE_PLAN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);

        View navigationHeaderView = navigationView.getHeaderView(0);
        uncompletedPlan = (TextView) navigationHeaderView.findViewById(R.id.uncompleted_plan);
        uncompletedPlanDescription = (TextView) navigationHeaderView.findViewById(R.id.uncompleted_plan_description);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreatePlanActivity.class);
                startActivityForResult(intent, REQ_CODE_CREATE_PLAN);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        allPlansFragment = new AllPlansFragment();
        getFragmentManager().beginTransaction().replace(R.id.frame_layout, allPlansFragment).commit();
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
    protected void onResume() {
        super.onResume();
        //
    }

    @Override
    protected void onPause() {
        super.onPause();
        //
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_all_types:
                AllTypesFragment allTypesFragment = new AllTypesFragment();
                transaction.replace(R.id.frame_layout, allTypesFragment).addToBackStack(null).commit();
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
        int planItemClickPosition = allPlansFragment.getPlanItemClickPosition();
        allPlansFragment.getPlanList().get(planItemClickPosition).setReminderTime(newTimeInMillis);
        allPlansFragment.getPlanAdapter().notifyItemChanged(planItemClickPosition);
        //TODO 设定提醒，存储数据
    }

    @Override
    public void onDateTimeRemoved() {
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

    class RemindedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String planCode = intent.getStringExtra("plan_code");
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
