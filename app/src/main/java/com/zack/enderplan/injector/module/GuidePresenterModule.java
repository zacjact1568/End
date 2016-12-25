package com.zack.enderplan.injector.module;

import com.zack.enderplan.view.contract.GuideViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class GuidePresenterModule {

    private final GuideViewContract mGuideViewContract;

    public GuidePresenterModule(GuideViewContract guideViewContract) {
        mGuideViewContract = guideViewContract;
    }

    @Provides
    GuideViewContract provideGuideViewContract() {
        return mGuideViewContract;
    }
}
