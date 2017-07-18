package me.imzack.app.ender.injector.module;

import me.imzack.app.ender.util.CommonUtil;
import me.imzack.app.ender.model.bean.Plan;
import me.imzack.app.ender.view.contract.PlanCreationViewContract;

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
        return new Plan(CommonUtil.makeCode());
    }
}
