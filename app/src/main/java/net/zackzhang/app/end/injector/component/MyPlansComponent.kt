package net.zackzhang.app.end.injector.component

import dagger.Component
import net.zackzhang.app.end.injector.module.MyPlansPresenterModule
import net.zackzhang.app.end.injector.scope.FragmentScope
import net.zackzhang.app.end.view.fragment.MyPlansFragment

@FragmentScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(MyPlansPresenterModule::class))
interface MyPlansComponent {

    fun inject(myPlansFragment: MyPlansFragment)
}
