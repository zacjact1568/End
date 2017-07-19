package me.imzack.app.end.injector.module;

import me.imzack.app.end.view.contract.PlanDetailViewContract;

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
