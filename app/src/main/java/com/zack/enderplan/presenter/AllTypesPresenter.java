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
import com.zack.enderplan.view.adapter.TypeListAdapter;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.callback.TypeListItemTouchCallback;
import com.zack.enderplan.view.contract.AllTypesViewContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class AllTypesPresenter extends BasePresenter {

    private AllTypesViewContract mAllTypesViewContract;
    private DataManager mDataManager;
    private TypeListAdapter mTypeListAdapter;
    private EventBus mEventBus;

    @Inject
    public AllTypesPresenter(AllTypesViewContract allTypesViewContract, DataManager dataManager, EventBus eventBus) {
        mAllTypesViewContract = allTypesViewContract;
        mDataManager = dataManager;
        mEventBus = eventBus;

        mTypeListAdapter = new TypeListAdapter(mDataManager);
        mTypeListAdapter.setOnTypeItemClickListener(new TypeListAdapter.OnTypeItemClickListener() {
            @Override
            public void onTypeItemClick(int position, View typeItem) {
                notifyTypeItemClicked(position, typeItem);
            }
        });
    }

    @Override
    public void attach() {
        mEventBus.register(this);

        TypeListItemTouchCallback typeListItemTouchCallback = new TypeListItemTouchCallback(mDataManager);
        typeListItemTouchCallback.setOnItemMovedListener(new TypeListItemTouchCallback.OnItemMovedListener() {
            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                notifyTypeSequenceChanged(fromPosition, toPosition);
            }
        });

        mAllTypesViewContract.showInitialView(mTypeListAdapter, new ItemTouchHelper(typeListItemTouchCallback));
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
        mTypeListAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Subscribe
    public void onDataLoaded(DataLoadedEvent event) {
        mTypeListAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onTypeCreated(TypeCreatedEvent event) {
        int position = mDataManager.getTypeCount() - 1;
        mTypeListAdapter.notifyItemInserted(position);
        mAllTypesViewContract.onTypeCreated(position);
    }

    @Subscribe
    public void onPlanCreated(PlanCreatedEvent event) {
        Plan newPlan = mDataManager.getPlan(event.getPosition());
        if (!newPlan.isCompleted()) {
            //新计划是一个未完成的计划
            mTypeListAdapter.notifyItemChanged(mDataManager.getTypeLocationInTypeList(newPlan.getTypeCode()));
        }
    }

    @Subscribe
    public void onTypeDetailChanged(TypeDetailChangedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        mTypeListAdapter.notifyItemChanged(event.getPosition());
    }

    @Subscribe
    public void onTypeDeleted(TypeDeletedEvent event) {
        mTypeListAdapter.notifyItemRemoved(event.getPosition());
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (event.getChangedField() == PlanDetailChangedEvent.FIELD_PLAN_STATUS || event.getChangedField() == PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN) {
            //类型或完成情况改变后的刷新（其他改变未在此界面上呈现）
            //因为可能有多个item需要刷新，比较麻烦，所以直接全部刷新了
            mTypeListAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {
        Plan deletedPlan = event.getDeletedPlan();
        if (!deletedPlan.isCompleted()) {
            //删除的计划是一个未完成的计划
            mTypeListAdapter.notifyItemChanged(mDataManager.getTypeLocationInTypeList(deletedPlan.getTypeCode()));
        }
    }
}
