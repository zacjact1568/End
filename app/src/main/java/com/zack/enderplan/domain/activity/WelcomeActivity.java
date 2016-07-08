package com.zack.enderplan.domain.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.database.DatabaseDispatcher;
import com.zack.enderplan.model.preference.PreferenceDispatcher;
import com.zack.enderplan.model.ram.DataManager;
import com.zack.enderplan.util.Util;

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
        //TODO 去掉DataManager中一些map，改为动态获取，不然在这里创建新type很麻烦
        //PreferenceDispatcher.getInstance().setPref(PreferenceDispatcher.KEY_PREF_NEED_WELCOME, false);
        //addDefaultTypes();
        finish();
    }

    /** 添加预置的几个type */
    private void addDefaultTypes() {
        DatabaseDispatcher dispatcher = DatabaseDispatcher.getInstance();
        DataManager manager = DataManager.getInstance();

        Type type0 = new Type(Util.makeCode(), getResources().getString(R.string.to_do), "#FF3F51B5", 0);
        manager.addToTypeList(type0);
        dispatcher.saveType(type0);

        Type type1 = new Type(Util.makeCode(), getResources().getString(R.string.family), "#FFE51C23", 1);
        manager.addToTypeList(type1);
        dispatcher.saveType(type1);

        Type type2 = new Type(Util.makeCode(), getResources().getString(R.string.work), "#FFFF9800", 2);
        manager.addToTypeList(type2);
        dispatcher.saveType(type2);

        Type type3 = new Type(Util.makeCode(), getResources().getString(R.string.study), "#FF259B24", 3);
        manager.addToTypeList(type3);
        dispatcher.saveType(type3);
    }
}
