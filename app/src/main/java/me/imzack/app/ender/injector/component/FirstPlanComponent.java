package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.FirstPlanPresenterModule;
import me.imzack.app.ender.injector.scope.FragmentScope;
import me.imzack.app.ender.view.fragment.FirstPlanFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = FirstPlanPresenterModule.class)
public interface FirstPlanComponent {

    void inject(FirstPlanFragment firstPlanFragment);
}
