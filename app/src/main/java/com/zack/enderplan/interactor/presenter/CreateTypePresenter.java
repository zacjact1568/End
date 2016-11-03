package com.zack.enderplan.interactor.presenter;

import android.graphics.Color;
import android.text.TextUtils;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.utility.Util;
import com.zack.enderplan.domain.view.CreateTypeView;

import org.greenrobot.eventbus.EventBus;

public class CreateTypePresenter extends BasePresenter implements Presenter<CreateTypeView> {

    private CreateTypeView mCreateTypeView;
    private DataManager mDataManager;
    private Type mType;
    private String mDefaultTypeName, mEmptyTypeName;

    public CreateTypePresenter(CreateTypeView createTypeView) {
        attachView(createTypeView);
        mDataManager = DataManager.getInstance();
        mDefaultTypeName = App.getGlobalContext().getString(R.string.text_new_type_name);
        mEmptyTypeName = App.getGlobalContext().getString(R.string.text_empty_type_name);
        mType = new Type(Util.makeCode(), mDataManager.getTypeCount());
        mType.setTypeName(mDefaultTypeName);
        mType.setTypeMarkColor(mDataManager.getRandomTypeMarkColor());
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
        mCreateTypeView.showInitialView(
                Color.parseColor(mType.getTypeMarkColor()),
                mDefaultTypeName.substring(0, 1),
                mDefaultTypeName,
                mDataManager.getTypeMarkColorName(mType.getTypeMarkColor())
        );
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

    public void notifyCreateButtonClicked() {
        //TODO 后续：只要color和pattern的组合唯一就ok
        if (mDataManager.isTypeNameUsed(mType.getTypeName())) {
            mCreateTypeView.playShakeAnimation("type_name");
            mCreateTypeView.showToast(R.string.toast_type_name_exists);
        } else if (mDataManager.isTypeMarkColorUsed(mType.getTypeMarkColor())) {
            mCreateTypeView.playShakeAnimation("type_mark_color");
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
