package com.zack.enderplan.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import com.zack.enderplan.R;
import com.zack.enderplan.model.database.DatabaseDispatcher;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.util.Util;

import java.util.Locale;

public class App extends Application {

    private static final String LOG_TAG = "App";

    private static Context globalContext;

    @Override
    public void onCreate() {
        super.onCreate();

        globalContext = getApplicationContext();

        //设定preferences默认值
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        //设定预置的Types
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("init_type", true)) {
            initType();
            sharedPreferences.edit().putBoolean("init_type", false).apply();
        }
        //设定运行时的默认语言
        initLocale(sharedPreferences.getString("language", ""));
        //设定白天夜间模式
        initNightMode(sharedPreferences.getString("night_mode", ""));
    }

    public static Context getGlobalContext() {
        return globalContext;
    }

    private void initType() {
        DatabaseDispatcher dispatcher = DatabaseDispatcher.getInstance();
        dispatcher.saveType(new Type(Util.makeCode(), getResources().getString(R.string.to_do), "#FF3F51B5", 0));
        dispatcher.saveType(new Type(Util.makeCode(), getResources().getString(R.string.family), "#FFE51C23", 1));
        dispatcher.saveType(new Type(Util.makeCode(), getResources().getString(R.string.work), "#FFFF9800", 2));
        dispatcher.saveType(new Type(Util.makeCode(), getResources().getString(R.string.study), "#FF259B24", 3));
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

    private void initNightMode(String value) {
        int mode = AppCompatDelegate.MODE_NIGHT_NO;
        switch (value) {
            case "off":
                mode = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case "on":
                mode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            case "auto":
                mode = AppCompatDelegate.MODE_NIGHT_AUTO;
                break;
            case "def":
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
            default:
                break;
        }
        AppCompatDelegate.setDefaultNightMode(mode);
    }
}
