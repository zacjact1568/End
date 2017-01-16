package com.zack.enderplan.presenter;

import android.support.v7.widget.helper.ItemTouchHelper;

import com.zack.enderplan.common.Constant;
import com.zack.enderplan.common.Logger;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.view.adapter.PlanAdapter;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.callback.PlanItemTouchCallback;
import com.zack.enderplan.view.contract.MyPlansViewContract;
import com.zack.enderplan.common.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

public class MyPlansPresenter extends BasePresenter {

    private MyPlansViewContract mMyPlansViewContract;
    private DataManager mDataManager;
    private PlanAdapter mPlanAdapter;
    private EventBus mEventBus;
    private boolean mViewVisible;

    @Inject
    public MyPlansPresenter(MyPlansViewContract myPlansViewContract, DataManager dataManager, EventBus eventBus) {
        mMyPlansViewContract = myPlansViewContract;
        mDataManager = dataManager;
        mEventBus = eventBus;

        //TODO 写到一个单独的方法里
        //初始化adapter
        mPlanAdapter = new PlanAdapter(mDataManager);
        mPlanAdapter.setOnPlanItemClickListener(new PlanAdapter.OnPlanItemClickListener() {
            @Override
            public void onPlanItemClick(int position) {
                mMyPlansViewContract.onPlanItemClicked(position);
            }
        });
        mPlanAdapter.setOnPlanItemLongClickListener(new PlanAdapter.OnPlanItemLongClickListener() {
            @Override
            public void onPlanItemLongClick(int position) {
                Logger.d("Long click at position" + position);
            }
        });
        mPlanAdapter.setOnStarStatusChangedListener(new PlanAdapter.OnStarStatusChangedListener() {
            @Override
            public void onStarStatusChanged(int position) {
                mDataManager.notifyStarStatusChanged(position);
                mEventBus.post(new PlanDetailChangedEvent(
                        getPresenterName(),
                        mDataManager.getPlan(position).getPlanCode(),
                        position,
                        PlanDetailChangedEvent.FIELD_STAR_STATUS
                ));
            }
        });
    }

    @Override
    public void attach() {
        mEventBus.register(this);

        PlanItemTouchCallback planItemTouchCallback = new PlanItemTouchCallback();
        planItemTouchCallback.setOnItemSwipedListener(new PlanItemTouchCallback.OnItemSwipedListener() {
            @Override
            public void onItemSwiped(int position, int direction) {
                switch (direction) {
                    case PlanItemTouchCallback.DIR_START:
                        notifyDeletingPlan(position);
                        break;
                    case PlanItemTouchCallback.DIR_END:
                        notifyPlanStatusChanged(position);
                        break;
                }
            }
        });
        planItemTouchCallback.setOnItemMovedListener(new PlanItemTouchCallback.OnItemMovedListener() {
            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                notifyPlanSequenceChanged(fromPosition, toPosition);
            }
        });

        mMyPlansViewContract.showInitialView(mPlanAdapter, new ItemTouchHelper(planItemTouchCallback));
    }

    @Override
    public void detach() {
        mMyPlansViewContract = null;
        mEventBus.unregister(this);
    }

    public void notifySwitchingViewVisibility(boolean isVisible) {
        mViewVisible = isVisible;
    }

    public void notifyDeletingPlan(int position) {

        Util.makeShortVibrate();

        //必须先把要删除计划的引用拿到
        Plan plan = mDataManager.getPlan(position);

        mDataManager.notifyPlanDeleted(position);
        mPlanAdapter.notifyItemRemoved(position);
        mMyPlansViewContract.onPlanDeleted(plan, position, mViewVisible);

        mEventBus.post(new PlanDeletedEvent(getPresenterName(), plan.getPlanCode(), position, plan));
    }

    public void notifyCreatingPlan(Plan newPlan, int position) {
        mDataManager.notifyPlanCreated(position, newPlan);
        mPlanAdapter.notifyItemInserted(position);
        mEventBus.post(new PlanCreatedEvent(getPresenterName(), newPlan.getPlanCode(), position));
    }

    public void notifyPlanStatusChanged(int position) {
        Plan plan = mDataManager.getPlan(position);
        //首先检测此计划是否有提醒
        if (plan.hasReminder()) {
            mDataManager.notifyReminderTimeChanged(position, Constant.UNDEFINED_TIME);
            mPlanAdapter.notifyItemChanged(position);
            mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), plan.getPlanCode(), position, PlanDetailChangedEvent.FIELD_REMINDER_TIME));
        }
        //执行以下语句时，只是在view上让position处的plan删除了，实际上还未被删除但也即将被删除
        //NOTE: 不能用notifyItemRemoved，会没有效果
        mPlanAdapter.notifyItemRemoved(position);
        mDataManager.notifyPlanStatusChanged(position);
        //这里，plan的状态已经更新
        int newPosition = plan.isCompleted() ? mDataManager.getUcPlanCount() : 0;
        mPlanAdapter.notifyItemInserted(newPosition);
        //发送事件，更新其他组件
        mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), plan.getPlanCode(), newPosition, PlanDetailChangedEvent.FIELD_PLAN_STATUS));
    }

    public void notifyPlanSequenceChanged(int fromPosition, int toPosition) {
        if (fromPosition < mDataManager.getUcPlanCount() != toPosition < mDataManager.getUcPlanCount()) return;
        //只能移动到相同完成状态的计划位置处
        mDataManager.swapPlansInPlanList(fromPosition, toPosition);
        mPlanAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Subscribe
    public void onDataLoaded(DataLoadedEvent event) {
        mPlanAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onPlanCreated(PlanCreatedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        //可能会报错
        mPlanAdapter.notifyItemInserted(event.getPosition());
        //mPlanAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onTypeDetailChanged(TypeDetailChangedEvent event) {
        List<Integer> singleTypeUcPlanPosList = mDataManager.getPlanLocationListOfOneType(event.getTypeCode());
        for (int position : singleTypeUcPlanPosList) {
            //所有属于这个类型的计划都需要刷新
            mPlanAdapter.notifyItemChanged(position);
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        if (event.getChangedField() == PlanDetailChangedEvent.FIELD_PLAN_STATUS) {
            //有完成情况的改变，直接全部刷新
            mPlanAdapter.notifyDataSetChanged();
        } else {
            //普通、类型改变的刷新
            mPlanAdapter.notifyItemChanged(event.getPosition());
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        mPlanAdapter.notifyItemRemoved(event.getPosition());
    }
}
