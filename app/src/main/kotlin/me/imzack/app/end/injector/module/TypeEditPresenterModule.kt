package me.imzack.app.end.injector.module

import dagger.Module
import dagger.Provides
import me.imzack.app.end.view.contract.TypeEditViewContract

@Module
class TypeEditPresenterModule(private val mTypeEditViewContract: TypeEditViewContract, private val mTypeListPosition: Int) {

    @Provides
    fun provideEditTypeViewContract() = mTypeEditViewContract

    @Provides
    fun provideTypeListPosition() = mTypeListPosition
}
