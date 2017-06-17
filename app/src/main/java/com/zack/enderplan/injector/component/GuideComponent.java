package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.GuidePresenterModule;
import com.zack.enderplan.view.activity.GuideActivity;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = GuidePresenterModule.class)
public interface GuideComponent {

    void inject(GuideActivity guideActivity);
}
