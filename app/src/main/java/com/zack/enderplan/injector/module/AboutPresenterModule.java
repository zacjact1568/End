package com.zack.enderplan.injector.module;

import android.hardware.SensorManager;
import android.support.v4.app.FragmentManager;

import com.zack.enderplan.view.contract.AboutViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class AboutPresenterModule {

    private final AboutViewContract mAboutViewContract;
    private final FragmentManager mFragmentManager;
    private final SensorManager mSensorManager;

    public AboutPresenterModule(AboutViewContract aboutViewContract, FragmentManager fragmentManager, SensorManager sensorManager) {
        mAboutViewContract = aboutViewContract;
        mFragmentManager = fragmentManager;
        mSensorManager = sensorManager;
    }

    @Provides
    AboutViewContract provideAboutViewContract() {
        return mAboutViewContract;
    }

    @Provides
    FragmentManager provideFragmentManager() {
        return mFragmentManager;
    }

    @Provides
    SensorManager provideSensorManager() {
        return mSensorManager;
    }
}
