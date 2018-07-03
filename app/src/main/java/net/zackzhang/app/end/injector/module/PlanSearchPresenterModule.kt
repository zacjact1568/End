package net.zackzhang.app.end.injector.module

import dagger.Module
import dagger.Provides
import net.zackzhang.app.end.view.contract.PlanSearchViewContract

@Module
class PlanSearchPresenterModule(private val mPlanSearchViewContract: PlanSearchViewContract) {

    @Provides
    fun providePlanSearchViewContract() = mPlanSearchViewContract
}
