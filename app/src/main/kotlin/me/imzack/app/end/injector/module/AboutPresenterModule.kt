package me.imzack.app.end.injector.module

import android.hardware.SensorManager
import android.support.v4.app.FragmentManager
import dagger.Module
import dagger.Provides
import me.imzack.app.end.view.contract.AboutViewContract

@Module
class AboutPresenterModule(
        private val mAboutViewContract: AboutViewContract,
        private val mFragmentManager: FragmentManager,
        private val mSensorManager: SensorManager
) {

    @Provides
    fun provideAboutViewContract() = mAboutViewContract

    @Provides
    fun provideFragmentManager() = mFragmentManager

    @Provides
    fun provideSensorManager() = mSensorManager
}
