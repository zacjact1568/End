package me.imzack.app.end.injector.module

import dagger.Module
import dagger.Provides
import me.imzack.app.end.view.contract.TypeCreationViewContract

@Module
class TypeCreationPresenterModule(private val mTypeCreationViewContract: TypeCreationViewContract) {

    @Provides
    fun provideCreateTypeViewContract() = mTypeCreationViewContract
}
