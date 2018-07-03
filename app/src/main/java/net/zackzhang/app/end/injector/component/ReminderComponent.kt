package net.zackzhang.app.end.injector.component

import dagger.Component
import net.zackzhang.app.end.injector.module.ReminderPresenterModule
import net.zackzhang.app.end.injector.scope.ActivityScope
import net.zackzhang.app.end.view.activity.ReminderActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ReminderPresenterModule::class))
interface ReminderComponent {

    fun inject(reminderActivity: ReminderActivity)
}
