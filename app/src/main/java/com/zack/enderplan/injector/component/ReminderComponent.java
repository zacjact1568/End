package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.ReminderPresenterModule;
import com.zack.enderplan.view.activity.ReminderActivity;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = ReminderPresenterModule.class)
public interface ReminderComponent {

    void inject(ReminderActivity reminderActivity);
}
