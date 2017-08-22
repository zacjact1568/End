package me.imzack.app.end.injector.component

import dagger.Component
import me.imzack.app.end.injector.module.TypeSearchPresenterModule
import me.imzack.app.end.injector.scope.ActivityScope
import me.imzack.app.end.view.activity.TypeSearchActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(TypeSearchPresenterModule::class))
interface TypeSearchComponent {

    fun inject(typeSearchActivity: TypeSearchActivity)
}
