package com.zack.enderplan.injector.module;

import android.support.v4.app.FragmentManager;

import com.zack.enderplan.view.contract.AboutViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class AboutPresenterModule {

    private final AboutViewContract mAboutViewContract;
    private final FragmentManager mFragmentManager;

    public AboutPresenterModule(AboutViewContract aboutViewContract, FragmentManager fragmentManager) {
        mAboutViewContract = aboutViewContract;
        mFragmentManager = fragmentManager;
    }

    @Provides
    AboutViewContract provideAboutViewContract() {
        return mAboutViewContract;
    }

    @Provides
    FragmentManager provideFragmentManager() {
        return mFragmentManager;
    }
}
