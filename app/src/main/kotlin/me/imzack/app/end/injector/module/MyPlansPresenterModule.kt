package me.imzack.app.end.injector.module

import dagger.Module
import dagger.Provides
import me.imzack.app.end.view.contract.MyPlansViewContract

@Module
class MyPlansPresenterModule(private val mMyPlansViewContract: MyPlansViewContract) {

    @Provides
    fun provideMyPlansViewContract() = mMyPlansViewContract
}
