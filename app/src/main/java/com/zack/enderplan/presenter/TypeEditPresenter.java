package com.zack.enderplan.presenter;

import android.graphics.Color;
import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.contract.TypeEditViewContract;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.model.bean.TypeMarkPattern;
import com.zack.enderplan.common.Util;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class TypeEditPresenter extends BasePresenter {

    private TypeEditViewContract mTypeEditViewContract;
    private DataManager mDataManager;
    private EventBus mEventBus;
    private Type mType;
    private int mTypeListPosition;

    @Inject
    public TypeEditPresenter(TypeEditViewContract typeEditViewContract, int typeListPosition, DataManager dataManager, EventBus eventBus) {
        mTypeEditViewContract = typeEditViewContract;
        mTypeListPosition = typeListPosition;
        mDataManager = dataManager;
        mEventBus = eventBus;

        mType = mDataManager.getType(mTypeListPosition);
    }

    @Override
    public void attach() {
        mTypeEditViewContract.showInitialView(new FormattedType(
                Color.parseColor(mType.getTypeMarkColor()),
                mDataManager.getTypeMarkColorName(mType.getTypeMarkColor()),
                mType.getTypeMarkPattern() != null,
                Util.getDrawableResourceId(mType.getTypeMarkPattern()),
                mDataManager.getTypeMarkPatternName(mType.getTypeMarkPattern()),
                mType.getTypeName(),
                Util.getFirstChar(mType.getTypeName())
        ));
    }

    @Override
    public void detach() {
        mTypeEditViewContract = null;
    }

    public void notifySettingTypeName() {
        mTypeEditViewContract.showTypeNameEditorDialog(mType.getTypeName());
    }

    public void notifySettingTypeMarkColor() {
        mTypeEditViewContract.showTypeMarkColorPickerDialog(mType.getTypeMarkColor());
    }

    public void notifySettingTypeMarkPattern() {
        mTypeEditViewContract.showTypeMarkPatternPickerDialog(mType.getTypeMarkPattern());
    }

    public void notifyUpdatingTypeName(String newTypeName) {
        if (TextUtils.isEmpty(newTypeName)) {
            mTypeEditViewContract.showToast(R.string.toast_empty_type_name);
        } else if (!mType.getTypeName().equals(newTypeName) && mDataManager.isTypeNameUsed(newTypeName)) {
            mTypeEditViewContract.showToast(R.string.toast_type_name_exists);
        } else {
            mDataManager.notifyUpdatingTypeName(mTypeListPosition, newTypeName);
            mTypeEditViewContract.onTypeNameChanged(newTypeName, Util.getFirstChar(newTypeName));
            postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_NAME);
        }
    }

    public void notifyTypeMarkColorSelected(TypeMarkColor typeMarkColor) {
        String colorHex = typeMarkColor.getColorHex();
        if (mType.getTypeMarkColor().equals(colorHex)) return;
        if (mDataManager.isTypeMarkUsed(colorHex, mType.getTypeMarkPattern())) {
            mTypeEditViewContract.showToast(R.string.toast_type_mark_exists);
        } else {
            mDataManager.notifyUpdatingTypeMarkColor(mTypeListPosition, colorHex);
            mTypeEditViewContract.onTypeMarkColorChanged(Color.parseColor(colorHex), typeMarkColor.getColorName());
            postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_MARK_COLOR);
        }
    }

    public void notifyTypeMarkPatternSelected(TypeMarkPattern typeMarkPattern) {
        boolean hasPattern = typeMarkPattern != null;
        String patternFn = hasPattern ? typeMarkPattern.getPatternFn() : null;
        if (Util.isObjectEqual(mType.getTypeMarkPattern(), patternFn)) return;
        if (mDataManager.isTypeMarkUsed(mType.getTypeMarkColor(), patternFn)) {
            mTypeEditViewContract.showToast(R.string.toast_type_mark_exists);
        } else {
            mDataManager.notifyUpdatingTypeMarkPattern(mTypeListPosition, patternFn);
            mTypeEditViewContract.onTypeMarkPatternChanged(
                    hasPattern,
                    Util.getDrawableResourceId(patternFn),
                    hasPattern ? typeMarkPattern.getPatternName() : null
            );
            postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_MARK_PATTERN);
        }
    }

    private void postTypeDetailChangedEvent(int changedField) {
        mEventBus.post(new TypeDetailChangedEvent(getPresenterName(), mType.getTypeCode(), mTypeListPosition, changedField));
    }
}
