package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.TypeSearchPresenterModule;
import me.imzack.app.ender.injector.scope.ActivityScope;
import me.imzack.app.ender.view.activity.TypeSearchActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = TypeSearchPresenterModule.class)
public interface TypeSearchComponent {

    void inject(TypeSearchActivity typeSearchActivity);
}
