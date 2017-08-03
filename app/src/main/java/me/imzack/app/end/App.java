package me.imzack.app.end;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import org.greenrobot.eventbus.EventBus;

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

    private static AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        initAppComponent();

        initFromPreferences();

        initTypeMarkDB();
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    /** 获取全局Context */
    public static Context getContext() {
        return mAppComponent.getContext();
    }

    /**
     * 获取DataManager单例<br>
     * NOTE：使用DataManager.getInstance()获取到的DataManager还是同一个，因为DataManager本就是单例模式<br>
     * 每调用一次AppComponent的getDataManager()方法，都会调用一次AppModule的provideDataManager()方法
     */
    public static DataManager getDataManager() {
        return mAppComponent.getDataManager();
    }

    /** 获取EventBus单例<br>NOTE：不要用EventBus.getDefault()，获取到的EventBus不是同一个 */
    public static EventBus getEventBus() {
        return mAppComponent.getEventBus();
    }

    private void initAppComponent() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .build();
    }

    /** 通过Preference中的数据初始化某些设置 */
    private void initFromPreferences() {
        //设定preferences默认值（仅执行一次）
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
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
                // 400000 is BUFFER_SIZE
                byte[] buffer = new byte[400000];
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
