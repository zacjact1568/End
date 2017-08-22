package me.imzack.app.end.injector.module

import android.support.v4.app.FragmentManager
import dagger.Module
import dagger.Provides
import me.imzack.app.end.view.contract.GuideViewContract

@Module
class GuidePresenterModule(private val mGuideViewContract: GuideViewContract, private val mFragmentManager: FragmentManager) {

    @Provides
    fun provideGuideViewContract() = mGuideViewContract

    @Provides
    fun provideFragmentManager() = mFragmentManager
}
