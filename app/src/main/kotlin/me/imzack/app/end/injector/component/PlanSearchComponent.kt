package me.imzack.app.end.injector.component

import dagger.Component
import me.imzack.app.end.injector.module.PlanSearchPresenterModule
import me.imzack.app.end.injector.scope.ActivityScope
import me.imzack.app.end.view.activity.PlanSearchActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PlanSearchPresenterModule::class))
interface PlanSearchComponent {

    fun inject(planSearchActivity: PlanSearchActivity)
}
