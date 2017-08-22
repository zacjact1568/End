package me.imzack.app.end.injector.module

import dagger.Module
import dagger.Provides
import me.imzack.app.end.view.contract.AllTypesViewContract

@Module
class AllTypesPresenterModule(private val mAllTypesViewContract: AllTypesViewContract) {

    @Provides
    fun provideAllTypesViewContract() = mAllTypesViewContract
}
