package me.imzack.app.end.injector.module;

import me.imzack.app.end.view.contract.TypeSearchViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class TypeSearchPresenterModule {

    private final TypeSearchViewContract mTypeSearchViewContract;

    public TypeSearchPresenterModule(TypeSearchViewContract typeSearchViewContract) {
        mTypeSearchViewContract = typeSearchViewContract;
    }

    @Provides
    TypeSearchViewContract provideTypeSearchViewContract() {
        return mTypeSearchViewContract;
    }
}
