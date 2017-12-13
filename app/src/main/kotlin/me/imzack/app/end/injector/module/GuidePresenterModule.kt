package me.imzack.app.end.injector.module

import dagger.Module
import dagger.Provides
import me.imzack.app.end.view.contract.GuideViewContract

@Module
class GuidePresenterModule(private val mGuideViewContract: GuideViewContract) {

    @Provides
    fun provideGuideViewContract() = mGuideViewContract
}
