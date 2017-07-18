package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.ReminderPresenterModule;
import me.imzack.app.ender.injector.scope.ActivityScope;
import me.imzack.app.ender.view.activity.ReminderActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = ReminderPresenterModule.class)
public interface ReminderComponent {

    void inject(ReminderActivity reminderActivity);
}
