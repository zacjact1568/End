package me.imzack.app.end.injector.component

import dagger.Component
import me.imzack.app.end.injector.module.HomePresenterModule
import me.imzack.app.end.injector.scope.ActivityScope
import me.imzack.app.end.view.activity.HomeActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(HomePresenterModule::class))
interface HomeComponent {

    fun inject(homeActivity: HomeActivity)
}
