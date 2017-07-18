package me.imzack.app.ender.injector.component;

import me.imzack.app.ender.injector.module.AllTypesPresenterModule;
import me.imzack.app.ender.injector.scope.FragmentScope;
import me.imzack.app.ender.view.fragment.AllTypesFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = AllTypesPresenterModule.class)
public interface AllTypesComponent {

    void inject(AllTypesFragment allTypesFragment);
}
