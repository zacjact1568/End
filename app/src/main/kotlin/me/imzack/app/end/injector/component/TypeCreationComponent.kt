package me.imzack.app.end.injector.component

import dagger.Component
import me.imzack.app.end.injector.module.TypeCreationPresenterModule
import me.imzack.app.end.injector.scope.ActivityScope
import me.imzack.app.end.view.activity.TypeCreationActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(TypeCreationPresenterModule::class))
interface TypeCreationComponent {

    fun inject(typeCreationActivity: TypeCreationActivity)
}
