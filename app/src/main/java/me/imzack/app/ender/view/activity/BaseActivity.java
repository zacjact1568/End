package me.imzack.app.ender.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onInjectPresenter();
    }

    protected void onInjectPresenter() {

    }

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

    public void showToast(@StringRes int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    public void exit() {
        finish();
    }
}
