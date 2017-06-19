package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.TypeCreationPresenterModule;
import com.zack.enderplan.injector.scope.ActivityScope;
import com.zack.enderplan.view.activity.TypeCreationActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = TypeCreationPresenterModule.class)
public interface TypeCreationComponent {

    void inject(TypeCreationActivity typeCreationActivity);
}
