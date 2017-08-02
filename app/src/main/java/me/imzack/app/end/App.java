package me.imzack.app.end;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import me.imzack.app.end.injector.component.AppComponent;
import me.imzack.app.end.injector.component.DaggerAppComponent;
import me.imzack.app.end.injector.module.AppModule;
import me.imzack.app.end.model.DataManager;
import me.imzack.app.end.common.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class App extends Application {

    private static final int BUFFER_SIZE = 400000;

    private static AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .build();

        //设定preferences默认值
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        initFromPreferences();

        initTypeMarkDB();
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    public static Context getContext() {
        return mAppComponent.getContext();
    }

    public static DataManager getDataManager() {
        return mAppComponent.getDataManager();
    }

    /** 通过Preference中的数据初始化某些设置 */
    private void initFromPreferences() {
        //设定白天夜间模式
        AppCompatDelegate.setDefaultNightMode(getDataManager().getPreferenceHelper().getNightModeValue() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    /** 初始化类型标记数据库 */
    private void initTypeMarkDB() {
        File typeMarkDBFile = getDatabasePath(Constant.DB_TYPE_MARK);
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
}