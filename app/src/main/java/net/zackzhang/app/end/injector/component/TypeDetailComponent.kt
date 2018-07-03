package net.zackzhang.app.end.injector.component

import dagger.Component
import net.zackzhang.app.end.injector.module.TypeDetailPresenterModule
import net.zackzhang.app.end.injector.scope.ActivityScope
import net.zackzhang.app.end.view.activity.TypeDetailActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(TypeDetailPresenterModule::class))
interface TypeDetailComponent {

    fun inject(typeDetailActivity: TypeDetailActivity)
}
