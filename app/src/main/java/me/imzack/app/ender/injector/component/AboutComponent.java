package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.AboutPresenterModule;
import me.imzack.app.ender.injector.scope.ActivityScope;
import me.imzack.app.ender.view.activity.AboutActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = AboutPresenterModule.class)
public interface AboutComponent {

    void inject(AboutActivity aboutActivity);
}
