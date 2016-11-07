package com.zack.enderplan.interactor.presenter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.App;
import com.zack.enderplan.event.TypeDeletedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.interactor.adapter.SimpleTypeAdapter;
import com.zack.enderplan.interactor.adapter.SingleTypePlanAdapter;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.utility.Util;
import com.zack.enderplan.domain.view.TypeDetailView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class TypeDetailPresenter extends BasePresenter implements Presenter<TypeDetailView> {

    private static final int APP_BAR_STATE_EXPANDED = 1;
    private static final int APP_BAR_STATE_INTERMEDIATE = 0;
    private static final int APP_BAR_STATE_COLLAPSED = -1;

    private TypeDetailView typeDetailView;
    private DataManager dataManager;
    private SingleTypePlanAdapter mSingleTypePlanAdapter;
    private List<Plan> singleTypePlanList;
    private List<Type> mOtherTypeList;
    private int mPosition;
    private Type type;
    private EventBus mEventBus;
    private String mNoneUcPlanCountStr, mOneUcPlanCountStr, mMultiUcPlanCountFmtStr;
    private int mAppBarMaxRange, mContentEditorLayoutHeight;
    private float mLastHeaderAlpha = 1f;
    private int mAppBarState = APP_BAR_STATE_EXPANDED;
    private boolean mViewVisible;

    public TypeDetailPresenter(TypeDetailView typeDetailView, int position) {
        mEventBus = EventBus.getDefault();
        attachView(typeDetailView);
        dataManager = DataManager.getInstance();

        mPosition = position;
        type = dataManager.getType(position);

        singleTypePlanList = dataManager.getSingleTypePlanList(type.getTypeCode());
        mSingleTypePlanAdapter = new SingleTypePlanAdapter(singleTypePlanList);

        mOtherTypeList = dataManager.getTypeList(type.getTypeCode());

        Context context = App.getGlobalContext();
        mNoneUcPlanCountStr = context.getString(R.string.text_uc_plan_count_none);
        mOneUcPlanCountStr = context.getString(R.string.text_uc_plan_count_one);
        mMultiUcPlanCountFmtStr = context.getString(R.string.text_uc_plan_count_multi_format);
    }

    @Override
    public void attachView(TypeDetailView view) {
        typeDetailView = view;
        mEventBus.register(this);
    }

    @Override
    public void detachView() {
        typeDetailView = null;
        mEventBus.unregister(this);
    }

    public void setInitialView() {
        typeDetailView.showInitialView(new FormattedType(
                Color.parseColor(type.getTypeMarkColor()),
                type.getTypeMarkPattern() != null,
                Util.getDrawableResourceId(type.getTypeMarkPattern()),
                type.getTypeName(),
                type.getTypeName().substring(0, 1)
        ), getUcPlanCountStr(type.getTypeCode()), mSingleTypePlanAdapter);
    }

    public void notifyPreDrawingAppBar(int appBarMaxRange) {
        mAppBarMaxRange = appBarMaxRange;
    }

    public void notifyPreDrawingContentEditorLayout(int contentEditorLayoutHeight) {
        mContentEditorLayoutHeight = contentEditorLayoutHeight;
    }

    public void notifySwitchingViewVisibility(boolean isVisible) {
        mViewVisible = isVisible;
    }

    public void notifyAppBarScrolled(int offset) {

        if (mAppBarMaxRange == 0 || mContentEditorLayoutHeight == 0) return;

        int absOffset = Math.abs(offset);
        float headerAlpha = 1f - absOffset * 1.3f / mAppBarMaxRange;
        if (headerAlpha < 0) headerAlpha = 0;

        if ((headerAlpha == 0 || mLastHeaderAlpha == 0) && headerAlpha != mLastHeaderAlpha) {
            //刚退出透明状态或刚进入透明状态（即临界点）
            typeDetailView.onAppBarScrolledToCriticalPoint(
                    headerAlpha == 0 ? type.getTypeName() : " ",
                    headerAlpha > 0 ? 0f : mContentEditorLayoutHeight
            );
            mLastHeaderAlpha = headerAlpha;
        }

        typeDetailView.onAppBarScrolled(headerAlpha);

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
            typeDetailView.pressBack();
        } else {
            typeDetailView.backToTop();
        }
    }

    public void notifyCreatingPlan(Plan newPlan, int position, int planListPos) {
        //刷新全局列表
        dataManager.notifyPlanCreated(position, newPlan);

        //刷新此界面
        singleTypePlanList.add(planListPos, newPlan);
        mSingleTypePlanAdapter.notifyItemInserted(planListPos);
        //下面这句一定要在「刷新全局列表」后
        typeDetailView.onUcPlanCountChanged(getUcPlanCountStr(newPlan.getTypeCode()));

        mEventBus.post(new PlanCreatedEvent(getPresenterName(), newPlan.getPlanCode(), position));
    }

    public void notifyCreatingPlan(String newContent) {
        if (TextUtils.isEmpty(newContent)) {
            //内容为空
            typeDetailView.showToast(R.string.toast_create_plan_failed);
        } else {
            //创建新计划
            Plan plan = new Plan(Util.makeCode());
            plan.setContent(newContent);
            plan.setTypeCode(type.getTypeCode());
            plan.setCreationTime(System.currentTimeMillis());

            notifyCreatingPlan(plan, 0, 0);

            typeDetailView.showToast(R.string.toast_create_plan_success);
            typeDetailView.onPlanCreated();
        }
    }

    public void notifyPlanItemClicked(int position) {
        int posInPlanList = dataManager.getPlanLocationInPlanList(singleTypePlanList.get(position).getPlanCode());
        typeDetailView.onPlanItemClicked(posInPlanList);
    }

    public void notifyPlanStarStatusChanged(int position) {
        //获取plan
        Plan plan = singleTypePlanList.get(position);

        int posInPlanList = dataManager.getPlanLocationInPlanList(plan.getPlanCode());

        dataManager.notifyStarStatusChanged(posInPlanList);

        //更新界面
        mSingleTypePlanAdapter.notifyItemChanged(position);
        //通知AllPlans界面更新
        mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), plan.getPlanCode(), posInPlanList, PlanDetailChangedEvent.FIELD_STAR_STATUS));
    }

    public void notifyDeletingPlan(int position) {

        Util.makeShortVibrate();

        Plan plan = singleTypePlanList.get(position);
        int planListPos = dataManager.getPlanLocationInPlanList(plan.getPlanCode());

        //刷新此界面上的列表
        singleTypePlanList.remove(position);
        mSingleTypePlanAdapter.notifyItemRemoved(position);
        typeDetailView.onPlanDeleted(plan, position, planListPos, mViewVisible);
        if (!plan.isCompleted()) {
            //说明刚才删除的是个未完成的计划，需要修改界面上的内容
            typeDetailView.onUcPlanCountChanged(getUcPlanCountStr(type.getTypeCode()));
        }

        //刷新全局列表
        dataManager.notifyPlanDeleted(planListPos);

        //刷新其他组件
        mEventBus.post(new PlanDeletedEvent(getPresenterName(), plan.getPlanCode(), planListPos, plan));
    }

    public void notifySwitchingPlanStatus(int position) {

        Plan plan = singleTypePlanList.get(position);
        int planListPos = dataManager.getPlanLocationInPlanList(plan.getPlanCode());

        //检测是否设置了提醒
        if (plan.hasReminder()) {
            dataManager.notifyReminderTimeChanged(planListPos, 0);
            //TODO mSingleTypePlanAdapter.notifyItemChanged(position)这句不要，MyPlansPresenter也要删去这一句
            mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), plan.getPlanCode(), planListPos, PlanDetailChangedEvent.FIELD_REMINDER_TIME));
        }
        //刷新全局列表
        dataManager.notifyPlanStatusChanged(planListPos);

        //刷新此列表
        singleTypePlanList.remove(position);
        mSingleTypePlanAdapter.notifyItemRemoved(position);
        //下面这句一定要在「刷新全局列表」后
        int newPosition = plan.isCompleted() ? dataManager.getUcPlanCountOfOneType(plan.getTypeCode()) : 0;
        singleTypePlanList.add(newPosition, plan);
        mSingleTypePlanAdapter.notifyItemInserted(newPosition);
        typeDetailView.onUcPlanCountChanged(getUcPlanCountStr(type.getTypeCode()));

        //发送事件，更新其他组件
        mEventBus.post(new PlanDetailChangedEvent(
                getPresenterName(),
                plan.getPlanCode(),
                plan.isCompleted() ? dataManager.getUcPlanCount() : 0,
                PlanDetailChangedEvent.FIELD_PLAN_STATUS
        ));
    }

    public void notifyTypeEditingButtonClicked() {
        typeDetailView.enterEditType(mPosition, mAppBarState == APP_BAR_STATE_EXPANDED);
    }

    public void notifyTypeDeletionButtonClicked(boolean deletePlan) {
        if (dataManager.getTypeCount() == 1) {
            //如果只剩一个类型，禁止删除
            typeDetailView.onDetectedDeletingLastType();
            return;
        }
        if (!deletePlan && dataManager.isPlanOfOneTypeExists(type.getTypeCode())) {
            //如果不删除类型连带的计划，且检测到类型有连带的计划，弹移动计划到其他类型的对话框
            typeDetailView.onDetectedTypeNotEmpty();
            //typeDetailView.showToast(R.string.toast_type_not_empty);
        } else {
            //如果决定要删除连带的计划，或检测到类型没有连带的计划，直接弹删除确认对话框
            typeDetailView.showTypeDeletionConfirmationDialog(type.getTypeName());
        }
    }

    public void notifyMovePlanButtonClicked() {
        typeDetailView.showMovePlanDialog(
                dataManager.getPlanCountOfOneType(type.getTypeCode()),
                new SimpleTypeAdapter(mOtherTypeList, SimpleTypeAdapter.STYLE_DIALOG)
        );
    }

    public void notifyTypeItemInMovePlanDialogClicked(int position) {
        Type toType = mOtherTypeList.get(position);
        typeDetailView.showPlanMigrationConfirmationDialog(type.getTypeName(), toType.getTypeName(), toType.getTypeCode());
    }

    /**
     * 通知model删除类型
     * @param migration 是否将与此类型有关的计划迁移到另一类型
     * @param toTypeCode 要迁移到的类型代码
     */
    public void notifyDeletingType(boolean migration, String toTypeCode) {
        if (migration) {
            List<Integer> planPosList = dataManager.getPlanLocationListOfOneType(type.getTypeCode());
            dataManager.migratePlan(planPosList, toTypeCode);
            for (int position : planPosList) {
                mEventBus.post(new PlanDetailChangedEvent(
                        getPresenterName(),
                        dataManager.getPlan(position).getPlanCode(),
                        position,
                        PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN
                ));
            }
        }
        dataManager.notifyTypeDeleted(mPosition);
        mEventBus.post(new TypeDeletedEvent(getPresenterName(), type.getTypeCode(), mPosition, type));
        typeDetailView.exitTypeDetail();
    }

    /** 注意！必须在更新UcPlanCountOfEachTypeMap之后调用才有效果 */
    private String getUcPlanCountStr(String typeCode) {
        Integer count = dataManager.getUcPlanCountOfEachTypeMap().get(typeCode);
        if (count == null) {
            count = 0;
        }
        switch (count) {
            case 0:
                return mNoneUcPlanCountStr;
            case 1:
                return mOneUcPlanCountStr;
            default:
                return String.format(mMultiUcPlanCountFmtStr, count);
        }
    }

    /** 计算给定planCode的计划在singleTypePlanList中的位置 */
    private int getPosInSingleTypePlanList(String planCode) {
        for (int i = 0; i < singleTypePlanList.size(); i++) {
            if (singleTypePlanList.get(i).getPlanCode().equals(planCode)) {
                return i;
            }
        }
        return -1;
    }

    @Subscribe
    public void onTypeDetailChanged(TypeDetailChangedEvent event) {
        if (!type.getTypeCode().equals(event.getTypeCode()) || event.getEventSource().equals(getPresenterName())) return;
        switch (event.getChangedField()) {
            case TypeDetailChangedEvent.FIELD_TYPE_NAME:
                typeDetailView.onTypeNameChanged(type.getTypeName(), type.getTypeName().substring(0, 1));
                break;
            case TypeDetailChangedEvent.FIELD_TYPE_MARK_COLOR:
                typeDetailView.onTypeMarkColorChanged(Color.parseColor(type.getTypeMarkColor()));
                break;
            case TypeDetailChangedEvent.FIELD_TYPE_MARK_PATTERN:
                typeDetailView.onTypeMarkPatternChanged(type.getTypeMarkPattern() != null, Util.getDrawableResourceId(type.getTypeMarkPattern()));
                break;
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {

        if (event.getEventSource().equals(getPresenterName())) return;

        //刚才发生变化的计划在singleTypePlanList中的位置
        int position = getPosInSingleTypePlanList(event.getPlanCode());

        if (event.getChangedField() == PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN || event.getChangedField() == PlanDetailChangedEvent.FIELD_PLAN_STATUS) {
            //说明有类型或完成情况的更新，需要从singleTypePlanList中移除

            //更新此列表
            singleTypePlanList.remove(position);
            mSingleTypePlanAdapter.notifyItemRemoved(position);

            //更新显示的未完成计划数量
            typeDetailView.onUcPlanCountChanged(getUcPlanCountStr(type.getTypeCode()));
        } else {
            //其他更新，需要刷新singleTypeUcPlanList
            mSingleTypePlanAdapter.notifyItemChanged(position);
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {

        if (event.getEventSource().equals(getPresenterName())) return;

        //计算刚才删除的计划在这个list中的位置
        int position = getPosInSingleTypePlanList(event.getPlanCode());

        //刷新list
        singleTypePlanList.remove(position);
        mSingleTypePlanAdapter.notifyItemRemoved(position);

        if (!event.getDeletedPlan().isCompleted()) {
            //说明刚才删除的是个未完成的计划，需要修改界面上的内容
            typeDetailView.onUcPlanCountChanged(getUcPlanCountStr(type.getTypeCode()));
        }
    }
}
