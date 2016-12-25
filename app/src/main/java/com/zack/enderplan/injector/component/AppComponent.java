package com.zack.enderplan.injector.component;

import android.content.Context;

import com.zack.enderplan.injector.module.AppModule;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.preference.PreferenceHelper;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    Context getContext();

    DataManager getDataManager();

    PreferenceHelper getPreferenceHelper();

    EventBus getEventBus();
}
