package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.MyPlansPresenterModule;
import me.imzack.app.ender.injector.scope.FragmentScope;
import me.imzack.app.ender.view.fragment.MyPlansFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = MyPlansPresenterModule.class)
public interface MyPlansComponent {

    void inject(MyPlansFragment myPlansFragment);
}
