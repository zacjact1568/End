package com.zack.enderplan.injector.module;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.view.contract.TypeCreationViewContract;

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

    @Provides
    Type provideType() {
        DataManager dataManager = App.getDataManager();
        return new Type(
                Util.makeCode(),
                Util.getString(R.string.text_new_type_name),
                dataManager.getRandomTypeMarkColor(),
                dataManager.getTypeCount()
        );
    }
}
