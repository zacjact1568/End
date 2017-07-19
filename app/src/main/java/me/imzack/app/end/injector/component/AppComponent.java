package me.imzack.app.end.injector.component;

import android.content.Context;

import me.imzack.app.end.injector.module.AppModule;
import me.imzack.app.end.model.DataManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    Context getContext();

    DataManager getDataManager();

    EventBus getEventBus();
}
