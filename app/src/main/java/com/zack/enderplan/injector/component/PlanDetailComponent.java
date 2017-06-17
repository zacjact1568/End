package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.PlanDetailPresenterModule;
import com.zack.enderplan.view.activity.PlanDetailActivity;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = PlanDetailPresenterModule.class)
public interface PlanDetailComponent {

    void inject(PlanDetailActivity planDetailActivity);
}
