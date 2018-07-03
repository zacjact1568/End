package net.zackzhang.app.end.injector.module

import dagger.Module
import dagger.Provides
import net.zackzhang.app.end.view.contract.HomeViewContract

@Module
class HomePresenterModule(private val mHomeViewContract: HomeViewContract) {

    @Provides
    fun provideHomeViewContract() = mHomeViewContract
}
