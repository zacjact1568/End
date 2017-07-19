package me.imzack.app.end.injector.module;

import me.imzack.app.end.view.contract.PlanSearchViewContract;

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
