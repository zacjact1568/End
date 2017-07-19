package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.PlanSearchPresenterModule;
import me.imzack.app.end.injector.scope.ActivityScope;
import me.imzack.app.end.view.activity.PlanSearchActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = PlanSearchPresenterModule.class)
public interface PlanSearchComponent {

    void inject(PlanSearchActivity planSearchActivity);
}
