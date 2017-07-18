package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.TypeEditPresenterModule;
import me.imzack.app.ender.injector.scope.ActivityScope;
import me.imzack.app.ender.view.activity.TypeEditActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = TypeEditPresenterModule.class)
public interface TypeEditComponent {

    void inject(TypeEditActivity typeEditActivity);
}
