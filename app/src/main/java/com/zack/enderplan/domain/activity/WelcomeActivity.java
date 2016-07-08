package com.zack.enderplan.domain.activity;

import android.os.Bundle;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.database.DatabaseDispatcher;
import com.zack.enderplan.model.preference.PreferenceDispatcher;
import com.zack.enderplan.util.Util;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends BaseActivity {

    private static final String LOG_TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    @OnClick(R.id.btn_enter)
    public void onClick() {
        exit();
    }

    private void exit() {
        PreferenceDispatcher.getInstance().setPref(PreferenceDispatcher.KEY_PREF_NEED_WELCOME, false);
        addDefaultTypes();
        //TODO 还需要添加进内存中的list！
        finish();
    }

    /** 添加预置的几个type */
    private void addDefaultTypes() {
        DatabaseDispatcher dispatcher = DatabaseDispatcher.getInstance();
        dispatcher.saveType(new Type(Util.makeCode(), getResources().getString(R.string.to_do), "#FF3F51B5", 0));
        dispatcher.saveType(new Type(Util.makeCode(), getResources().getString(R.string.family), "#FFE51C23", 1));
        dispatcher.saveType(new Type(Util.makeCode(), getResources().getString(R.string.work), "#FFFF9800", 2));
        dispatcher.saveType(new Type(Util.makeCode(), getResources().getString(R.string.study), "#FF259B24", 3));
    }
}
