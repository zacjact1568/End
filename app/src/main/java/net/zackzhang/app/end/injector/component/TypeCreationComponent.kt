package net.zackzhang.app.end.injector.component

import dagger.Component
import net.zackzhang.app.end.injector.module.TypeCreationPresenterModule
import net.zackzhang.app.end.injector.scope.ActivityScope
import net.zackzhang.app.end.view.activity.TypeCreationActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(TypeCreationPresenterModule::class))
interface TypeCreationComponent {

    fun inject(typeCreationActivity: TypeCreationActivity)
}
