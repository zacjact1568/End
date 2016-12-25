package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.FirstPlanPresenterModule;
import com.zack.enderplan.injector.module.HomePresenterModule;
import com.zack.enderplan.injector.scope.ActivityScope;
import com.zack.enderplan.injector.scope.FragmentScope;
import com.zack.enderplan.view.activity.HomeActivity;
import com.zack.enderplan.view.fragment.FirstPlanFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = FirstPlanPresenterModule.class)
public interface FirstPlanComponent {

    void inject(FirstPlanFragment firstPlanFragment);
}
