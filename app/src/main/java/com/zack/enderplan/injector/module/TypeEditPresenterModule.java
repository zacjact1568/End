package com.zack.enderplan.injector.module;

import com.zack.enderplan.view.contract.TypeEditViewContract;

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
