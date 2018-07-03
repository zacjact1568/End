package net.zackzhang.app.end.injector.module

import dagger.Module
import dagger.Provides
import net.zackzhang.app.end.view.contract.TypeSearchViewContract

@Module
class TypeSearchPresenterModule(private val mTypeSearchViewContract: TypeSearchViewContract) {

    @Provides
    fun provideTypeSearchViewContract() = mTypeSearchViewContract
}
