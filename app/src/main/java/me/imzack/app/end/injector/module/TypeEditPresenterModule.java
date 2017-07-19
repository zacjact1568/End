package me.imzack.app.end.injector.module;

import me.imzack.app.end.view.contract.TypeEditViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class TypeEditPresenterModule {

    private final TypeEditViewContract mTypeEditViewContract;
    private final int mTypeListPosition;

    public TypeEditPresenterModule(TypeEditViewContract typeEditViewContract, int typeListPosition) {
        mTypeEditViewContract = typeEditViewContract;
        mTypeListPosition = typeListPosition;
    }

    @Provides
    TypeEditViewContract provideEditTypeViewContract() {
        return mTypeEditViewContract;
    }

    @Provides
    int provideTypeListPosition() {
        return mTypeListPosition;
    }
}
