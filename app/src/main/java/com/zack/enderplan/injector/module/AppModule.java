package com.zack.enderplan.injector.module;

import android.content.Context;

import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.preference.PreferenceHelper;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Context mContext;

    public AppModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mContext;
    }

    @Provides
    @Singleton
    DataManager provideDataManager() {
        return DataManager.getInstance();
    }

    @Provides
    @Singleton
    PreferenceHelper providePreferenceHelper() {
        return PreferenceHelper.getInstance();
    }

    @Provides
    @Singleton
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }
}
