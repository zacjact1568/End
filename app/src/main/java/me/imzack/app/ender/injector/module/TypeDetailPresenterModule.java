package me.imzack.app.ender.injector.module;

import me.imzack.app.ender.view.contract.TypeDetailViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class TypeDetailPresenterModule {

    private final TypeDetailViewContract mTypeDetailViewContract;
    private final int mTypeListPosition;

    public TypeDetailPresenterModule(TypeDetailViewContract typeDetailViewContract, int typeListPosition) {
        mTypeDetailViewContract = typeDetailViewContract;
        mTypeListPosition = typeListPosition;
    }

    @Provides
    TypeDetailViewContract provideTypeDetailViewContract() {
        return mTypeDetailViewContract;
    }

    @Provides
    int provideTypeListPosition() {
        return mTypeListPosition;
    }
}
