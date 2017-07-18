package me.imzack.app.ender.injector.component;

import android.content.Context;

import me.imzack.app.ender.injector.module.AppModule;
import me.imzack.app.ender.model.DataManager;

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
