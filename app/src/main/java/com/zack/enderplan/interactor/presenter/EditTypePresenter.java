package com.zack.enderplan.interactor.presenter;

import android.graphics.Color;
import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.domain.view.EditTypeView;
import com.zack.enderplan.model.bean.TypeMarkColor;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class EditTypePresenter extends BasePresenter implements Presenter<EditTypeView> {

    private EditTypeView mEditTypeView;
    private DataManager mDataManager;
    private EventBus mEventBus;
    private Type mType;
    private int mPosition;

    public EditTypePresenter(EditTypeView editTypeView, int position) {
        mEventBus = EventBus.getDefault();
        attachView(editTypeView);
        mDataManager = DataManager.getInstance();
        mType = mDataManager.getType(position);
        mPosition = position;
    }

    @Override
    public void attachView(EditTypeView view) {
        mEditTypeView = view;
    }

    @Override
    public void detachView() {
        mEditTypeView = null;
    }

    public void setInitialView() {
        mEditTypeView.showInitialView(
                Color.parseColor(mType.getTypeMarkColor()),
                mType.getTypeName().substring(0, 1),
                mType.getTypeName(),
                mDataManager.getTypeMarkColorName(mType.getTypeMarkColor())
        );
    }

    public void notifySettingTypeName() {
        mEditTypeView.showTypeNameEditorDialog(mType.getTypeName());
    }

    public void notifySettingTypeMarkColor() {
        mEditTypeView.showTypeMarkColorPickerDialog(mType.getTypeMarkColor());
    }

    public void notifyUpdatingTypeName(String newTypeName) {
        if (TextUtils.isEmpty(newTypeName)) {
            mEditTypeView.showToast(R.string.toast_empty_type_name);
        } else if (!mType.getTypeName().equals(newTypeName) && mDataManager.isTypeNameUsed(newTypeName)) {
            mEditTypeView.showToast(R.string.toast_type_name_exists);
        } else {
            mDataManager.notifyUpdatingTypeName(mPosition, newTypeName);
            mEditTypeView.onTypeNameChanged(newTypeName, newTypeName.substring(0, 1));
            postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_NAME);
        }
    }

    public void notifyTypeMarkColorSelected(TypeMarkColor typeMarkColor) {
        String colorHex = typeMarkColor.getColorHex();
        if (!mType.getTypeMarkColor().equals(colorHex) && mDataManager.isTypeMarkColorUsed(colorHex)) {
            mEditTypeView.showToast(R.string.toast_type_mark_exists);
        } else {
            mDataManager.notifyUpdatingTypeMarkColor(mPosition, colorHex);
            mEditTypeView.onTypeMarkColorChanged(Color.parseColor(colorHex), typeMarkColor.getColorName());
            postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_MARK_COLOR);
        }
    }

    private void postTypeDetailChangedEvent(int changedField) {
        mEventBus.post(new TypeDetailChangedEvent(getPresenterName(), mType.getTypeCode(), mPosition, changedField));
    }
}
