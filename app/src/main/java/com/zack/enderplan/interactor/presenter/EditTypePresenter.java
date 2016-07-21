package com.zack.enderplan.interactor.presenter;

import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.bean.TypeMark;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.model.ram.DataManager;
import com.zack.enderplan.util.Util;
import com.zack.enderplan.domain.view.EditTypeView;
import com.zack.enderplan.interactor.adapter.TypeMarkAdapter;

import org.greenrobot.eventbus.EventBus;

public class EditTypePresenter implements Presenter<EditTypeView> {

    private EditTypeView mEditTypeView;
    private DataManager mDataManager;
    private TypeMarkAdapter mTypeMarkAdapter;
    private Type mType;
    private int mPosition;

    private String typeName;
    private int originalPosition = -1;
    private int clickedPosition = -1;
    private int selectedPosition = -1;

    public EditTypePresenter(EditTypeView editTypeView, int position) {
        attachView(editTypeView);
        mDataManager = DataManager.getInstance();
        mType = mDataManager.getType(position);
        mPosition = position;

        initTypeValues();
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
        mTypeMarkAdapter = new TypeMarkAdapter(mDataManager.getTypeMarkList());
        mEditTypeView.showInitialView(mType.getTypeName(), mTypeMarkAdapter);
    }

    public void notifyTypeNameChanged(String newTypeName) {
        typeName = newTypeName;
        updateSaveButton();
    }

    public void notifyTypeMarkClicked(int position) {

        int lastClickedPosition = clickedPosition;
        clickedPosition = position;

        TypeMark typeMark = mDataManager.getTypeMark(clickedPosition);
        typeMark.setIsSelected(!typeMark.isSelected());
        if (lastClickedPosition != -1 && clickedPosition != lastClickedPosition) {
            mDataManager.getTypeMark(lastClickedPosition).setIsSelected(false);
        }
        mTypeMarkAdapter.notifyDataSetChanged();

        //若当前点击的被选中，那么说明该颜色被选择，否则什么都没选
        selectedPosition = typeMark.isSelected() ? clickedPosition : -1;

        updateSaveButton();
    }

    private void updateSaveButton() {
        mEditTypeView.updateSaveButton(!TextUtils.isEmpty(typeName) && selectedPosition != -1);
    }

    /** 取消编辑类型的时候调用 */
    public void notifyTypeEditCanceled() {
        //清除选中状态（如果存在的话）
        if (selectedPosition != -1) {
            mDataManager.getTypeMark(selectedPosition).setIsSelected(false);
        }
        //使以前的类型颜色变为不可用
        mDataManager.getTypeMark(originalPosition).setIsValid(false);
    }

    public void notifyViewClicked(int viewId) {
        switch (viewId) {
            case R.id.button_cancel:
                mEditTypeView.closeDialog(true);
                break;
            case R.id.button_save:
                //只会在类型颜色有选择时执行，所以就不必考虑selectedPosition为-1的情况
                mDataManager.getTypeMark(selectedPosition).setIsSelected(false);

                mDataManager.notifyTypeEdited(
                        mPosition,
                        typeName,
                        Util.parseColor(mDataManager.getTypeMark(originalPosition).getColorInt()),
                        Util.parseColor(mDataManager.getTypeMark(selectedPosition).getColorInt()));

                //通知AllTypesFragment和AllPlansFragment更新
                EventBus.getDefault().post(new TypeDetailChangedEvent(mType.getTypeCode(), mPosition));

                mEditTypeView.closeDialog(false);
                break;
        }
    }

    /** 1. 初始化类型名<br>2. 使此类型对应的初始颜色选中且变为可用<br>3. 初始化类型颜色的位置 */
    private void initTypeValues() {
        typeName = mType.getTypeName();
        //获取此类型初始颜色的位置
        originalPosition = mDataManager.getTypeMarkLocationInTypeMarkList(mType.getTypeMark());

        TypeMark typeMark = mDataManager.getTypeMark(originalPosition);
        typeMark.setIsValid(true);
        typeMark.setIsSelected(true);

        //把初始颜色位置赋值给当前选中、点击的颜色位置
        selectedPosition = clickedPosition = originalPosition;
    }
}
