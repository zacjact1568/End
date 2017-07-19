package me.imzack.app.end.injector.module;

import me.imzack.app.end.view.contract.AllTypesViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class AllTypesPresenterModule {

    private final AllTypesViewContract mAllTypesViewContract;

    public AllTypesPresenterModule(AllTypesViewContract allTypesViewContract) {
        mAllTypesViewContract = allTypesViewContract;
    }

    @Provides
    AllTypesViewContract provideAllTypesViewContract() {
        return mAllTypesViewContract;
    }
}
