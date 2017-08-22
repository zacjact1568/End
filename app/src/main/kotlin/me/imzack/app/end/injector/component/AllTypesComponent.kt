package me.imzack.app.end.injector.component

import dagger.Component
import me.imzack.app.end.injector.module.AllTypesPresenterModule
import me.imzack.app.end.injector.scope.FragmentScope
import me.imzack.app.end.view.fragment.AllTypesFragment

@FragmentScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(AllTypesPresenterModule::class))
interface AllTypesComponent {

    fun inject(allTypesFragment: AllTypesFragment)
}
