package com.zack.enderplan.injector.module;

import com.zack.enderplan.view.contract.FirstPlanViewContract;
import com.zack.enderplan.view.contract.HomeViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class FirstPlanPresenterModule {

    private final FirstPlanViewContract mFirstPlanViewContract;

    public FirstPlanPresenterModule(FirstPlanViewContract firstPlanViewContract) {
        mFirstPlanViewContract = firstPlanViewContract;
    }

    @Provides
    FirstPlanViewContract provideFirstPlanViewContract() {
        return mFirstPlanViewContract;
    }
}
