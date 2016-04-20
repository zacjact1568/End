package com.zack.enderplan.presenter;

import com.zack.enderplan.bean.Type;
import com.zack.enderplan.bean.TypeMark;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.manager.DataManager;
import com.zack.enderplan.util.Util;
import com.zack.enderplan.view.CreateTypeView;
import com.zack.enderplan.widget.TypeMarkAdapter;

import org.greenrobot.eventbus.EventBus;

public class CreateTypePresenter implements Presenter<CreateTypeView> {

    private CreateTypeView createTypeView;
    private DataManager dataManager;
    private TypeMarkAdapter typeMarkAdapter;
    private Type type;
    private int selectedPosition;

    public CreateTypePresenter(CreateTypeView createTypeView) {
        attachView(createTypeView);
        dataManager = DataManager.getInstance();
        type = new Type(Util.makeCode(), dataManager.getTypeCount());
    }

    @Override
    public void attachView(CreateTypeView view) {
        createTypeView = view;
    }

    @Override
    public void detachView() {
        createTypeView = null;
        if (selectedPosition != -1) {
            dataManager.getTypeMark(selectedPosition).setIsSelected(false);
        }
    }

    public TypeMarkAdapter createTypeMarkAdapter() {
        typeMarkAdapter = new TypeMarkAdapter(dataManager.getTypeMarkList());
        return typeMarkAdapter;
    }

    public void notifyTypeNameChanged(String newTypeName) {
        type.setTypeName(newTypeName);
    }

    public void notifyTypeMarkClicked(int lastClickedPosition, int clickedPosition) {
        TypeMark typeMark = dataManager.getTypeMark(clickedPosition);
        typeMark.setIsSelected(!typeMark.isSelected());
        if (lastClickedPosition != -1 && clickedPosition != lastClickedPosition) {
            dataManager.getTypeMark(lastClickedPosition).setIsSelected(false);
        }
        typeMarkAdapter.notifyDataSetChanged();
        createTypeView.onTypeMarkClicked(typeMark.isSelected(), typeMark.isSelected() ? typeMark.getResId() : 0);

        //若当前点击的被选中，那么说明该颜色被选择，否则什么都没选
        selectedPosition = typeMark.isSelected() ? clickedPosition : -1;
    }

    public void createNewType(String typeMark) {
        type.setTypeMark(typeMark);
        dataManager.addToTypeList(type);
        //只会在类型颜色有选择时执行，不必考虑selectedPosition为-1的情况
        dataManager.updateTypeMarkList(selectedPosition);
        dataManager.putMappingInFindingColorResMap(type.getTypeCode(), type.getTypeMark());
        EnderPlanDB.getInstance().saveType(type);
        EventBus.getDefault().post(new TypeCreatedEvent());
    }
}
