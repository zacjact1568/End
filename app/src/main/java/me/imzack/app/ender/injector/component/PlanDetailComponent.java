package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.PlanDetailPresenterModule;
import me.imzack.app.ender.injector.scope.ActivityScope;
import me.imzack.app.ender.view.activity.PlanDetailActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = PlanDetailPresenterModule.class)
public interface PlanDetailComponent {

    void inject(PlanDetailActivity planDetailActivity);
}
