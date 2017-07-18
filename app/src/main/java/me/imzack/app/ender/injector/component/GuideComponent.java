package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.GuidePresenterModule;
import me.imzack.app.ender.injector.scope.ActivityScope;
import me.imzack.app.ender.view.activity.GuideActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = GuidePresenterModule.class)
public interface GuideComponent {

    void inject(GuideActivity guideActivity);
}
