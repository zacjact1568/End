package me.imzack.app.ender.injector.module;

import me.imzack.app.ender.view.contract.FirstPlanViewContract;

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
