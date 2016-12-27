package com.zack.enderplan.injector.module;

import com.zack.enderplan.common.Util;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.view.contract.PlanCreationViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class PlanCreationPresenterModule {

    private final PlanCreationViewContract mPlanCreationViewContract;

    public PlanCreationPresenterModule(PlanCreationViewContract planCreationViewContract) {
        mPlanCreationViewContract = planCreationViewContract;
    }

    @Provides
    PlanCreationViewContract provideCreatePlanViewContract() {
        return mPlanCreationViewContract;
    }

    @Provides
    Plan providePlan() {
        return new Plan(Util.makeCode());
    }
}
