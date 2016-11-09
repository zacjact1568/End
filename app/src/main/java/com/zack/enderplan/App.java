package com.zack.enderplan;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import com.zack.enderplan.model.database.DatabaseManager;
import com.zack.enderplan.model.preference.PreferenceHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class App extends Application {

    private static final int BUFFER_SIZE = 400000;

    private static Context globalContext;

    @Override
    public void onCreate() {
        super.onCreate();

        globalContext = getApplicationContext();

        //设定preferences默认值
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        initFromPreferences();

        initTypeMarkDB();
    }

    public static Context getGlobalContext() {
        return globalContext;
    }

    /** 通过Preference中的数据初始化某些设置 */
    private void initFromPreferences() {
        PreferenceHelper helper = PreferenceHelper.getInstance();
        //设定运行时的默认语言
        initLocale(helper.getStringPref(PreferenceHelper.KEY_PREF_LANGUAGE));
        //设定白天夜间模式
        initNightMode(helper.getStringPref(PreferenceHelper.KEY_PREF_NIGHT_MODE));
    }

    /** 初始化类型标记数据库 */
    private void initTypeMarkDB() {
        File typeMarkDBFile = getDatabasePath(DatabaseManager.DB_TYPE_MARK);
        if (typeMarkDBFile.exists()) {
            return;
        }
        File typeMarkDBDir = typeMarkDBFile.getParentFile();
        if (typeMarkDBDir.exists() || typeMarkDBDir.mkdir()) {
            try {
                InputStream is = getResources().openRawResource(R.raw.type_mark);
                FileOutputStream fos = new FileOutputStream(typeMarkDBFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initLocale(String value) {
        if (value.equals("def")) {
            return;
        }
        Configuration config = getResources().getConfiguration();
        switch (value) {
            case "en":
                config.setLocale(Locale.ENGLISH);
                break;
            case "zh_cn":
                config.setLocale(Locale.SIMPLIFIED_CHINESE);
                break;
            case "zh_tw":
                config.setLocale(Locale.TRADITIONAL_CHINESE);
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
