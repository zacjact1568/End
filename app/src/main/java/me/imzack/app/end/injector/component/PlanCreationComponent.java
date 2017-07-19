package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.PlanCreationPresenterModule;
import me.imzack.app.end.injector.scope.ActivityScope;
import me.imzack.app.end.view.activity.PlanCreationActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = PlanCreationPresenterModule.class)
public interface PlanCreationComponent {

    void inject(PlanCreationActivity planCreationActivity);

    //TODO 为什么这里不需要Plan getPlan()？
}
