package net.zackzhang.app.end.injector.component

import dagger.Component
import net.zackzhang.app.end.injector.module.PlanCreationPresenterModule
import net.zackzhang.app.end.injector.scope.ActivityScope
import net.zackzhang.app.end.view.activity.PlanCreationActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PlanCreationPresenterModule::class))
interface PlanCreationComponent {

    fun inject(planCreationActivity: PlanCreationActivity)
}
