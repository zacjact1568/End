package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.MyPlansPresenterModule;
import me.imzack.app.end.injector.scope.FragmentScope;
import me.imzack.app.end.view.fragment.MyPlansFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = MyPlansPresenterModule.class)
public interface MyPlansComponent {

    void inject(MyPlansFragment myPlansFragment);
}
