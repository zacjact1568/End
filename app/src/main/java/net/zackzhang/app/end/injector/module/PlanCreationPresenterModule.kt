package net.zackzhang.app.end.injector.module

import dagger.Module
import dagger.Provides
import net.zackzhang.app.end.view.contract.PlanCreationViewContract

@Module
class PlanCreationPresenterModule(private val mPlanCreationViewContract: PlanCreationViewContract) {

    @Provides
    fun provideCreatePlanViewContract() = mPlanCreationViewContract
}
