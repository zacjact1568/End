package com.zack.enderplan.injector.module;

import com.zack.enderplan.view.contract.HomeViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class HomePresenterModule {

    private final HomeViewContract mHomeViewContract;

    public HomePresenterModule(HomeViewContract homeViewContract) {
        mHomeViewContract = homeViewContract;
    }

    @Provides
    HomeViewContract provideHomeViewContract() {
        return mHomeViewContract;
    }
}
