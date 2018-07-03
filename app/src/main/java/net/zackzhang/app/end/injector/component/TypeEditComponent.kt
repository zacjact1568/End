package net.zackzhang.app.end.injector.component

import dagger.Component
import net.zackzhang.app.end.injector.module.TypeEditPresenterModule
import net.zackzhang.app.end.injector.scope.ActivityScope
import net.zackzhang.app.end.view.activity.TypeEditActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(TypeEditPresenterModule::class))
interface TypeEditComponent {

    fun inject(typeEditActivity: TypeEditActivity)
}
