package com.zack.enderplan.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import com.zack.enderplan.R;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.util.Util;

import java.util.Locale;

public class EnderPlanApp extends Application {

    private static Context globalContext;

    @Override
    public void onCreate() {
        super.onCreate();

        globalContext = getApplicationContext();

        //设定language默认值
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //设定预置的Types
        if (sharedPreferences.getBoolean("init_type", true)) {
            initType();
            sharedPreferences.edit().putBoolean("init_type", false).apply();
        }
        //设定默认语言
        initLocale(sharedPreferences.getString("language", ""));
    }

    public static Context getGlobalContext() {
        return globalContext;
    }

    private void initType() {
        EnderPlanDB enderplanDB = EnderPlanDB.getInstance();
        enderplanDB.saveType(new Type(Util.makeCode(), getResources().getString(R.string.to_do), "#FF3F51B5", 0));
        enderplanDB.saveType(new Type(Util.makeCode(), getResources().getString(R.string.family), "#FFE51C23", 1));
        enderplanDB.saveType(new Type(Util.makeCode(), getResources().getString(R.string.work), "#FFFF9800", 2));
        enderplanDB.saveType(new Type(Util.makeCode(), getResources().getString(R.string.study), "#FF259B24", 3));
    }

    private void initLocale(String value) {
        if (value.equals("def")) {
            return;
        }
        Configuration config = getResources().getConfiguration();
        switch (value) {
            case "en":
                config.locale = Locale.ENGLISH;
                break;
            case "zh":
                config.locale = Locale.CHINESE;
                break;
            default:
                break;
        }
        getResources().updateConfiguration(config, null);
    }
}
