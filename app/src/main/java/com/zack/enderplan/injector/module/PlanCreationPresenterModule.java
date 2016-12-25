package com.zack.enderplan.injector.module;

import com.zack.enderplan.common.Util;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.view.contract.CreatePlanViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class PlanCreationPresenterModule {

    private final CreatePlanViewContract mCreatePlanViewContract;

    public PlanCreationPresenterModule(CreatePlanViewContract createPlanViewContract) {
        mCreatePlanViewContract = createPlanViewContract;
    }

    @Provides
    CreatePlanViewContract provideCreatePlanViewContract() {
        return mCreatePlanViewContract;
    }

    @Provides
    Plan providePlan() {
        return new Plan(Util.makeCode());
    }
}
