package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.ReminderPresenterModule;
import me.imzack.app.end.injector.scope.ActivityScope;
import me.imzack.app.end.view.activity.ReminderActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = ReminderPresenterModule.class)
public interface ReminderComponent {

    void inject(ReminderActivity reminderActivity);
}
