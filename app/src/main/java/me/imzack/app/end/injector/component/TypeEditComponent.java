package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.TypeEditPresenterModule;
import me.imzack.app.end.injector.scope.ActivityScope;
import me.imzack.app.end.view.activity.TypeEditActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = TypeEditPresenterModule.class)
public interface TypeEditComponent {

    void inject(TypeEditActivity typeEditActivity);
}
