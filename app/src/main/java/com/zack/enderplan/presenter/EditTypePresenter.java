package com.zack.enderplan.presenter;

import android.content.ContentValues;
import android.text.TextUtils;

import com.zack.enderplan.bean.Type;
import com.zack.enderplan.bean.TypeMark;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.manager.DataManager;
import com.zack.enderplan.util.LogUtil;
import com.zack.enderplan.util.Util;
import com.zack.enderplan.view.EditTypeView;
import com.zack.enderplan.widget.TypeMarkAdapter;

import org.greenrobot.eventbus.EventBus;

public class EditTypePresenter implements Presenter<EditTypeView> {

    private EditTypeView editTypeView;
    private DataManager dataManager;
    private TypeMarkAdapter typeMarkAdapter;
    private Type type;
    private int originalPosition, selectedPosition;
    private boolean isTypeNameNotEmpty = true;
    private boolean isTypeMarkSelected = true;

    public EditTypePresenter(EditTypeView editTypeView, int position) {
        attachView(editTypeView);
        dataManager = DataManager.getInstance();
        type = dataManager.getType(position);

        //获取此类型初始颜色的位置
        originalPosition = dataManager.getTypeMarkLocationInTypeMarkList(type.getTypeMark());
        //初始化颜色选中状态
        initTypeMarkSelectionStatus();

        //把初始颜色位置赋值给当前选中的颜色位置
        selectedPosition = originalPosition;

        typeMarkAdapter = new TypeMarkAdapter(dataManager.getTypeMarkList());
    }

    @Override
    public void attachView(EditTypeView view) {
        editTypeView = view;
    }

    @Override
    public void detachView() {
        editTypeView = null;
    }

    public void setInitialView() {
        editTypeView.showInitialView(type.getTypeName(), typeMarkAdapter, selectedPosition);
    }

    public void notifyTypeNameChanged(String newTypeName) {
        isTypeNameNotEmpty = !TextUtils.isEmpty(newTypeName);
        updateSaveButton();
    }

    public void notifyTypeMarkClicked(int lastClickedPosition, int clickedPosition) {
        TypeMark typeMark = dataManager.getTypeMark(clickedPosition);
        typeMark.setIsSelected(!typeMark.isSelected());
        if (lastClickedPosition != -1 && clickedPosition != lastClickedPosition) {
            dataManager.getTypeMark(lastClickedPosition).setIsSelected(false);
        }
        typeMarkAdapter.notifyDataSetChanged();

        isTypeMarkSelected = typeMark.isSelected();
        updateSaveButton();
        //editTypeView.onTypeMarkClicked(, typeMark.isSelected() ? typeMark.getResId() : 0);

        //若当前点击的被选中，那么说明该颜色被选择，否则什么都没选
        selectedPosition = typeMark.isSelected() ? clickedPosition : -1;
    }

    private void updateSaveButton() {
        editTypeView.onUpdateSaveButton(isTypeNameNotEmpty && isTypeMarkSelected);
    }

    //在保存编辑类型的时候调用
    public void notifyTypeEdited(String newTypeName) {
        String newTypeMark = Util.parseColor(dataManager.getTypeMark(selectedPosition).getColorInt());
        //只会在类型颜色有选择时执行，所以就不必考虑selectedPosition为-1的情况
        //NOTE: 这里的typeMark还没更新
        dataManager.getTypeMark(selectedPosition).setIsSelected(false);
        dataManager.updateTypeMarkList(originalPosition, selectedPosition);
        dataManager.updateFindingColorResMap(type.getTypeCode(), type.getTypeMark(), newTypeMark);
        //更新type（list中的type实际上也更新了）
        type.setTypeName(newTypeName);
        type.setTypeMark(newTypeMark);
        //更新数据库
        ContentValues values = new ContentValues();
        values.put("type_name", newTypeName);
        values.put("type_mark", newTypeMark);
        EnderPlanDB.getInstance().editType(type.getTypeCode(), values);
        //通知AllTypesFragment和AllPlansFragment更新
        EventBus.getDefault().post(new TypeDetailChangedEvent(type.getTypeCode()));
    }

    //在取消编辑类型的时候调用
    public void notifyTypeEditCanceled() {
        //清除选中状态（如果存在的话）
        if (selectedPosition != -1) {
            dataManager.getTypeMark(selectedPosition).setIsSelected(false);
        }
        //使以前的类型颜色变为不可用
        dataManager.getTypeMark(originalPosition).setIsValid(false);
    }

    private void initTypeMarkSelectionStatus() {
        //使此类型对应的初始颜色选中且变为可用
        TypeMark typeMark = dataManager.getTypeMark(originalPosition);
        typeMark.setIsValid(true);
        typeMark.setIsSelected(true);
    }
}
