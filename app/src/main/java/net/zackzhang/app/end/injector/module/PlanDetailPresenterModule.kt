package net.zackzhang.app.end.injector.module

import dagger.Module
import dagger.Provides
import net.zackzhang.app.end.view.contract.PlanDetailViewContract

@Module
class PlanDetailPresenterModule(private val mPlanDetailViewContract: PlanDetailViewContract, private val mPlanListPosition: Int) {

    @Provides
    fun providePlanDetailViewContract() = mPlanDetailViewContract

    @Provides
    fun providePlanListPosition() = mPlanListPosition
}
