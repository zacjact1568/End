package me.imzack.app.end.injector.component

import dagger.Component
import me.imzack.app.end.injector.module.GuidePresenterModule
import me.imzack.app.end.injector.scope.ActivityScope
import me.imzack.app.end.view.activity.GuideActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(GuidePresenterModule::class))
interface GuideComponent {

    fun inject(guideActivity: GuideActivity)
}
