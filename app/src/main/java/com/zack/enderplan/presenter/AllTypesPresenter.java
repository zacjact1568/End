package com.zack.enderplan.presenter;

import android.content.ContentValues;
import android.view.View;

import com.zack.enderplan.bean.Type;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.manager.DataManager;
import com.zack.enderplan.view.AllTypesView;
import com.zack.enderplan.widget.TypeAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AllTypesPresenter implements Presenter<AllTypesView> {

    private AllTypesView allTypesView;
    private DataManager dataManager;
    private TypeAdapter typeAdapter;
    private EnderPlanDB enderplanDB;
    private int typeItemClickPosition;

    public AllTypesPresenter(AllTypesView allTypesView) {
        attachView(allTypesView);
        dataManager = DataManager.getInstance();
        enderplanDB = EnderPlanDB.getInstance();
    }

    @Override
    public void attachView(AllTypesView view) {
        allTypesView = view;
        EventBus.getDefault().register(this);//TODO 这个类里可以不用EventBus？
    }

    @Override
    public void detachView() {
        allTypesView = null;
        EventBus.getDefault().unregister(this);
    }

    //储存类型列表的排序
    public void syncWithDatabase() {
        for (int i = 0; i < dataManager.getTypeCount(); i++) {
            Type type = dataManager.getType(i);
            if (type.getTypeSequence() != i) {
                //更新typeList
                //在移动typeList的item的时候只是交换了items在list中的位置，并没有改变item中的type_sequence
                type.setTypeSequence(i);
                //更新数据库
                ContentValues values = new ContentValues();
                values.put("type_sequence", i);
                enderplanDB.editType(type.getTypeCode(), values);
            }
        }
    }

    public void createTypeAdapter() {
        typeAdapter = new TypeAdapter(dataManager.getTypeList(), dataManager.getTypeMarkAndColorResMap(), dataManager.getPlanCountOfEachTypeMap());
        typeAdapter.setOnTypeItemClickListener(new TypeAdapter.OnTypeItemClickListener() {
            @Override
            public void onTypeItemClick(View itemView, int position) {
                typeItemClickPosition = position;
                allTypesView.onShowTypeDetailDialogFragment(position);
            }
        });
    }

    public TypeAdapter getTypeAdapter() {
        return typeAdapter;
    }

    public void notifyTypeSequenceChanged(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                dataManager.swapTypesInTypeList(i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                dataManager.swapTypesInTypeList(i, i - 1);
            }
        }
        typeAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    public void notifyTypeDeleted(int position) {
        Type type = dataManager.getType(position);

        dataManager.removeFromTypeList(position);
        typeAdapter.notifyItemRemoved(position);

        if (dataManager.isPlanCountOfOneTypeExists(type.getTypeCode())) {
            dataManager.addToTypeList(position, type);
            typeAdapter.notifyItemInserted(position);
            //弹提示有计划属于该类型的dialog
            allTypesView.onShowPlanCountOfOneTypeExistsDialog();
        } else {
            dataManager.updateTypeMarkList(type.getTypeMark());
            dataManager.removeMappingInFindingColorResMap(type.getTypeCode(), type.getTypeMark());
            //弹提示删除成功的SnackBar
            allTypesView.onTypeDeleted(type.getTypeName(), position, type);
            enderplanDB.deleteType(type.getTypeCode());
        }
    }

    public void notifyTypeRecreated(int position, Type type) {
        dataManager.addToTypeList(position, type);
        dataManager.updateTypeMarkList(type.getTypeMark());
        dataManager.putMappingInFindingColorResMap(type.getTypeCode(), type.getTypeMark());
        typeAdapter.notifyItemInserted(position);
        enderplanDB.saveType(type);
    }

    @Subscribe
    public void onTypeListLoaded(DataLoadedEvent event) {
        typeAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onTypeCreated(TypeCreatedEvent event) {
        typeAdapter.notifyItemInserted(dataManager.getTypeCount() - 1);
    }

    @Subscribe
    public void onPlanCreated(PlanCreatedEvent event) {
        typeAdapter.notifyItemChanged(typeItemClickPosition);
    }

    @Subscribe
    public void onTypeDetailChanged(TypeDetailChangedEvent event) {
        typeAdapter.notifyItemChanged(typeItemClickPosition);
    }
}
