package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.PlanCreationPresenterModule;
import com.zack.enderplan.injector.scope.ActivityScope;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.view.activity.CreatePlanActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = PlanCreationPresenterModule.class)
public interface PlanCreationComponent {

    void inject(CreatePlanActivity createPlanActivity);

    //TODO 为什么这里不需要Plan getPlan()？
}
