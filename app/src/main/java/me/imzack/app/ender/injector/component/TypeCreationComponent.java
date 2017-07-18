package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.TypeCreationPresenterModule;
import me.imzack.app.ender.injector.scope.ActivityScope;
import me.imzack.app.ender.view.activity.TypeCreationActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = TypeCreationPresenterModule.class)
public interface TypeCreationComponent {

    void inject(TypeCreationActivity typeCreationActivity);
}
