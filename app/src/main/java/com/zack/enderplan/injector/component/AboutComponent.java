package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.AboutPresenterModule;
import com.zack.enderplan.view.activity.AboutActivity;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = AboutPresenterModule.class)
public interface AboutComponent {

    void inject(AboutActivity aboutActivity);
}
