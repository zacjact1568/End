package net.zackzhang.app.end.injector.component

import dagger.Component
import net.zackzhang.app.end.injector.module.GuidePresenterModule
import net.zackzhang.app.end.injector.scope.ActivityScope
import net.zackzhang.app.end.view.activity.GuideActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(GuidePresenterModule::class))
interface GuideComponent {

    fun inject(guideActivity: GuideActivity)
}
