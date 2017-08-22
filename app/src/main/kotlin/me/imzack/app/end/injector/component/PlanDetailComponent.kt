package me.imzack.app.end.injector.component

import dagger.Component
import me.imzack.app.end.injector.module.PlanDetailPresenterModule
import me.imzack.app.end.injector.scope.ActivityScope
import me.imzack.app.end.view.activity.PlanDetailActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PlanDetailPresenterModule::class))
interface PlanDetailComponent {

    fun inject(planDetailActivity: PlanDetailActivity)
}
