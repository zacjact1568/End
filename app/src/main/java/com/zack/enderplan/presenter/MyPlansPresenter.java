package com.zack.enderplan.presenter;

import android.support.v7.widget.helper.ItemTouchHelper;

import com.zack.enderplan.common.Constant;
import com.zack.enderplan.util.SystemUtil;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.view.adapter.PlanListAdapter;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.callback.PlanListItemTouchCallback;
import com.zack.enderplan.view.contract.MyPlansViewContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

public class MyPlansPresenter extends BasePresenter {

    private MyPlansViewContract mMyPlansViewContract;
    private DataManager mDataManager;
    private PlanListAdapter mPlanListAdapter;
    private EventBus mEventBus;
    private boolean mViewVisible;
    private boolean mPlanItemEmpty;

    @Inject
    MyPlansPresenter(MyPlansViewContract myPlansViewContract, DataManager dataManager, EventBus eventBus) {
        mMyPlansViewContract = myPlansViewContract;
        mDataManager = dataManager;
        mEventBus = eventBus;

        //TODO 写到一个单独的方法里
        //初始化adapter
        mPlanListAdapter = new PlanListAdapter(mDataManager);
        mPlanListAdapter.setOnPlanItemClickListener(new PlanListAdapter.OnPlanItemClickListener() {
            @Override
            public void onPlanItemClick(int position) {
                mMyPlansViewContract.onPlanItemClicked(position);
            }
        });
        mPlanListAdapter.setOnStarStatusChangedListener(new PlanListAdapter.OnStarStatusChangedListener() {
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

        PlanListItemTouchCallback planListItemTouchCallback = new PlanListItemTouchCallback(mDataManager);
        planListItemTouchCallback.setOnItemSwipedListener(new PlanListItemTouchCallback.OnItemSwipedListener() {
            @Override
            public void onItemSwiped(int position, int direction) {
                switch (direction) {
                    case PlanListItemTouchCallback.DIR_START:
                        notifyDeletingPlan(position);
                        break;
                    case PlanListItemTouchCallback.DIR_END:
                        notifyPlanStatusChanged(position);
                        break;
                }
            }
        });
        planListItemTouchCallback.setOnItemMovedListener(new PlanListItemTouchCallback.OnItemMovedListener() {
            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                notifyPlanSequenceChanged(fromPosition, toPosition);
            }
        });

        mPlanItemEmpty = mDataManager.getPlanCount() == 0;

        mMyPlansViewContract.showInitialView(mPlanListAdapter, new ItemTouchHelper(planListItemTouchCallback), mPlanItemEmpty);
    }

    @Override
    public void detach() {
        mMyPlansViewContract = null;
        mEventBus.unregister(this);
    }

    public void notifySwitchingViewVisibility(boolean isVisible) {
        mViewVisible = isVisible;
    }

    /** 两个参数分别指出list是否滚动到顶部和底部 */
    public void notifyPlanListScrolled(boolean top, boolean bottom) {
        int scrollEdge;
        if (top) {
            //触顶
            scrollEdge = PlanListAdapter.SCROLL_EDGE_TOP;
        } else if (bottom) {
            //触底
            scrollEdge = PlanListAdapter.SCROLL_EDGE_BOTTOM;
        } else {
            //中间
            scrollEdge = PlanListAdapter.SCROLL_EDGE_MIDDLE;
        }
        mPlanListAdapter.notifyListScrolled(scrollEdge);
    }

    public void notifyDeletingPlan(int position) {

        SystemUtil.makeShortVibrate();

        //必须先把要删除计划的引用拿到
        Plan plan = mDataManager.getPlan(position);

        mDataManager.notifyPlanDeleted(position);
        mPlanListAdapter.notifyItemRemovedAndChangingFooter(position);
        mMyPlansViewContract.onPlanDeleted(plan, position, mViewVisible);

        checkPlanItemEmptyState();

        mEventBus.post(new PlanDeletedEvent(getPresenterName(), plan.getPlanCode(), position, plan));
    }

    public void notifyCreatingPlan(Plan newPlan, int position) {
        mDataManager.notifyPlanCreated(position, newPlan);
        mPlanListAdapter.notifyItemInsertedAndChangingFooter(position);

        checkPlanItemEmptyState();

        mEventBus.post(new PlanCreatedEvent(getPresenterName(), newPlan.getPlanCode(), position));
    }

    public void notifyPlanStatusChanged(int position) {
        Plan plan = mDataManager.getPlan(position);
        //首先检测此计划是否有提醒
        if (plan.hasReminder()) {
            mDataManager.notifyReminderTimeChanged(position, Constant.UNDEFINED_TIME);
            mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), plan.getPlanCode(), position, PlanDetailChangedEvent.FIELD_REMINDER_TIME));
        }
        //执行以下语句时，只是在view上让position处的plan删除了，实际上还未被删除但也即将被删除
        //NOTE: 不能用notifyItemRemoved，会没有效果
        mPlanListAdapter.notifyItemRemoved(position);
        mDataManager.notifyPlanStatusChanged(position);
        //这里，plan的状态已经更新
        int newPosition = plan.isCompleted() ? mDataManager.getUcPlanCount() : 0;
        mPlanListAdapter.notifyItemInserted(newPosition);
        //发送事件，更新其他组件
        mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), plan.getPlanCode(), newPosition, PlanDetailChangedEvent.FIELD_PLAN_STATUS));
    }

    public void notifyPlanSequenceChanged(int fromPosition, int toPosition) {
        if (fromPosition < mDataManager.getUcPlanCount() != toPosition < mDataManager.getUcPlanCount()) return;
        //只能移动到相同完成状态的计划位置处
        mDataManager.swapPlansInPlanList(fromPosition, toPosition);
        mPlanListAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    private void checkPlanItemEmptyState() {
        boolean isEmpty = mDataManager.getPlanCount() == 0;
        if (mPlanItemEmpty == isEmpty) return;
        mPlanItemEmpty = isEmpty;
        mMyPlansViewContract.onPlanItemEmptyStateChanged(mPlanItemEmpty);
    }

    @Subscribe
    public void onDataLoaded(DataLoadedEvent event) {
        mPlanListAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onPlanCreated(PlanCreatedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        checkPlanItemEmptyState();
        mPlanListAdapter.notifyItemInsertedAndChangingFooter(event.getPosition());
        mMyPlansViewContract.onPlanCreated();
    }

    @Subscribe
    public void onTypeDetailChanged(TypeDetailChangedEvent event) {
        List<Integer> singleTypeUcPlanPosList = mDataManager.getPlanLocationListOfOneType(event.getTypeCode());
        for (int position : singleTypeUcPlanPosList) {
            //所有属于这个类型的计划都需要刷新
            mPlanListAdapter.notifyItemChanged(position, PlanListAdapter.PAYLOAD_TYPE_MARK);
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        if (event.getChangedField() == PlanDetailChangedEvent.FIELD_PLAN_STATUS) {
            //有完成情况的改变，直接全部刷新
            mPlanListAdapter.notifyDataSetChanged();
        } else {
            //其他改变的刷新，不加payload，直接刷新整个item
            mPlanListAdapter.notifyItemChanged(event.getPosition());
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        mPlanListAdapter.notifyItemRemovedAndChangingFooter(event.getPosition());
    }
}
