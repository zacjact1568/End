package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.TypeDetailPresenterModule;
import me.imzack.app.end.injector.scope.ActivityScope;
import me.imzack.app.end.view.activity.TypeDetailActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = TypeDetailPresenterModule.class)
public interface TypeDetailComponent {

    void inject(TypeDetailActivity typeDetailActivity);
}
