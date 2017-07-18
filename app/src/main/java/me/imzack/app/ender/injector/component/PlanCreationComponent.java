package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.PlanCreationPresenterModule;
import me.imzack.app.ender.injector.scope.ActivityScope;
import me.imzack.app.ender.view.activity.PlanCreationActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = PlanCreationPresenterModule.class)
public interface PlanCreationComponent {

    void inject(PlanCreationActivity planCreationActivity);

    //TODO 为什么这里不需要Plan getPlan()？
}
