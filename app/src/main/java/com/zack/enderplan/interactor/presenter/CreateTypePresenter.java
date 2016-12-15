package com.zack.enderplan.interactor.presenter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.TypeMarkPattern;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.domain.view.CreateTypeView;

import org.greenrobot.eventbus.EventBus;

public class CreateTypePresenter extends BasePresenter implements Presenter<CreateTypeView> {

    private CreateTypeView mCreateTypeView;
    private DataManager mDataManager;
    private Type mType;
    private String mEmptyTypeName;

    public CreateTypePresenter(CreateTypeView createTypeView) {
        attachView(createTypeView);
        mDataManager = DataManager.getInstance();
        Context context = App.getGlobalContext();
        mType = new Type(Util.makeCode(), mDataManager.getTypeCount());
        mType.setTypeName(context.getString(R.string.text_new_type_name));
        mType.setTypeMarkColor(mDataManager.getRandomTypeMarkColor());

        mEmptyTypeName = context.getString(R.string.text_empty_type_name);
    }

    @Override
    public void attachView(CreateTypeView view) {
        mCreateTypeView = view;
    }

    @Override
    public void detachView() {
        mCreateTypeView = null;
    }

    public void setInitialView() {
        mCreateTypeView.showInitialView(new FormattedType(
                Color.parseColor(mType.getTypeMarkColor()),
                mDataManager.getTypeMarkColorName(mType.getTypeMarkColor()),
                mType.getTypeName(),
                mType.getTypeName().substring(0, 1)
        ));
    }

    public void notifyTypeNameTextChanged(String typeName) {
        mType.setTypeName(typeName);
        if (TextUtils.isEmpty(typeName)) {
            mCreateTypeView.onTypeNameChanged(mEmptyTypeName, "-", false);
        } else {
            mCreateTypeView.onTypeNameChanged(typeName, typeName.substring(0, 1), true);
        }
    }

    public void notifySettingTypeMarkColor() {
        mCreateTypeView.showTypeMarkColorPickerDialog(mType.getTypeMarkColor());
    }

    public void notifyTypeMarkColorSelected(TypeMarkColor typeMarkColor) {
        String colorHex = typeMarkColor.getColorHex();
        mType.setTypeMarkColor(colorHex);
        mCreateTypeView.onTypeMarkColorChanged(Color.parseColor(colorHex), typeMarkColor.getColorName());
    }

    public void notifySettingTypeMarkPattern() {
        mCreateTypeView.showTypeMarkPatternPickerDialog(mType.getTypeMarkPattern());
    }

    public void notifyTypeMarkPatternSelected(TypeMarkPattern typeMarkPattern) {
        boolean hasPattern = typeMarkPattern != null;
        String patternFn = hasPattern ? typeMarkPattern.getPatternFn() : null;
        mType.setTypeMarkPattern(patternFn);
        mCreateTypeView.onTypeMarkPatternChanged(
                hasPattern,
                Util.getDrawableResourceId(patternFn),
                hasPattern ? typeMarkPattern.getPatternName() : null
        );
    }

    public void notifyCreateButtonClicked() {
        if (mDataManager.isTypeNameUsed(mType.getTypeName())) {
            mCreateTypeView.playShakeAnimation(Constant.TYPE_NAME);
            mCreateTypeView.showToast(R.string.toast_type_name_exists);
        } else if (mDataManager.isTypeMarkUsed(mType.getTypeMarkColor(), mType.getTypeMarkPattern())) {
            mCreateTypeView.playShakeAnimation(Constant.TYPE_MARK);
            mCreateTypeView.showToast(R.string.toast_type_mark_exists);
        } else {
            mDataManager.notifyTypeCreated(mType);
            EventBus.getDefault().post(new TypeCreatedEvent(
                    getPresenterName(),
                    mDataManager.getRecentlyCreatedType().getTypeCode(),
                    mDataManager.getRecentlyCreatedTypeLocation()
            ));
            mCreateTypeView.exitCreateType();
        }
    }

    public void notifyCancelButtonClicked() {
        //TODO 判断是否已编辑过
        mCreateTypeView.exitCreateType();
    }
}
