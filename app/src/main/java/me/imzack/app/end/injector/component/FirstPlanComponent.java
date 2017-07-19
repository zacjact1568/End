package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.FirstPlanPresenterModule;
import me.imzack.app.end.injector.scope.FragmentScope;
import me.imzack.app.end.view.fragment.FirstPlanFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = FirstPlanPresenterModule.class)
public interface FirstPlanComponent {

    void inject(FirstPlanFragment firstPlanFragment);
}
