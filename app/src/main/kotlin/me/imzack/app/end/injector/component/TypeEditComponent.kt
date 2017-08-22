package me.imzack.app.end.injector.component

import dagger.Component
import me.imzack.app.end.injector.module.TypeEditPresenterModule
import me.imzack.app.end.injector.scope.ActivityScope
import me.imzack.app.end.view.activity.TypeEditActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(TypeEditPresenterModule::class))
interface TypeEditComponent {

    fun inject(typeEditActivity: TypeEditActivity)
}
