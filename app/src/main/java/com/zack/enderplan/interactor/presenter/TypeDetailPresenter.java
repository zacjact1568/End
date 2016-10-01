package com.zack.enderplan.interactor.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.App;
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

    private TypeDetailView typeDetailView;
    private DataManager dataManager;
    private PlanSingleTypeAdapter planSingleTypeAdapter;
    private List<Plan> singleTypeUcPlanList;
    private Type type;
    private EventBus mEventBus;
    private String noneUcPlan, oneUcPlan, multiUcPlan;

    public TypeDetailPresenter(TypeDetailView typeDetailView, int position) {
        mEventBus = EventBus.getDefault();
        attachView(typeDetailView);
        dataManager = DataManager.getInstance();

        type = dataManager.getType(position);

        singleTypeUcPlanList = dataManager.getSingleTypeUcPlanList(type.getTypeCode());
        planSingleTypeAdapter = new PlanSingleTypeAdapter(singleTypeUcPlanList);

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
        typeDetailView.showInitialView(
                dataManager.findColorResByTypeMark(type.getTypeMark()),
                type.getTypeName().substring(0, 1),
                type.getTypeName(),
                getUcPlanCountStr(type.getTypeCode()),
                planSingleTypeAdapter
        );
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

        Plan plan = singleTypeUcPlanList.get(position);

        //操作list，用posInPlanList（而不是position）标记位置
        int posInPlanList = dataManager.getPlanLocationInPlanList(plan.getPlanCode());

        dataManager.notifyPlanStatusChanged(posInPlanList);

        int newPosInPlanList = dataManager.getUcPlanCount();
        //通知AllPlansPresenter（更新计划列表）、AllTypesPresenter（更新类型列表）与本Presenter更新界面
        mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), plan.getPlanCode(), newPosInPlanList, PlanDetailChangedEvent.FIELD_PLAN_STATUS));
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
