package com.zack.enderplan.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    /** Activity result: plan detail changed. */
    public static final int RESULT_PLAN_DETAIL_CHANGED = 2;
    /** Activity result: plan status changed. */
    public static final int RESULT_PLAN_STATUS_CHANGED = 3;
    /** Activity result: plan detail and status changed. */
    public static final int RESULT_PLAN_DETAIL_AND_STATUS_CHANGED = 4;
    /** Activity result: plan deleted. */
    public static final int RESULT_PLAN_DELETED = 5;

    /** Set up the {@link android.app.ActionBar}, if the API is available. */
    protected void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
