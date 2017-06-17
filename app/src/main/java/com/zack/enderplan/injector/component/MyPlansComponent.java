package com.zack.enderplan.injector.component;

import com.zack.enderplan.injector.module.MyPlansPresenterModule;
import com.zack.enderplan.view.fragment.MyPlansFragment;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = MyPlansPresenterModule.class)
public interface MyPlansComponent {

    void inject(MyPlansFragment myPlansFragment);
}
