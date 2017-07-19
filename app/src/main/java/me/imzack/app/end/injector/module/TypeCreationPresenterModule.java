package me.imzack.app.end.injector.module;

import me.imzack.app.end.App;
import me.imzack.app.end.R;
import me.imzack.app.end.util.ColorUtil;
import me.imzack.app.end.util.ResourceUtil;
import me.imzack.app.end.util.CommonUtil;
import me.imzack.app.end.model.DataManager;
import me.imzack.app.end.model.bean.Type;
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

    @Provides
    Type provideType() {
        DataManager dataManager = App.getDataManager();
        //按顺序产生未使用过的新类型名称
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
        //随机产生未使用过的颜色
        String color;
        while (true) {
            color = ColorUtil.makeColor();
            if (!dataManager.isTypeMarkColorUsed(color)) break;
        }
        return new Type(
                CommonUtil.makeCode(),
                typeName.toString(),
                color,
                dataManager.getTypeCount()
        );
    }
}
