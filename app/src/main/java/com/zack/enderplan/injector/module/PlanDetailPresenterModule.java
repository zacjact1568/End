package com.zack.enderplan.injector.module;

import com.zack.enderplan.view.contract.PlanDetailViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class PlanDetailPresenterModule {

    private final PlanDetailViewContract mPlanDetailViewContract;
    private final int mPlanListPosition;

    public PlanDetailPresenterModule(PlanDetailViewContract planDetailViewContract, int planListPosition) {
        mPlanDetailViewContract = planDetailViewContract;
        mPlanListPosition = planListPosition;
    }

    @Provides
    PlanDetailViewContract providePlanDetailViewContract() {
        return mPlanDetailViewContract;
    }

    @Provides
    int providePlanListPosition() {
        return mPlanListPosition;
    }
}
