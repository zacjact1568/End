package net.zackzhang.app.end.injector.module

import dagger.Module
import dagger.Provides
import net.zackzhang.app.end.view.contract.TypeDetailViewContract

@Module
class TypeDetailPresenterModule(private val mTypeDetailViewContract: TypeDetailViewContract, private val mTypeListPosition: Int) {

    @Provides
    fun provideTypeDetailViewContract() = mTypeDetailViewContract

    @Provides
    fun provideTypeListPosition() = mTypeListPosition
}
