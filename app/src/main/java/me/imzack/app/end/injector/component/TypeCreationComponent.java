package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.TypeCreationPresenterModule;
import me.imzack.app.end.injector.scope.ActivityScope;
import me.imzack.app.end.view.activity.TypeCreationActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = TypeCreationPresenterModule.class)
public interface TypeCreationComponent {

    void inject(TypeCreationActivity typeCreationActivity);
}
