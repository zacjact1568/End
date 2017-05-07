package com.zack.enderplan.presenter;

import android.graphics.Color;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.StringUtil;
import com.zack.enderplan.util.SystemUtil;
import com.zack.enderplan.event.TypeDeletedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.view.adapter.SimpleTypeListAdapter;
import com.zack.enderplan.view.adapter.SingleTypePlanListAdapter;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.util.CommonUtil;
import com.zack.enderplan.view.callback.PlanListItemTouchCallback;
import com.zack.enderplan.view.callback.SingleTypePlanListItemTouchCallback;
import com.zack.enderplan.view.contract.TypeDetailViewContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class TypeDetailPresenter extends BasePresenter {

    private static final int APP_BAR_STATE_EXPANDED = 1;
    private static final int APP_BAR_STATE_INTERMEDIATE = 0;
    private static final int APP_BAR_STATE_COLLAPSED = -1;

    private TypeDetailViewContract mTypeDetailViewContract;
    private DataManager mDataManager;
    private SingleTypePlanListAdapter mSingleTypePlanListAdapter;
    private List<Plan> mSingleTypePlanList;
    private List<Type> mOtherTypeList;
    private int mTypeListPosition;
    private Type mType;
    private EventBus mEventBus;
    private int mAppBarMaxRange, mEditorLayoutHeight;
    private float mLastHeaderAlpha = 1f;
    private int mAppBarState = APP_BAR_STATE_EXPANDED;
    private boolean mViewVisible;

    @Inject
    TypeDetailPresenter(TypeDetailViewContract typeDetailViewContract, int typeListPosition, DataManager dataManager, EventBus eventBus) {
        mTypeDetailViewContract = typeDetailViewContract;
        mTypeListPosition = typeListPosition;
        mDataManager = dataManager;
        mEventBus = eventBus;

        mType = mDataManager.getType(mTypeListPosition);
        mSingleTypePlanList = mDataManager.getSingleTypePlanList(mType.getTypeCode());
        mSingleTypePlanListAdapter = new SingleTypePlanListAdapter(mSingleTypePlanList);
        mSingleTypePlanListAdapter.setOnPlanItemClickListener(new SingleTypePlanListAdapter.OnPlanItemClickListener() {
            @Override
            public void onPlanItemClick(int position) {
                mTypeDetailViewContract.onPlanItemClicked(mDataManager.getPlanLocationInPlanList(mSingleTypePlanList.get(position).getPlanCode()));
            }
        });
        mSingleTypePlanListAdapter.setOnStarStatusChangedListener(new SingleTypePlanListAdapter.OnStarStatusChangedListener() {
            @Override
            public void onStarStatusChanged(int position) {
                Plan plan = mSingleTypePlanList.get(position);
                int planListPos = mDataManager.getPlanLocationInPlanList(plan.getPlanCode());
                mDataManager.notifyStarStatusChanged(planListPos);
                mEventBus.post(new PlanDetailChangedEvent(
                        getPresenterName(),
                        plan.getPlanCode(),
                        planListPos,
                        PlanDetailChangedEvent.FIELD_STAR_STATUS
                ));
            }
        });
        mOtherTypeList = mDataManager.getTypeList(mType.getTypeCode());
    }

    @Override
    public void attach() {
        mEventBus.register(this);

        SingleTypePlanListItemTouchCallback singleTypePlanListItemTouchCallback = new SingleTypePlanListItemTouchCallback(mSingleTypePlanList);
        singleTypePlanListItemTouchCallback.setOnItemSwipedListener(new PlanListItemTouchCallback.OnItemSwipedListener() {
            @Override
            public void onItemSwiped(int position, int direction) {
                switch (direction) {
                    case PlanListItemTouchCallback.DIR_START:
                        notifyDeletingPlan(position);
                        break;
                    case PlanListItemTouchCallback.DIR_END:
                        notifySwitchingPlanStatus(position);
                        break;
                }
            }
        });
        singleTypePlanListItemTouchCallback.setOnItemMovedListener(new PlanListItemTouchCallback.OnItemMovedListener() {
            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                notifyPlanSequenceChanged(fromPosition, toPosition);
            }
        });

        mTypeDetailViewContract.showInitialView(new FormattedType(
                Color.parseColor(mType.getTypeMarkColor()),
                mType.getTypeMarkPattern() != null,
                ResourceUtil.getDrawableResourceId(mType.getTypeMarkPattern()),
                mType.getTypeName(),
                StringUtil.getFirstChar(mType.getTypeName())
        ), getUcPlanCountStr(mType.getTypeCode()), mSingleTypePlanListAdapter, new ItemTouchHelper(singleTypePlanListItemTouchCallback));
    }

    @Override
    public void detach() {
        mTypeDetailViewContract = null;
        mEventBus.unregister(this);
    }

    public void notifyPreDrawingAppBar(int appBarMaxRange) {
        mAppBarMaxRange = appBarMaxRange;
    }

    public void notifyPreDrawingEditorLayout(int editorLayoutHeight) {
        mEditorLayoutHeight = editorLayoutHeight;
    }

    public void notifySwitchingViewVisibility(boolean isVisible) {
        mViewVisible = isVisible;
    }

    public void notifyAppBarScrolled(int offset) {

        if (mAppBarMaxRange == 0 || mEditorLayoutHeight == 0) return;

        int absOffset = Math.abs(offset);
        float headerAlpha = 1f - absOffset * 1.3f / mAppBarMaxRange;
        if (headerAlpha < 0) headerAlpha = 0;

        if ((headerAlpha == 0 || mLastHeaderAlpha == 0) && headerAlpha != mLastHeaderAlpha) {
            //刚退出透明状态或刚进入透明状态（即临界点）
            mTypeDetailViewContract.onAppBarScrolledToCriticalPoint(
                    headerAlpha == 0 ? mType.getTypeName() : " ",
                    headerAlpha > 0 ? 0f : mEditorLayoutHeight
            );
            mLastHeaderAlpha = headerAlpha;
        }

        mTypeDetailViewContract.onAppBarScrolled(headerAlpha);

        if (absOffset == 0) {
            mAppBarState = APP_BAR_STATE_EXPANDED;
        } else if (absOffset == mAppBarMaxRange) {
            mAppBarState = APP_BAR_STATE_COLLAPSED;
        } else {
            mAppBarState = APP_BAR_STATE_INTERMEDIATE;
        }
    }

    public void notifyBackPressed() {
        if (mAppBarState == APP_BAR_STATE_EXPANDED) {
            mTypeDetailViewContract.pressBack();
        } else {
            mTypeDetailViewContract.backToTop();
        }
    }

    public void notifyCreatingPlan(Plan newPlan, int position, int planListPos) {
        //刷新全局列表
        mDataManager.notifyPlanCreated(planListPos, newPlan);

        //刷新此界面
        mSingleTypePlanList.add(position, newPlan);
        mSingleTypePlanListAdapter.notifyItemInserted(position);
        //下面这句一定要在「刷新全局列表」后
        mTypeDetailViewContract.onUcPlanCountChanged(getUcPlanCountStr(newPlan.getTypeCode()));

        mEventBus.post(new PlanCreatedEvent(getPresenterName(), newPlan.getPlanCode(), planListPos));
    }

    public void notifyCreatingPlan(String newContent) {
        if (TextUtils.isEmpty(newContent)) {
            //内容为空
            mTypeDetailViewContract.showToast(R.string.toast_create_plan_failed);
        } else {
            //创建新计划
            Plan plan = new Plan(CommonUtil.makeCode());
            plan.setContent(newContent);
            plan.setTypeCode(mType.getTypeCode());
            plan.setCreationTime(System.currentTimeMillis());

            notifyCreatingPlan(plan, 0, 0);

            mTypeDetailViewContract.showToast(R.string.toast_create_plan_success);
            mTypeDetailViewContract.onPlanCreated();
        }
    }

    public void notifyDeletingPlan(int position) {

        SystemUtil.makeShortVibrate();

        Plan plan = mSingleTypePlanList.get(position);
        int planListPos = mDataManager.getPlanLocationInPlanList(plan.getPlanCode());

        //刷新全局列表
        mDataManager.notifyPlanDeleted(planListPos);

        //刷新此界面上的列表
        mSingleTypePlanList.remove(position);
        mSingleTypePlanListAdapter.notifyItemRemoved(position);
        mTypeDetailViewContract.onPlanDeleted(plan, position, planListPos, mViewVisible);
        //下面这句一定要在「刷新全局列表」后
        if (!plan.isCompleted()) {
            //说明刚才删除的是个未完成的计划，需要修改界面上的内容
            mTypeDetailViewContract.onUcPlanCountChanged(getUcPlanCountStr(mType.getTypeCode()));
        }

        //刷新其他组件
        mEventBus.post(new PlanDeletedEvent(getPresenterName(), plan.getPlanCode(), planListPos, plan));
    }

    public void notifySwitchingPlanStatus(int position) {

        Plan plan = mSingleTypePlanList.get(position);
        int planListPos = mDataManager.getPlanLocationInPlanList(plan.getPlanCode());

        //检测是否设置了提醒
        if (plan.hasReminder()) {
            mDataManager.notifyReminderTimeChanged(planListPos, Constant.UNDEFINED_TIME);
            //TODO mSingleTypePlanListAdapter.notifyItemChanged(position)这句不要，MyPlansPresenter也要删去这一句
            mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), plan.getPlanCode(), planListPos, PlanDetailChangedEvent.FIELD_REMINDER_TIME));
        }
        //刷新全局列表
        mDataManager.notifyPlanStatusChanged(planListPos);

        //刷新此界面上的列表
        mSingleTypePlanList.remove(position);
        mSingleTypePlanListAdapter.notifyItemRemoved(position);
        //下面这句一定要在「刷新全局列表」后
        int newPosition = plan.isCompleted() ? mDataManager.getUcPlanCountOfOneType(plan.getTypeCode()) : 0;
        mSingleTypePlanList.add(newPosition, plan);
        mSingleTypePlanListAdapter.notifyItemInserted(newPosition);
        mTypeDetailViewContract.onUcPlanCountChanged(getUcPlanCountStr(mType.getTypeCode()));

        //发送事件，更新其他组件
        mEventBus.post(new PlanDetailChangedEvent(
                getPresenterName(),
                plan.getPlanCode(),
                plan.isCompleted() ? mDataManager.getUcPlanCount() : 0,
                PlanDetailChangedEvent.FIELD_PLAN_STATUS
        ));
    }

    public void notifyPlanSequenceChanged(int fromPosition, int toPosition) {
        int fromPlanListPos = mDataManager.getPlanLocationInPlanList(mSingleTypePlanList.get(fromPosition).getPlanCode());
        int toPlanListPos = mDataManager.getPlanLocationInPlanList(mSingleTypePlanList.get(toPosition).getPlanCode());
        if (fromPlanListPos < mDataManager.getUcPlanCount() != toPlanListPos < mDataManager.getUcPlanCount()) return;
        //更新全局列表
        mDataManager.swapPlansInPlanList(fromPlanListPos, toPlanListPos);
        //更新此列表
        Collections.swap(mSingleTypePlanList, fromPosition, toPosition);
        mSingleTypePlanListAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    public void notifyTypeEditingButtonClicked() {
        mTypeDetailViewContract.enterEditType(mTypeListPosition, mAppBarState == APP_BAR_STATE_EXPANDED);
    }

    public void notifyTypeDeletionButtonClicked(boolean deletePlan) {
        if (mDataManager.getTypeCount() == 1) {
            //如果只剩一个类型，禁止删除
            mTypeDetailViewContract.onDetectedDeletingLastType();
            return;
        }
        if (!deletePlan && mDataManager.isPlanOfOneTypeExists(mType.getTypeCode())) {
            //如果不删除类型连带的计划，且检测到类型有连带的计划，弹移动计划到其他类型的对话框
            mTypeDetailViewContract.onDetectedTypeNotEmpty();
            //mTypeDetailViewContract.showToast(R.string.toast_type_not_empty);
        } else {
            //如果决定要删除连带的计划，或检测到类型没有连带的计划，直接弹删除确认对话框
            mTypeDetailViewContract.showTypeDeletionConfirmationDialog(mType.getTypeName());
        }
    }

    public void notifyMovePlanButtonClicked() {
        mTypeDetailViewContract.showMovePlanDialog(
                mDataManager.getPlanCountOfOneType(mType.getTypeCode()),
                new SimpleTypeListAdapter(mOtherTypeList, SimpleTypeListAdapter.STYLE_DIALOG)
        );
    }

    public void notifyTypeItemInMovePlanDialogClicked(int position) {
        Type toType = mOtherTypeList.get(position);
        mTypeDetailViewContract.showPlanMigrationConfirmationDialog(mType.getTypeName(), toType.getTypeName(), toType.getTypeCode());
    }

    /**
     * 通知model删除类型
     * @param migration 是否将与此类型有关的计划迁移到另一类型
     * @param toTypeCode 要迁移到的类型代码
     */
    public void notifyDeletingType(boolean migration, String toTypeCode) {
        if (migration) {
            List<Integer> planPosList = mDataManager.getPlanLocationListOfOneType(mType.getTypeCode());
            mDataManager.migratePlan(planPosList, toTypeCode);
            for (int position : planPosList) {
                mEventBus.post(new PlanDetailChangedEvent(
                        getPresenterName(),
                        mDataManager.getPlan(position).getPlanCode(),
                        position,
                        PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN
                ));
            }
        }
        mDataManager.notifyTypeDeleted(mTypeListPosition);
        mEventBus.post(new TypeDeletedEvent(getPresenterName(), mType.getTypeCode(), mTypeListPosition, mType));
        mTypeDetailViewContract.exit();
    }

    /** 注意！必须在更新UcPlanCountOfEachTypeMap之后调用才有效果 */
    private String getUcPlanCountStr(String typeCode) {
        Integer count = mDataManager.getUcPlanCountOfEachTypeMap().get(typeCode);
        if (count == null) {
            count = 0;
        }
        return ResourceUtil.getQuantityString(R.plurals.text_uc_plan_count, count);
    }

    /** 计算给定planCode的计划在singleTypePlanList中的位置 */
    private int getPosInSingleTypePlanList(String planCode) {
        for (int i = 0; i < mSingleTypePlanList.size(); i++) {
            if (mSingleTypePlanList.get(i).getPlanCode().equals(planCode)) {
                return i;
            }
        }
        return -1;
    }

    /** 计算新加的plan在singleTypePlanList中的插入位置 */
    private int getInsertionPosInSingleTypePlanList(long creationTime, long completionTime) {
        for (int i = 0; i < mSingleTypePlanList.size(); i++) {
            Plan plan = mSingleTypePlanList.get(i);
            long creationTimeInList = plan.getCreationTime();
            long completionTimeInList = plan.getCompletionTime();
            if ((creationTimeInList != Constant.UNDEFINED_TIME && completionTimeInList == Constant.UNDEFINED_TIME && creationTimeInList < creationTime)
                    || (creationTimeInList == Constant.UNDEFINED_TIME && completionTimeInList != Constant.UNDEFINED_TIME && completionTimeInList < completionTime)) {
                return i;
            }
        }
        return -1;
    }

    @Subscribe
    public void onTypeDetailChanged(TypeDetailChangedEvent event) {
        if (!mType.getTypeCode().equals(event.getTypeCode()) || event.getEventSource().equals(getPresenterName())) return;
        switch (event.getChangedField()) {
            case TypeDetailChangedEvent.FIELD_TYPE_NAME:
                mTypeDetailViewContract.onTypeNameChanged(mType.getTypeName(), StringUtil.getFirstChar(mType.getTypeName()));
                break;
            case TypeDetailChangedEvent.FIELD_TYPE_MARK_COLOR:
                mTypeDetailViewContract.onTypeMarkColorChanged(Color.parseColor(mType.getTypeMarkColor()));
                break;
            case TypeDetailChangedEvent.FIELD_TYPE_MARK_PATTERN:
                mTypeDetailViewContract.onTypeMarkPatternChanged(mType.getTypeMarkPattern() != null, ResourceUtil.getDrawableResourceId(mType.getTypeMarkPattern()));
                break;
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {

        if (event.getEventSource().equals(getPresenterName())) return;

        //刚才发生变化的计划在singleTypePlanList中的位置
        int position = getPosInSingleTypePlanList(event.getPlanCode());
        //Plan从DataManager中取，因为上面的position可能为-1
        Plan plan = mDataManager.getPlan(event.getPosition());

        //若变化的plan不在mSingleTypePlanList中（position == -1），则说明此plan是外来的
        //则还需要判断【类型】是否由【其他类型】变为【当前类型】，则判断（改变的是不是【类型】&& 改变后的类型是不是【当前类型】）
        if (position == -1 && !(event.getChangedField() == PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN && plan.getTypeCode().equals(mType.getTypeCode()))) return;

        switch (event.getChangedField()) {
            case PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN:
                if (plan.getTypeCode().equals(mType.getTypeCode())) {
                    //某个plan由其他类型变成了当前页的类型，需要添加到singleTypePlanList中
                    //计算插入位置
                    position = getInsertionPosInSingleTypePlanList(plan.getCreationTime(), plan.getCompletionTime());
                    mSingleTypePlanList.add(position, plan);
                    mSingleTypePlanListAdapter.notifyItemInserted(position);
                } else {
                    //某个plan由当前页的类型变成了其他类型，需要从singleTypePlanList中移除
                    mSingleTypePlanList.remove(position);
                    mSingleTypePlanListAdapter.notifyItemRemoved(position);
                }
                //更新显示的未完成计划数量
                mTypeDetailViewContract.onUcPlanCountChanged(getUcPlanCountStr(mType.getTypeCode()));
                break;
            case PlanDetailChangedEvent.FIELD_PLAN_STATUS:
                //有完成情况的改变，直接全部刷新
                mSingleTypePlanListAdapter.notifyDataSetChanged();
                break;
            default:
                //其他改变的刷新，不加payload，直接刷新整个item
                mSingleTypePlanListAdapter.notifyItemChanged(position);
                break;
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {

        if (event.getEventSource().equals(getPresenterName())) return;

        //计算刚才删除的计划在这个list中的位置
        int position = getPosInSingleTypePlanList(event.getPlanCode());

        //刷新list
        mSingleTypePlanList.remove(position);
        mSingleTypePlanListAdapter.notifyItemRemoved(position);

        if (!event.getDeletedPlan().isCompleted()) {
            //说明刚才删除的是个未完成的计划，需要修改界面上的内容
            mTypeDetailViewContract.onUcPlanCountChanged(getUcPlanCountStr(mType.getTypeCode()));
        }
    }
}
