package net.zackzhang.app.end.injector.module

import dagger.Module
import dagger.Provides
import net.zackzhang.app.end.view.contract.TypeCreationViewContract

@Module
class TypeCreationPresenterModule(private val mTypeCreationViewContract: TypeCreationViewContract) {

    @Provides
    fun provideCreateTypeViewContract() = mTypeCreationViewContract
}
