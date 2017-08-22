package me.imzack.app.end.injector.component

import dagger.Component
import me.imzack.app.end.injector.module.MyPlansPresenterModule
import me.imzack.app.end.injector.scope.FragmentScope
import me.imzack.app.end.view.fragment.MyPlansFragment

@FragmentScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(MyPlansPresenterModule::class))
interface MyPlansComponent {

    fun inject(myPlansFragment: MyPlansFragment)
}
