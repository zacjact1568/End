package com.zack.enderplan.interactor.presenter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.App;
import com.zack.enderplan.event.TypeDeletedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.interactor.adapter.SimpleTypeAdapter;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.utility.Util;
import com.zack.enderplan.domain.view.TypeDetailView;
import com.zack.enderplan.interactor.adapter.PlanSingleTypeAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class TypeDetailPresenter extends BasePresenter implements Presenter<TypeDetailView> {

    private static final int APP_BAR_STATE_EXPANDED = 1;
    private static final int APP_BAR_STATE_INTERMEDIATE = 0;
    private static final int APP_BAR_STATE_COLLAPSED = -1;

    private TypeDetailView typeDetailView;
    private DataManager dataManager;
    private PlanSingleTypeAdapter planSingleTypeAdapter;
    private List<Plan> singleTypeUcPlanList;
    private List<Type> mOtherTypeList;
    private int mPosition;
    private Type type;
    private EventBus mEventBus;
    private String noneUcPlan, oneUcPlan, multiUcPlan;
    private float mLastHeaderOpacity = 1f;
    private int mAppBarState = APP_BAR_STATE_EXPANDED;

    public TypeDetailPresenter(TypeDetailView typeDetailView, int position) {
        mEventBus = EventBus.getDefault();
        attachView(typeDetailView);
        dataManager = DataManager.getInstance();

        mPosition = position;
        type = dataManager.getType(position);

        singleTypeUcPlanList = dataManager.getSingleTypeUcPlanList(type.getTypeCode());
        planSingleTypeAdapter = new PlanSingleTypeAdapter(singleTypeUcPlanList);

        mOtherTypeList = dataManager.getTypeList(type.getTypeCode());

        Context context = App.getGlobalContext();
        noneUcPlan = context.getResources().getString(R.string.uc_plan_count_of_each_type_none);
        oneUcPlan = context.getResources().getString(R.string.uc_plan_count_of_each_type_one);
        multiUcPlan = context.getResources().getString(R.string.uc_plan_count_of_each_type_multi);
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
                type.getTypeName().substring(0, 1),
                type.getTypeName(),
                getUcPlanCountStr(type.getTypeCode())
        ), planSingleTypeAdapter);
    }

    public void notifyAppBarScrolled(int offset, int maxRange) {
        float alpha = 1f - Math.abs(offset) * 1.3f / maxRange;
        if (alpha < 0) alpha = 0;
        typeDetailView.changeHeaderOpacity(alpha);

        if ((alpha == 0 || mLastHeaderOpacity == 0) && alpha != mLastHeaderOpacity) {
            //刚退出透明状态或刚进入透明状态
            typeDetailView.changeTitle(alpha == 0 ? type.getTypeName() : " ");
            typeDetailView.changeEditorVisibility(alpha > 0);
            mLastHeaderOpacity = alpha;
        }

        if (offset == 0) {
            mAppBarState = APP_BAR_STATE_EXPANDED;
        } else if (Math.abs(offset) == maxRange) {
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

    public void notifyPlanCreation(String newContent) {
        if (TextUtils.isEmpty(newContent)) {
            //内容为空
            typeDetailView.onPlanCreationFailed();
        } else {
            //创建新计划
            Plan plan = new Plan(Util.makeCode());
            plan.setContent(newContent);
            plan.setTypeCode(type.getTypeCode());
            plan.setCreationTime(System.currentTimeMillis());

            dataManager.notifyPlanCreated(plan);

            //通知AllPlansPresenter（更新计划列表）与AllTypesPresenter（更新类型列表）
            mEventBus.post(new PlanCreatedEvent(
                    getPresenterName(),
                    dataManager.getRecentlyCreatedPlan().getPlanCode(),
                    dataManager.getRecentlyCreatedPlanLocation()
            ));

            //更新此fragment中的数据
            singleTypeUcPlanList.add(0, plan);
            planSingleTypeAdapter.notifyItemInserted(0);
            typeDetailView.onPlanCreationSuccess(getUcPlanCountStr(type.getTypeCode()));
        }
    }

    public void notifyPlanItemClicked(int position) {
        int posInPlanList = dataManager.getPlanLocationInPlanList(singleTypeUcPlanList.get(position).getPlanCode());
        typeDetailView.onPlanItemClicked(posInPlanList);
    }

    public void notifyPlanStarStatusChanged(int position) {
        //获取plan
        Plan plan = singleTypeUcPlanList.get(position);

        int posInPlanList = dataManager.getPlanLocationInPlanList(plan.getPlanCode());

        dataManager.notifyStarStatusChanged(posInPlanList);

        //更新界面
        planSingleTypeAdapter.notifyItemChanged(position);
        //通知AllPlans界面更新
        mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), plan.getPlanCode(), posInPlanList, PlanDetailChangedEvent.FIELD_STAR_STATUS));
    }

    public void notifyPlanCompleted(int position) {
        //获取plan
        Plan plan = singleTypeUcPlanList.get(position);
        //操作list，用posInPlanList（而不是position）标记位置
        int posInPlanList = dataManager.getPlanLocationInPlanList(plan.getPlanCode());
        //检测是否设置了提醒
        if (plan.getReminderTime() != 0) {
            //有设置提醒，需要移除
            dataManager.notifyReminderTimeChanged(posInPlanList, 0);
            mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), plan.getPlanCode(), posInPlanList, PlanDetailChangedEvent.FIELD_REMINDER_TIME));
        }
        dataManager.notifyPlanStatusChanged(posInPlanList);
        //新位置
        int newPosInPlanList = dataManager.getUcPlanCount();
        //更新此列表
        singleTypeUcPlanList.remove(position);
        planSingleTypeAdapter.notifyItemRemoved(position);
        //更新显示的未完成计划数量
        typeDetailView.onUcPlanCountChanged(getUcPlanCountStr(type.getTypeCode()));
        //发送事件，更新其他组件
        mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), plan.getPlanCode(), newPosInPlanList, PlanDetailChangedEvent.FIELD_PLAN_STATUS));
    }

    public void notifyTypeEditingButtonClicked() {
        typeDetailView.enterEditType(mPosition);
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
                return noneUcPlan;
            case 1:
                return oneUcPlan;
            default:
                return String.format("%d " + multiUcPlan, count);
        }
    }

    /** 计算给定planCode的计划在singleTypeUcPlanList中的位置 */
    private int getPosInSingleTypeUcPlanList(String planCode) {
        for (int i = 0; i < singleTypeUcPlanList.size(); i++) {
            if (singleTypeUcPlanList.get(i).getPlanCode().equals(planCode)) {
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
                //TODO pattern
                break;
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {

        if (event.getEventSource().equals(getPresenterName())) return;

        //刚才发生变化的计划在singleTypeUcPlanList中的位置
        int position = getPosInSingleTypeUcPlanList(event.getPlanCode());

        if (event.getChangedField() == PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN || event.getChangedField() == PlanDetailChangedEvent.FIELD_PLAN_STATUS) {
            //说明有类型或完成情况的更新，需要从singleTypeUcPlanList中移除

            //更新此列表
            singleTypeUcPlanList.remove(position);
            planSingleTypeAdapter.notifyItemRemoved(position);

            //更新显示的未完成计划数量
            typeDetailView.onUcPlanCountChanged(getUcPlanCountStr(type.getTypeCode()));
        } else {
            //其他更新，需要刷新singleTypeUcPlanList
            planSingleTypeAdapter.notifyItemChanged(position);
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {

        if (event.getEventSource().equals(getPresenterName())) return;

        //计算刚才删除的计划在这个list中的位置
        int position = getPosInSingleTypeUcPlanList(event.getPlanCode());

        //刷新list
        singleTypeUcPlanList.remove(position);
        planSingleTypeAdapter.notifyItemRemoved(position);

        if (!event.getDeletedPlan().isCompleted()) {
            //说明刚才删除的是个未完成的计划，需要修改界面上的内容
            typeDetailView.onUcPlanCountChanged(getUcPlanCountStr(type.getTypeCode()));
        }
    }
}
