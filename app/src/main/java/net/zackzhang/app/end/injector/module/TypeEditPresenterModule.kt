package net.zackzhang.app.end.injector.module

import dagger.Module
import dagger.Provides
import net.zackzhang.app.end.view.contract.TypeEditViewContract

@Module
class TypeEditPresenterModule(private val mTypeEditViewContract: TypeEditViewContract, private val mTypeListPosition: Int) {

    @Provides
    fun provideEditTypeViewContract() = mTypeEditViewContract

    @Provides
    fun provideTypeListPosition() = mTypeListPosition
}
