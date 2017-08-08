package me.imzack.app.end.injector.module;

import android.content.Context;

import me.imzack.app.end.model.DataManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Context mContext;

    //仅能通过App提供的才需要通过构造方法传入
    public AppModule(Context context) {
        mContext = context;
    }

    //AppComponent构造过程中并不会调用以下的provide方法

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
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }
}
