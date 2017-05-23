package com.zack.enderplan.injector.module;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.CommonUtil;
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
        String base = ResourceUtil.getString(R.string.text_new_type_name);
        StringBuilder typeName = new StringBuilder(base);
        int i = 1;
        while (dataManager.isTypeNameUsed(typeName.toString())) {
            if (base.length() == typeName.length()) {
                //还没加空格
                typeName.append(" ");
            }
            typeName.replace(base.length() + 1, typeName.length(), String.valueOf(i));
            i++;
        }
        return new Type(
                CommonUtil.makeCode(),
                typeName.toString(),
                dataManager.getRandomTypeMarkColor(),
                dataManager.getTypeCount()
        );
    }
}
