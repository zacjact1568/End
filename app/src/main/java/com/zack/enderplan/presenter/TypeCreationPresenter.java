package com.zack.enderplan.presenter;

import android.graphics.Color;
import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.TypeMarkPattern;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.view.contract.TypeCreationViewContract;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class TypeCreationPresenter extends BasePresenter {

    private TypeCreationViewContract mTypeCreationViewContract;
    private DataManager mDataManager;
    private Type mType;

    @Inject
    public TypeCreationPresenter(TypeCreationViewContract typeCreationViewContract, Type type, DataManager dataManager) {
        mTypeCreationViewContract = typeCreationViewContract;
        mType = type;
        mDataManager = dataManager;
    }

    @Override
    public void attach() {
        mTypeCreationViewContract.showInitialView(new FormattedType(
                Color.parseColor(mType.getTypeMarkColor()),
                mDataManager.getTypeMarkColorName(mType.getTypeMarkColor()),
                mType.getTypeName(),
                mType.getTypeName().substring(0, 1)
        ));
    }

    @Override
    public void detach() {
        mTypeCreationViewContract = null;
    }

    public void notifyTypeNameTextChanged(String typeName) {
        mType.setTypeName(typeName);
        if (TextUtils.isEmpty(typeName)) {
            mTypeCreationViewContract.onTypeNameChanged(Util.getString(R.string.text_empty_type_name), "-", false);
        } else {
            mTypeCreationViewContract.onTypeNameChanged(typeName, typeName.substring(0, 1), true);
        }
    }

    public void notifySettingTypeMarkColor() {
        mTypeCreationViewContract.showTypeMarkColorPickerDialog(mType.getTypeMarkColor());
    }

    public void notifyTypeMarkColorSelected(TypeMarkColor typeMarkColor) {
        String colorHex = typeMarkColor.getColorHex();
        mType.setTypeMarkColor(colorHex);
        mTypeCreationViewContract.onTypeMarkColorChanged(Color.parseColor(colorHex), typeMarkColor.getColorName());
    }

    public void notifySettingTypeMarkPattern() {
        mTypeCreationViewContract.showTypeMarkPatternPickerDialog(mType.getTypeMarkPattern());
    }

    public void notifyTypeMarkPatternSelected(TypeMarkPattern typeMarkPattern) {
        boolean hasPattern = typeMarkPattern != null;
        String patternFn = hasPattern ? typeMarkPattern.getPatternFn() : null;
        mType.setTypeMarkPattern(patternFn);
        mTypeCreationViewContract.onTypeMarkPatternChanged(
                hasPattern,
                Util.getDrawableResourceId(patternFn),
                hasPattern ? typeMarkPattern.getPatternName() : null
        );
    }

    public void notifyCreateButtonClicked() {
        if (TextUtils.isEmpty(mType.getTypeName())) {
            mTypeCreationViewContract.showToast(R.string.toast_empty_type_name);
        } else if (mDataManager.isTypeNameUsed(mType.getTypeName())) {
            mTypeCreationViewContract.showToast(R.string.toast_type_name_exists);
        } else if (mDataManager.isTypeMarkUsed(mType.getTypeMarkColor(), mType.getTypeMarkPattern())) {
            mTypeCreationViewContract.showToast(R.string.toast_type_mark_exists);
        } else {
            mDataManager.notifyTypeCreated(mType);
            EventBus.getDefault().post(new TypeCreatedEvent(
                    getPresenterName(),
                    mDataManager.getRecentlyCreatedType().getTypeCode(),
                    mDataManager.getRecentlyCreatedTypeLocation()
            ));
            mTypeCreationViewContract.exit();
        }
    }

    public void notifyCancelButtonClicked() {
        //TODO 判断是否已编辑过
        mTypeCreationViewContract.exit();
    }
}
