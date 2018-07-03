package net.zackzhang.app.end.injector.component

import dagger.Component
import net.zackzhang.app.end.injector.module.HomePresenterModule
import net.zackzhang.app.end.injector.scope.ActivityScope
import net.zackzhang.app.end.view.activity.HomeActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(HomePresenterModule::class))
interface HomeComponent {

    fun inject(homeActivity: HomeActivity)
}
