package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.AboutPresenterModule;
import me.imzack.app.end.injector.scope.ActivityScope;
import me.imzack.app.end.view.activity.AboutActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = AboutPresenterModule.class)
public interface AboutComponent {

    void inject(AboutActivity aboutActivity);
}
