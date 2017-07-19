package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.GuidePresenterModule;
import me.imzack.app.end.injector.scope.ActivityScope;
import me.imzack.app.end.view.activity.GuideActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = GuidePresenterModule.class)
public interface GuideComponent {

    void inject(GuideActivity guideActivity);
}
