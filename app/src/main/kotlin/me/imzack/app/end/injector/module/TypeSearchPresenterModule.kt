package me.imzack.app.end.injector.module

import dagger.Module
import dagger.Provides
import me.imzack.app.end.view.contract.TypeSearchViewContract

@Module
class TypeSearchPresenterModule(private val mTypeSearchViewContract: TypeSearchViewContract) {

    @Provides
    fun provideTypeSearchViewContract() = mTypeSearchViewContract
}
