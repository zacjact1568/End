package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.TypeEditPresenterModule;
import com.zack.enderplan.injector.scope.ActivityScope;
import com.zack.enderplan.view.activity.EditTypeActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = TypeEditPresenterModule.class)
public interface TypeEditComponent {

    void inject(EditTypeActivity editTypeActivity);
}
