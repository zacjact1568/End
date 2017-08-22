package me.imzack.app.end.injector.component

import dagger.Component
import me.imzack.app.end.injector.module.TypeDetailPresenterModule
import me.imzack.app.end.injector.scope.ActivityScope
import me.imzack.app.end.view.activity.TypeDetailActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(TypeDetailPresenterModule::class))
interface TypeDetailComponent {

    fun inject(typeDetailActivity: TypeDetailActivity)
}
