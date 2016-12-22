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
import com.zack.enderplan.view.contract.CreateTypeViewContract;

import org.greenrobot.eventbus.EventBus;

public class CreateTypePresenter extends BasePresenter<CreateTypeViewContract> {

    private CreateTypeViewContract mCreateTypeViewContract;
    private DataManager mDataManager;
    private Type mType;

    public CreateTypePresenter(CreateTypeViewContract createTypeViewContract) {
        attachView(createTypeViewContract);
        mDataManager = DataManager.getInstance();
        mType = new Type(Util.makeCode(), mDataManager.getTypeCount());
        mType.setTypeName(Util.getString(R.string.text_new_type_name));
        mType.setTypeMarkColor(mDataManager.getRandomTypeMarkColor());
    }

    @Override
    public void attachView(CreateTypeViewContract viewContract) {
        mCreateTypeViewContract = viewContract;
    }

    @Override
    public void detachView() {
        mCreateTypeViewContract = null;
    }

    public void setInitialView() {
        mCreateTypeViewContract.showInitialView(new FormattedType(
                Color.parseColor(mType.getTypeMarkColor()),
                mDataManager.getTypeMarkColorName(mType.getTypeMarkColor()),
                mType.getTypeName(),
                mType.getTypeName().substring(0, 1)
        ));
    }

    public void notifyTypeNameTextChanged(String typeName) {
        mType.setTypeName(typeName);
        if (TextUtils.isEmpty(typeName)) {
            mCreateTypeViewContract.onTypeNameChanged(Util.getString(R.string.text_empty_type_name), "-", false);
        } else {
            mCreateTypeViewContract.onTypeNameChanged(typeName, typeName.substring(0, 1), true);
        }
    }

    public void notifySettingTypeMarkColor() {
        mCreateTypeViewContract.showTypeMarkColorPickerDialog(mType.getTypeMarkColor());
    }

    public void notifyTypeMarkColorSelected(TypeMarkColor typeMarkColor) {
        String colorHex = typeMarkColor.getColorHex();
        mType.setTypeMarkColor(colorHex);
        mCreateTypeViewContract.onTypeMarkColorChanged(Color.parseColor(colorHex), typeMarkColor.getColorName());
    }

    public void notifySettingTypeMarkPattern() {
        mCreateTypeViewContract.showTypeMarkPatternPickerDialog(mType.getTypeMarkPattern());
    }

    public void notifyTypeMarkPatternSelected(TypeMarkPattern typeMarkPattern) {
        boolean hasPattern = typeMarkPattern != null;
        String patternFn = hasPattern ? typeMarkPattern.getPatternFn() : null;
        mType.setTypeMarkPattern(patternFn);
        mCreateTypeViewContract.onTypeMarkPatternChanged(
                hasPattern,
                Util.getDrawableResourceId(patternFn),
                hasPattern ? typeMarkPattern.getPatternName() : null
        );
    }

    public void notifyCreateButtonClicked() {
        if (mDataManager.isTypeNameUsed(mType.getTypeName())) {
            mCreateTypeViewContract.playShakeAnimation(Constant.TYPE_NAME);
            mCreateTypeViewContract.showToast(R.string.toast_type_name_exists);
        } else if (mDataManager.isTypeMarkUsed(mType.getTypeMarkColor(), mType.getTypeMarkPattern())) {
            mCreateTypeViewContract.playShakeAnimation(Constant.TYPE_MARK);
            mCreateTypeViewContract.showToast(R.string.toast_type_mark_exists);
        } else {
            mDataManager.notifyTypeCreated(mType);
            EventBus.getDefault().post(new TypeCreatedEvent(
                    getPresenterName(),
                    mDataManager.getRecentlyCreatedType().getTypeCode(),
                    mDataManager.getRecentlyCreatedTypeLocation()
            ));
            mCreateTypeViewContract.exit();
        }
    }

    public void notifyCancelButtonClicked() {
        //TODO 判断是否已编辑过
        mCreateTypeViewContract.exit();
    }
}
