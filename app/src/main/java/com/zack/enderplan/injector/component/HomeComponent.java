package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.HomePresenterModule;
import com.zack.enderplan.view.activity.HomeActivity;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = HomePresenterModule.class)
public interface HomeComponent {

    void inject(HomeActivity homeActivity);
}
