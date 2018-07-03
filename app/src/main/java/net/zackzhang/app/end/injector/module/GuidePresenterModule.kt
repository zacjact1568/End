package net.zackzhang.app.end.injector.module

import dagger.Module
import dagger.Provides
import net.zackzhang.app.end.view.contract.GuideViewContract

@Module
class GuidePresenterModule(private val mGuideViewContract: GuideViewContract) {

    @Provides
    fun provideGuideViewContract() = mGuideViewContract
}
