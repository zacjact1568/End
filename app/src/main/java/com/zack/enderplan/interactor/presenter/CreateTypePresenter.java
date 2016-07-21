package com.zack.enderplan.interactor.presenter;

import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.bean.TypeMark;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.utility.Util;
import com.zack.enderplan.domain.view.CreateTypeView;
import com.zack.enderplan.interactor.adapter.TypeMarkAdapter;

import org.greenrobot.eventbus.EventBus;

public class CreateTypePresenter implements Presenter<CreateTypeView> {

    private CreateTypeView mCreateTypeView;
    private DataManager mDataManager;
    private TypeMarkAdapter mTypeMarkAdapter;
    private Type mType;

    private int clickedPosition = -1;
    private int selectedPosition = -1;

    public CreateTypePresenter(CreateTypeView createTypeView) {
        attachView(createTypeView);
        mDataManager = DataManager.getInstance();
        mType = new Type(Util.makeCode(), mDataManager.getTypeCount());
    }

    @Override
    public void attachView(CreateTypeView view) {
        mCreateTypeView = view;
    }

    @Override
    public void detachView() {
        mCreateTypeView = null;
        if (selectedPosition != -1) {
            mDataManager.getTypeMark(selectedPosition).setIsSelected(false);
        }
    }

    public void setInitialView() {
        mTypeMarkAdapter = new TypeMarkAdapter(mDataManager.getTypeMarkList());
        mCreateTypeView.showInitialView(mTypeMarkAdapter);
    }

    public void notifyTypeNameChanged(String newTypeName) {
        mType.setTypeName(newTypeName);
        updateSaveButton();
    }

    public void notifyTypeMarkClicked(int position) {

        //点击事件来临时，将上一个点击位置赋给lastClickedPosition，将此次点击的位置赋给clickedPosition
        //并将此次点击的位置保存成全局变量，供下一次点击事件来临时使用（作为下一次点击事件的上一个点击位置）
        int lastClickedPosition = clickedPosition;
        clickedPosition = position;

        TypeMark typeMark = mDataManager.getTypeMark(clickedPosition);
        typeMark.setIsSelected(!typeMark.isSelected());
        //不是初始点击且两次点击的不是同一个
        if (lastClickedPosition != -1 && clickedPosition != lastClickedPosition) {
            //取消上一次点击的
            mDataManager.getTypeMark(lastClickedPosition).setIsSelected(false);
        }
        //刷新选中状态的显示
        mTypeMarkAdapter.notifyDataSetChanged();

        //若当前点击的被选中，那么说明该颜色被选择，否则什么都没选
        selectedPosition = typeMark.isSelected() ? clickedPosition : -1;

        updateSaveButton();
    }

    public void notifyViewClicked(int viewId) {
        switch (viewId) {
            case R.id.btn_cancel:
                mCreateTypeView.closeDialog();
                break;
            case R.id.btn_save:
                mType.setTypeMark(Util.parseColor(mDataManager.getTypeMark(selectedPosition).getColorInt()));

                mDataManager.notifyTypeCreated(mType);

                EventBus.getDefault().post(new TypeCreatedEvent(
                        mDataManager.getRecentlyCreatedType().getTypeCode(),
                        mDataManager.getRecentlyCreatedTypeLocation()
                ));

                mCreateTypeView.closeDialog();
                break;
        }
    }

    private void updateSaveButton() {
        mCreateTypeView.updateSaveButton(!TextUtils.isEmpty(mType.getTypeName()) && selectedPosition != -1);
    }
}
