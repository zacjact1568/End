package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.TypeSearchPresenterModule;
import com.zack.enderplan.injector.scope.ActivityScope;
import com.zack.enderplan.view.activity.TypeSearchActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = TypeSearchPresenterModule.class)
public interface TypeSearchComponent {

    void inject(TypeSearchActivity typeSearchActivity);
}
