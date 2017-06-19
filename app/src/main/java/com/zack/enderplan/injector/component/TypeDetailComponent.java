package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.TypeDetailPresenterModule;
import com.zack.enderplan.injector.scope.ActivityScope;
import com.zack.enderplan.view.activity.TypeDetailActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = TypeDetailPresenterModule.class)
public interface TypeDetailComponent {

    void inject(TypeDetailActivity typeDetailActivity);
}
