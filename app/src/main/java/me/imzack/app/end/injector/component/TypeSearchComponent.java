package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.TypeSearchPresenterModule;
import me.imzack.app.end.injector.scope.ActivityScope;
import me.imzack.app.end.view.activity.TypeSearchActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = TypeSearchPresenterModule.class)
public interface TypeSearchComponent {

    void inject(TypeSearchActivity typeSearchActivity);
}
