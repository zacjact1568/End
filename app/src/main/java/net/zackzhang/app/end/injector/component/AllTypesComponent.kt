package net.zackzhang.app.end.injector.component

import dagger.Component
import net.zackzhang.app.end.injector.module.AllTypesPresenterModule
import net.zackzhang.app.end.injector.scope.FragmentScope
import net.zackzhang.app.end.view.fragment.AllTypesFragment

@FragmentScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(AllTypesPresenterModule::class))
interface AllTypesComponent {

    fun inject(allTypesFragment: AllTypesFragment)
}
