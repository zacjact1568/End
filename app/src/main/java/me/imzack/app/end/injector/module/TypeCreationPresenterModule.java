package me.imzack.app.end.injector.module;

import me.imzack.app.end.view.contract.TypeCreationViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class TypeCreationPresenterModule {

    private final TypeCreationViewContract mTypeCreationViewContract;

    public TypeCreationPresenterModule(TypeCreationViewContract typeCreationViewContract) {
        mTypeCreationViewContract = typeCreationViewContract;
    }

    @Provides
    TypeCreationViewContract provideCreateTypeViewContract() {
        return mTypeCreationViewContract;
    }
}
