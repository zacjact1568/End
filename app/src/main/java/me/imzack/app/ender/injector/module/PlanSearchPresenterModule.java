package me.imzack.app.ender.injector.module;

import me.imzack.app.ender.view.contract.PlanSearchViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class PlanSearchPresenterModule {

    private final PlanSearchViewContract mPlanSearchViewContract;

    public PlanSearchPresenterModule(PlanSearchViewContract planSearchViewContract) {
        mPlanSearchViewContract = planSearchViewContract;
    }

    @Provides
    PlanSearchViewContract providePlanSearchViewContract() {
        return mPlanSearchViewContract;
    }
}
