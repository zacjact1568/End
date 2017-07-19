package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.HomePresenterModule;
import me.imzack.app.end.injector.scope.ActivityScope;
import me.imzack.app.end.view.activity.HomeActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = HomePresenterModule.class)
public interface HomeComponent {

    void inject(HomeActivity homeActivity);
}
