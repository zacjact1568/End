package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.PlanCreationPresenterModule;
import com.zack.enderplan.injector.scope.ActivityScope;
import com.zack.enderplan.view.activity.PlanCreationActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = PlanCreationPresenterModule.class)
public interface PlanCreationComponent {

    void inject(PlanCreationActivity planCreationActivity);

    //TODO 为什么这里不需要Plan getPlan()？
}
