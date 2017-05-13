package com.zack.enderplan.injector.module;

import com.zack.enderplan.view.contract.TypeEditViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class TypeEditPresenterModule {

    private final TypeEditViewContract mTypeEditViewContract;
    private final int mTypeListPosition;
    private final boolean mEnableTransition;

    public TypeEditPresenterModule(TypeEditViewContract typeEditViewContract, int typeListPosition, boolean enableTransition) {
        mTypeEditViewContract = typeEditViewContract;
        mTypeListPosition = typeListPosition;
        mEnableTransition = enableTransition;
    }

    @Provides
    TypeEditViewContract provideEditTypeViewContract() {
        return mTypeEditViewContract;
    }

    @Provides
    int provideTypeListPosition() {
        return mTypeListPosition;
    }

    @Provides
    boolean provideEnableTransition() {
        return mEnableTransition;
    }
}
