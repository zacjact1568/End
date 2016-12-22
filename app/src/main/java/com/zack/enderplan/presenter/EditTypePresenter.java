package com.zack.enderplan.presenter;

import android.graphics.Color;
import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.contract.EditTypeViewContract;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.model.bean.TypeMarkPattern;
import com.zack.enderplan.common.Util;

import org.greenrobot.eventbus.EventBus;

public class EditTypePresenter extends BasePresenter<EditTypeViewContract> {

    private EditTypeViewContract mEditTypeViewContract;
    private DataManager mDataManager;
    private EventBus mEventBus;
    private Type mType;
    private int mPosition;

    public EditTypePresenter(EditTypeViewContract editTypeViewContract, int position) {
        mEventBus = EventBus.getDefault();
        attachView(editTypeViewContract);
        mDataManager = DataManager.getInstance();
        mType = mDataManager.getType(position);
        mPosition = position;
    }

    @Override
    public void attachView(EditTypeViewContract viewContract) {
        mEditTypeViewContract = viewContract;
    }

    @Override
    public void detachView() {
        mEditTypeViewContract = null;
    }

    public void setInitialView() {
        mEditTypeViewContract.showInitialView(new FormattedType(
                Color.parseColor(mType.getTypeMarkColor()),
                mDataManager.getTypeMarkColorName(mType.getTypeMarkColor()),
                mType.getTypeMarkPattern() != null,
                Util.getDrawableResourceId(mType.getTypeMarkPattern()),
                mDataManager.getTypeMarkPatternName(mType.getTypeMarkPattern()),
                mType.getTypeName(),
                mType.getTypeName().substring(0, 1)
        ));
    }

    public void notifySettingTypeName() {
        mEditTypeViewContract.showTypeNameEditorDialog(mType.getTypeName());
    }

    public void notifySettingTypeMarkColor() {
        mEditTypeViewContract.showTypeMarkColorPickerDialog(mType.getTypeMarkColor());
    }

    public void notifySettingTypeMarkPattern() {
        mEditTypeViewContract.showTypeMarkPatternPickerDialog(mType.getTypeMarkPattern());
    }

    public void notifyUpdatingTypeName(String newTypeName) {
        if (TextUtils.isEmpty(newTypeName)) {
            mEditTypeViewContract.showToast(R.string.toast_empty_type_name);
        } else if (!mType.getTypeName().equals(newTypeName) && mDataManager.isTypeNameUsed(newTypeName)) {
            mEditTypeViewContract.showToast(R.string.toast_type_name_exists);
        } else {
            mDataManager.notifyUpdatingTypeName(mPosition, newTypeName);
            mEditTypeViewContract.onTypeNameChanged(newTypeName, newTypeName.substring(0, 1));
            postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_NAME);
        }
    }

    public void notifyTypeMarkColorSelected(TypeMarkColor typeMarkColor) {
        String colorHex = typeMarkColor.getColorHex();
        if (mType.getTypeMarkColor().equals(colorHex)) return;
        if (mDataManager.isTypeMarkUsed(colorHex, mType.getTypeMarkPattern())) {
            mEditTypeViewContract.showToast(R.string.toast_type_mark_exists);
        } else {
            mDataManager.notifyUpdatingTypeMarkColor(mPosition, colorHex);
            mEditTypeViewContract.onTypeMarkColorChanged(Color.parseColor(colorHex), typeMarkColor.getColorName());
            postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_MARK_COLOR);
        }
    }

    public void notifyTypeMarkPatternSelected(TypeMarkPattern typeMarkPattern) {
        boolean hasPattern = typeMarkPattern != null;
        String patternFn = hasPattern ? typeMarkPattern.getPatternFn() : null;
        if (Util.isObjectEqual(mType.getTypeMarkPattern(), patternFn)) return;
        if (mDataManager.isTypeMarkUsed(mType.getTypeMarkColor(), patternFn)) {
            mEditTypeViewContract.showToast(R.string.toast_type_mark_exists);
        } else {
            mDataManager.notifyUpdatingTypeMarkPattern(mPosition, patternFn);
            mEditTypeViewContract.onTypeMarkPatternChanged(
                    hasPattern,
                    Util.getDrawableResourceId(patternFn),
                    hasPattern ? typeMarkPattern.getPatternName() : null
            );
            postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_MARK_PATTERN);
        }
    }

    private void postTypeDetailChangedEvent(int changedField) {
        mEventBus.post(new TypeDetailChangedEvent(getPresenterName(), mType.getTypeCode(), mPosition, changedField));
    }
}
