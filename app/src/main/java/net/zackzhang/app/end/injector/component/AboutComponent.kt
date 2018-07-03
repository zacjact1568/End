package net.zackzhang.app.end.injector.component

import dagger.Component
import net.zackzhang.app.end.injector.module.AboutPresenterModule
import net.zackzhang.app.end.injector.scope.ActivityScope
import net.zackzhang.app.end.view.activity.AboutActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(AboutPresenterModule::class))
interface AboutComponent {

    fun inject(aboutActivity: AboutActivity)
}
