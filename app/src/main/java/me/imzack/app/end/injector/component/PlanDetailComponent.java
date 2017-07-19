package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.PlanDetailPresenterModule;
import me.imzack.app.end.injector.scope.ActivityScope;
import me.imzack.app.end.view.activity.PlanDetailActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = PlanDetailPresenterModule.class)
public interface PlanDetailComponent {

    void inject(PlanDetailActivity planDetailActivity);
}
