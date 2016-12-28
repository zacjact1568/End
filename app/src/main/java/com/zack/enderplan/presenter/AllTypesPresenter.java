package com.zack.enderplan.presenter;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.zack.enderplan.event.TypeDeletedEvent;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.view.adapter.TypeAdapter;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.callback.TypeItemTouchCallback;
import com.zack.enderplan.view.contract.AllTypesViewContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class AllTypesPresenter extends BasePresenter {

    private AllTypesViewContract mAllTypesViewContract;
    private DataManager mDataManager;
    private TypeAdapter mTypeAdapter;
    private EventBus mEventBus;

    @Inject
    public AllTypesPresenter(AllTypesViewContract allTypesViewContract, DataManager dataManager, EventBus eventBus) {
        mAllTypesViewContract = allTypesViewContract;
        mDataManager = dataManager;
        mEventBus = eventBus;

        mTypeAdapter = new TypeAdapter(mDataManager);
        mTypeAdapter.setOnTypeItemClickListener(new TypeAdapter.OnTypeItemClickListener() {
            @Override
            public void onTypeItemClick(int position, View typeItem) {
                notifyTypeItemClicked(position, typeItem);
            }
        });
    }

    @Override
    public void attach() {
        mEventBus.register(this);

        TypeItemTouchCallback typeItemTouchCallback = new TypeItemTouchCallback();
        typeItemTouchCallback.setOnItemMovedListener(new TypeItemTouchCallback.OnItemMovedListener() {
            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                notifyTypeSequenceChanged(fromPosition, toPosition);
            }
        });

        mAllTypesViewContract.showInitialView(mTypeAdapter, new ItemTouchHelper(typeItemTouchCallback));
    }

    @Override
    public void detach() {
        mAllTypesViewContract = null;
        mEventBus.unregister(this);
    }

    public void notifyTypeItemClicked(int position, View typeItem) {
        mAllTypesViewContract.onTypeItemClicked(position, typeItem);
    }

    public void notifyTypeSequenceChanged(int fromPosition, int toPosition) {
        mDataManager.swapTypesInTypeList(fromPosition, toPosition);
        mTypeAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Subscribe
    public void onDataLoaded(DataLoadedEvent event) {
        mTypeAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onTypeCreated(TypeCreatedEvent event) {
        mTypeAdapter.notifyItemInserted(mDataManager.getTypeCount() - 1);
    }

    @Subscribe
    public void onPlanCreated(PlanCreatedEvent event) {
        Plan newPlan = mDataManager.getPlan(event.getPosition());
        if (!newPlan.isCompleted()) {
            //新计划是一个未完成的计划
            mTypeAdapter.notifyItemChanged(mDataManager.getTypeLocationInTypeList(newPlan.getTypeCode()));
        }
    }

    @Subscribe
    public void onTypeDetailChanged(TypeDetailChangedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        mTypeAdapter.notifyItemChanged(event.getPosition());
    }

    @Subscribe
    public void onTypeDeleted(TypeDeletedEvent event) {
        mTypeAdapter.notifyItemRemoved(event.getPosition());
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (event.getChangedField() == PlanDetailChangedEvent.FIELD_PLAN_STATUS || event.getChangedField() == PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN) {
            //类型或完成情况改变后的刷新（其他改变未在此界面上呈现）
            //因为可能有多个item需要刷新，比较麻烦，所以直接全部刷新了
            mTypeAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {
        Plan deletedPlan = event.getDeletedPlan();
        if (!deletedPlan.isCompleted()) {
            //删除的计划是一个未完成的计划
            mTypeAdapter.notifyItemChanged(mDataManager.getTypeLocationInTypeList(deletedPlan.getTypeCode()));
        }
    }
}
