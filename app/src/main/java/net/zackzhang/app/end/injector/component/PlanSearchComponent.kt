package net.zackzhang.app.end.injector.component

import dagger.Component
import net.zackzhang.app.end.injector.module.PlanSearchPresenterModule
import net.zackzhang.app.end.injector.scope.ActivityScope
import net.zackzhang.app.end.view.activity.PlanSearchActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PlanSearchPresenterModule::class))
interface PlanSearchComponent {

    fun inject(planSearchActivity: PlanSearchActivity)
}
