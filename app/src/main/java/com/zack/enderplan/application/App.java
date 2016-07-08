package com.zack.enderplan.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import com.zack.enderplan.R;
import com.zack.enderplan.model.preference.PreferenceDispatcher;

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

        initFromPreferences();
    }

    public static Context getGlobalContext() {
        return globalContext;
    }

    /** 通过Preference中的数据初始化某些设置 */
    private void initFromPreferences() {
        PreferenceDispatcher dispatcher = PreferenceDispatcher.getInstance();
        //设定运行时的默认语言
        initLocale(dispatcher.getStringPref(PreferenceDispatcher.KEY_PREF_LANGUAGE));
        //设定白天夜间模式
        initNightMode(dispatcher.getStringPref(PreferenceDispatcher.KEY_PREF_NIGHT_MODE));
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
