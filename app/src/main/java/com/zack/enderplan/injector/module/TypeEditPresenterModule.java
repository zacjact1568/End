package com.zack.enderplan.injector.module;

import com.zack.enderplan.App;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.view.contract.EditTypeViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class TypeEditPresenterModule {

    private final EditTypeViewContract mEditTypeViewContract;
    private final int mTypeListPosition;

    public TypeEditPresenterModule(EditTypeViewContract editTypeViewContract, int typeListPosition) {
        mEditTypeViewContract = editTypeViewContract;
        mTypeListPosition = typeListPosition;
    }

    @Provides
    EditTypeViewContract provideEditTypeViewContract() {
        return mEditTypeViewContract;
    }

    @Provides
    int provideTypeListPosition() {
        return mTypeListPosition;
    }
}
