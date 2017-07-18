package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.HomePresenterModule;
import me.imzack.app.ender.injector.scope.ActivityScope;
import me.imzack.app.ender.view.activity.HomeActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = HomePresenterModule.class)
public interface HomeComponent {

    void inject(HomeActivity homeActivity);
}
