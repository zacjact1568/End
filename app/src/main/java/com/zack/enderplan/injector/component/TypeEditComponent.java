package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.TypeEditPresenterModule;
import com.zack.enderplan.view.activity.TypeEditActivity;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = TypeEditPresenterModule.class)
public interface TypeEditComponent {

    void inject(TypeEditActivity typeEditActivity);
}
