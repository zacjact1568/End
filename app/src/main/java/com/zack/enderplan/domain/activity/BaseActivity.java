package com.zack.enderplan.domain.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected String getActivityName() {
        return getClass().getSimpleName();
    }

    /** Set up the {@link android.app.ActionBar}, if the API is available. */
    protected void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
