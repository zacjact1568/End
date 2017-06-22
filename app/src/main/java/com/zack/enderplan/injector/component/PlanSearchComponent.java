package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.PlanSearchPresenterModule;
import com.zack.enderplan.injector.scope.ActivityScope;
import com.zack.enderplan.view.activity.PlanSearchActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = PlanSearchPresenterModule.class)
public interface PlanSearchComponent {

    void inject(PlanSearchActivity planSearchActivity);
}
