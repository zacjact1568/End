package net.zackzhang.app.end.injector.component

import dagger.Component
import net.zackzhang.app.end.injector.module.TypeSearchPresenterModule
import net.zackzhang.app.end.injector.scope.ActivityScope
import net.zackzhang.app.end.view.activity.TypeSearchActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(TypeSearchPresenterModule::class))
interface TypeSearchComponent {

    fun inject(typeSearchActivity: TypeSearchActivity)
}
