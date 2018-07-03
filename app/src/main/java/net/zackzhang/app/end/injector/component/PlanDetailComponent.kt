package net.zackzhang.app.end.injector.component

import dagger.Component
import net.zackzhang.app.end.injector.module.PlanDetailPresenterModule
import net.zackzhang.app.end.injector.scope.ActivityScope
import net.zackzhang.app.end.view.activity.PlanDetailActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PlanDetailPresenterModule::class))
interface PlanDetailComponent {

    fun inject(planDetailActivity: PlanDetailActivity)
}
