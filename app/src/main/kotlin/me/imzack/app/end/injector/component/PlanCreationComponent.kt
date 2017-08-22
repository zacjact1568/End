package me.imzack.app.end.injector.component

import dagger.Component
import me.imzack.app.end.injector.module.PlanCreationPresenterModule
import me.imzack.app.end.injector.scope.ActivityScope
import me.imzack.app.end.view.activity.PlanCreationActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PlanCreationPresenterModule::class))
interface PlanCreationComponent {

    fun inject(planCreationActivity: PlanCreationActivity)
}
