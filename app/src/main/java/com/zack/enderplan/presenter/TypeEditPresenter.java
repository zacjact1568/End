package com.zack.enderplan.presenter;

import android.graphics.Color;
import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.StringUtil;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.contract.TypeEditViewContract;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.model.bean.TypeMarkPattern;
import com.zack.enderplan.util.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class TypeEditPresenter extends BasePresenter {

    private TypeEditViewContract mTypeEditViewContract;
    private DataManager mDataManager;
    private EventBus mEventBus;
    private Type mType;
    private int mTypeListPosition;
    private boolean mEnableTransition;

    @Inject
    TypeEditPresenter(TypeEditViewContract typeEditViewContract, int typeListPosition, boolean enableTransition, DataManager dataManager, EventBus eventBus) {
        mTypeEditViewContract = typeEditViewContract;
        mTypeListPosition = typeListPosition;
        mEnableTransition = enableTransition;
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
                ResourceUtil.getDrawableResourceId(mType.getTypeMarkPattern()),
                mDataManager.getTypeMarkPatternName(mType.getTypeMarkPattern()),
                mType.getTypeName(),
                StringUtil.getFirstChar(mType.getTypeName())
        ), mEnableTransition);
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
        if (mType.getTypeName().equals(newTypeName)) return;
        if (TextUtils.isEmpty(newTypeName)) {
            mTypeEditViewContract.showToast(R.string.toast_empty_type_name);
        } else if (mDataManager.isTypeNameUsed(newTypeName)) {
            mTypeEditViewContract.showToast(R.string.toast_type_name_exists);
        } else {
            mDataManager.notifyUpdatingTypeName(mTypeListPosition, newTypeName);
            mTypeEditViewContract.onTypeNameChanged(newTypeName, StringUtil.getFirstChar(newTypeName));
            postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_NAME);
        }
    }

    public void notifyTypeMarkColorSelected(TypeMarkColor typeMarkColor) {
        String colorHex = typeMarkColor.getColorHex();
        if (mType.getTypeMarkColor().equals(colorHex)) return;
        //上一行已经把此类型当前的颜色排除了，所以不存在选择相同的颜色还弹toast的问题
        if (mDataManager.isTypeMarkColorUsed(colorHex)) {
            mTypeEditViewContract.showToast(R.string.toast_type_mark_color_exists);
        } else {
            mDataManager.notifyUpdatingTypeMarkColor(mTypeListPosition, colorHex);
            mTypeEditViewContract.onTypeMarkColorChanged(Color.parseColor(colorHex), typeMarkColor.getColorName());
            postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_MARK_COLOR);
        }
    }

    public void notifyTypeMarkPatternSelected(TypeMarkPattern typeMarkPattern) {
        boolean hasPattern = typeMarkPattern != null;
        String patternFn = hasPattern ? typeMarkPattern.getPatternFn() : null;
        if (CommonUtil.isObjectEqual(mType.getTypeMarkPattern(), patternFn)) return;
        mDataManager.notifyUpdatingTypeMarkPattern(mTypeListPosition, patternFn);
        mTypeEditViewContract.onTypeMarkPatternChanged(
                hasPattern,
                ResourceUtil.getDrawableResourceId(patternFn),
                hasPattern ? typeMarkPattern.getPatternName() : null
        );
        postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_MARK_PATTERN);
    }

    private void postTypeDetailChangedEvent(int changedField) {
        mEventBus.post(new TypeDetailChangedEvent(getPresenterName(), mType.getTypeCode(), mTypeListPosition, changedField));
    }
}
