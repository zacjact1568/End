package me.imzack.app.end.injector.component

import dagger.Component
import me.imzack.app.end.injector.module.AboutPresenterModule
import me.imzack.app.end.injector.scope.ActivityScope
import me.imzack.app.end.view.activity.AboutActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(AboutPresenterModule::class))
interface AboutComponent {

    fun inject(aboutActivity: AboutActivity)
}
