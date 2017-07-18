package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.TypeDetailPresenterModule;
import me.imzack.app.ender.injector.scope.ActivityScope;
import me.imzack.app.ender.view.activity.TypeDetailActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = TypeDetailPresenterModule.class)
public interface TypeDetailComponent {

    void inject(TypeDetailActivity typeDetailActivity);
}
