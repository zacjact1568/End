package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.PlanSearchPresenterModule;
import me.imzack.app.ender.injector.scope.ActivityScope;
import me.imzack.app.ender.view.activity.PlanSearchActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = PlanSearchPresenterModule.class)
public interface PlanSearchComponent {

    void inject(PlanSearchActivity planSearchActivity);
}
