package com.zack.enderplan.view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zack.enderplan.R;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setupActionBar();

        getFragmentManager().beginTransaction().add(R.id.frame_layout, new SettingsFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exit();
                break;
            case R.id.action_reset:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_dialog_reset_settings)
                        .setMessage(R.string.msg_dialog_reset_settings)
                        .setPositiveButton(R.string.btn_dialog_reset_settings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataManager.getInstance().getPreferenceHelper().resetAllValues();
                            }
                        })
                        .setNegativeButton(R.string.button_cancel, null)
                        .show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
