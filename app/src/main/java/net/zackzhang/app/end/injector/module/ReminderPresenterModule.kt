package net.zackzhang.app.end.injector.module

import dagger.Module
import dagger.Provides
import net.zackzhang.app.end.view.contract.ReminderViewContract

@Module
class ReminderPresenterModule(private val mReminderViewContract: ReminderViewContract, private val mPlanListPosition: Int) {

    @Provides
    fun provideReminderViewContract() = mReminderViewContract

    @Provides
    fun providePlanListPosition() = mPlanListPosition
}
