package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.FirstPlanPresenterModule;
import com.zack.enderplan.view.fragment.FirstPlanFragment;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = FirstPlanPresenterModule.class)
public interface FirstPlanComponent {

    void inject(FirstPlanFragment firstPlanFragment);
}
