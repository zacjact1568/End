package com.zack.enderplan.activity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.widget.EnhancedRecyclerView;
import com.zack.enderplan.widget.PlanAdapter;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.bean.Plan;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DateTimePickerDialogFragment.OnDateTimeChangedListener {

    private DrawerLayout drawerLayout;
    private EnderPlanDB enderplanDB;
    private List<Plan> planList;
    private PlanAdapter planAdapter;
    private TextView uncompletedPlan;
    private TextView uncompletedPlanDescription;
    private FrameLayout frameLayout;
    private int uncompletedPlanCount;
    private int itemClickPosition;
    //private int[] priorityLevelMarkResIds;
    //private LocalBroadcastManager localBroadcastManager;
    private RemindedReceiver remindedReceiver;

    private static final String CLASS_NAME = "HomeActivity";
    private static final int REQ_CODE_CREATE_PLAN = 0;
    private static final int REQ_CODE_PLAN_DETAIL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        EnhancedRecyclerView recyclerView = (EnhancedRecyclerView) findViewById(R.id.recycler_view);

        View navigationHeaderView = navigationView.getHeaderView(0);
        uncompletedPlan = (TextView) navigationHeaderView.findViewById(R.id.uncompleted_plan);
        uncompletedPlanDescription = (TextView) navigationHeaderView.findViewById(R.id.uncompleted_plan_description);

        enderplanDB = EnderPlanDB.getInstance(this);

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
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        planList = new ArrayList<>();
        planAdapter = new PlanAdapter(this, planList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(findViewById(R.id.text_empty_view));
        recyclerView.setAdapter(planAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                planList.addAll(enderplanDB.loadPlan());
                uncompletedPlanCount = getUncompletedPlanCount(planList);
                planAdapter.notifyDataSetChanged();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDrawerHeaderContent();
                    }
                });
            }
        }).start();

        planAdapter.setOnItemClickListener(new PlanAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                itemClickPosition = position;
                Intent intent = new Intent(HomeActivity.this, PlanDetailActivity.class);
                intent.putExtra("plan_detail", planList.get(position));
                startActivityForResult(intent, REQ_CODE_PLAN_DETAIL);
            }
        });

        planAdapter.setOnItemLongClickListener(new PlanAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                itemClickPosition = position;
                DateTimePickerDialogFragment dialog = DateTimePickerDialogFragment.newInstance(planList.get(position).getReminderTime());
                dialog.show(getFragmentManager(), "reminder");
            }
        });

        //priorityLevelMarkResIds = Util.getPriorityLevelMarkResIds(this);

        planAdapter.setOnStarMarkClickListener(new PlanAdapter.OnStarMarkClickListener() {
            @Override
            public void onStarMarkClick(ImageView starMark, int itemPosition) {
                Plan plan = planList.get(itemPosition);
                boolean isStarred = plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED;
                int newStarStatus = isStarred ? Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED;
                plan.setStarStatus(newStarStatus);
                starMark.setImageResource(isStarred ? R.drawable.ic_star_outline_grey600_24dp :
                        R.drawable.ic_star_color_accent_24dp);

                ContentValues values = new ContentValues();
                values.put("star_status", newStarStatus);
                enderplanDB.editPlan(plan.getPlanCode(), values);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(0, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getLayoutPosition();
                final Plan plan = planList.get(position);
                final boolean isReminderEnabled = plan.getReminderTime() != 0;
                final boolean isCompleted = plan.getCompletionTime() != 0;

                if (!isCompleted && isReminderEnabled) {
                    ReminderManager manager = new ReminderManager(HomeActivity.this);
                    manager.cancelAlarm(plan.getPlanCode());
                }

                planList.remove(position);
                planAdapter.notifyItemRemoved(position);

                switch (direction) {
                    case ItemTouchHelper.START:
                        //Delete
                        vibrate();

                        if (!isCompleted) {
                            uncompletedPlanCount--;
                            updateDrawerHeaderContent();
                        }

                        String text = plan.getContent() + " " + getResources().getString(R.string.deleted_prompt);
                        Snackbar snackbar = Snackbar.make(frameLayout, text, Snackbar.LENGTH_LONG);
                        snackbar.setAction(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!isCompleted) {
                                    uncompletedPlanCount++;
                                    updateDrawerHeaderContent();
                                }
                                if (isReminderEnabled) {
                                    ReminderManager manager = new ReminderManager(HomeActivity.this);
                                    manager.setAlarm(plan.getPlanCode(), plan.getDeadline());
                                }
                                planList.add(position, plan);
                                planAdapter.notifyItemInserted(position);
                            }
                        });
                        snackbar.setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                if (event != DISMISS_EVENT_ACTION) {
                                    enderplanDB.deletePlan(plan.getPlanCode());
                                }
                            }
                        });
                        snackbar.show();

                        break;
                    case ItemTouchHelper.END:
                        //Complete
                        long currentTimeMillis = System.currentTimeMillis();

                        long newCreationTime = isCompleted ? currentTimeMillis : 0;
                        long newCompletionTime = isCompleted ? 0 : currentTimeMillis;

                        plan.setCreationTime(newCreationTime);
                        plan.setCompletionTime(newCompletionTime);

                        uncompletedPlanCount = uncompletedPlanCount + (isCompleted ? 1 : -1);
                        updateDrawerHeaderContent();

                        int newPosition = isCompleted ? 0 : uncompletedPlanCount;
                        planList.add(newPosition, plan);
                        planAdapter.notifyItemInserted(newPosition);

                        ContentValues values = new ContentValues();
                        values.put(EnderPlanDB.DB_STR_CREATION_TIME, newCreationTime);
                        values.put(EnderPlanDB.DB_STR_COMPLETION_TIME, newCompletionTime);
                        enderplanDB.editPlan(plan.getPlanCode(), values);

                        break;
                    default:
                        break;
                }
            }
        }).attachToRecyclerView(recyclerView);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.zack.enderplan.ACTION_REMINDED");
        intentFilter.setPriority(0);
        remindedReceiver = new RemindedReceiver();
        registerReceiver(remindedReceiver, intentFilter);
        /*localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(remindedReceiver, intentFilter);*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO Save data
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(remindedReceiver);
        //localBroadcastManager.unregisterReceiver(remindedReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        } else if (resultCode == RESULT_FIRST_USER) {
            Plan plan = planList.get(itemClickPosition);
            planList.remove(itemClickPosition);
            planAdapter.notifyItemRemoved(itemClickPosition);
            if (plan.getCompletionTime() == 0) {
                uncompletedPlanCount--;
                updateDrawerHeaderContent();
            }
            String text = plan.getContent() + " " + getResources().getString(R.string.deleted_prompt);
            Snackbar.make(frameLayout, text, Snackbar.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode) {
            case REQ_CODE_CREATE_PLAN:
                uncompletedPlanCount++;
                updateDrawerHeaderContent();

                Plan newPlan = data.getParcelableExtra("plan_detail");
                planList.add(0, newPlan);

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
            case REQ_CODE_PLAN_DETAIL:
                Plan editedPlan = data.getParcelableExtra("plan_detail");
                planList.set(itemClickPosition, editedPlan);
                planAdapter.notifyItemChanged(itemClickPosition);
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
            case R.id.nav_starred_plans:
                StarredPlansFragment starredPlansFragment = new StarredPlansFragment();
                transaction.replace(R.id.frame_layout, starredPlansFragment).addToBackStack(null).commit();
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
        planList.get(itemClickPosition).setReminderTime(newTimeInMillis);
        planAdapter.notifyItemChanged(itemClickPosition);
        //TODO 设定提醒，存储数据
    }

    @Override
    public void onDateTimeRemoved() {
        planList.get(itemClickPosition).setReminderTime(0);
        planAdapter.notifyItemChanged(itemClickPosition);
        //TODO 设定提醒，存储数据
    }

    //@Override
    public void onPlanEdited() {
        planAdapter.notifyItemChanged(itemClickPosition);
        Snackbar.make(frameLayout, R.string.edited_prompt, Snackbar.LENGTH_SHORT).show();
    }

    private int getUncompletedPlanCount(List<Plan> planList) {
        int quantity = 0;
        for (Plan plan : planList) {
            if (plan.getCreationTime() == 0) {
                break;
            }
            quantity++;
        }
        return quantity;
    }

    private void updateDrawerHeaderContent() {
        uncompletedPlan.setText(String.valueOf(uncompletedPlanCount));
        uncompletedPlanDescription.setText(getResources().getString(uncompletedPlanCount == 0 ?
                R.string.plan_uncompleted_none : R.string.plan_uncompleted_exist));
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {0, 100};
        vibrator.vibrate(pattern, -1);
    }

    class RemindedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String planCode = intent.getStringExtra("plan_code");
            for (int i = 0; i < planList.size(); i++) {
                Plan plan = planList.get(i);
                if (plan.getPlanCode().equals(planCode)) {
                    //TODO 改成只遍历有reminder的plan
                    plan.setReminderTime(0);
                    planAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    }
}
