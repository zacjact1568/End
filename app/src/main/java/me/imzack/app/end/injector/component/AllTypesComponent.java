package me.imzack.app.end.injector.component;

import me.imzack.app.end.injector.module.AllTypesPresenterModule;
import me.imzack.app.end.injector.scope.FragmentScope;
import me.imzack.app.end.view.fragment.AllTypesFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = AllTypesPresenterModule.class)
public interface AllTypesComponent {

    void inject(AllTypesFragment allTypesFragment);
}
