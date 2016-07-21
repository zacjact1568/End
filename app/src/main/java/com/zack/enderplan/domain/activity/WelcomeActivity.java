package com.zack.enderplan.domain.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.preference.PreferenceHelper;
import com.zack.enderplan.utility.Util;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends BaseActivity {

    private static final String LOG_TAG = "WelcomeActivity";

    private long lastBackKeyPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackKeyPressedTime < 1500) {
            setResult(RESULT_OK);
            super.onBackPressed();
        } else {
            lastBackKeyPressedTime = currentTime;
            Toast.makeText(this, R.string.toast_double_click_exit, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_start)
    public void onClick() {
        PreferenceHelper.getInstance().setPref(PreferenceHelper.KEY_PREF_NEED_WELCOME, false);
        addDefaultTypes();
        finish();
    }

    /** 添加预置的几个type */
    private void addDefaultTypes() {
        DataManager manager = DataManager.getInstance();
        manager.notifyTypeCreated(new Type(Util.makeCode(), getResources().getString(R.string.to_do), "#FF3F51B5", 0));
        manager.notifyTypeCreated(new Type(Util.makeCode(), getResources().getString(R.string.family), "#FFE51C23", 1));
        manager.notifyTypeCreated(new Type(Util.makeCode(), getResources().getString(R.string.work), "#FFFF9800", 2));
        manager.notifyTypeCreated(new Type(Util.makeCode(), getResources().getString(R.string.study), "#FF259B24", 3));
    }
}
