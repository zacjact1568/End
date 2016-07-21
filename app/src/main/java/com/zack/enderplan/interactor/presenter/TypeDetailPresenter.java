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
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.utility.Util;
import com.zack.enderplan.domain.view.TypeDetailView;
import com.zack.enderplan.interactor.adapter.PlanSingleTypeAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class TypeDetailPresenter implements Presenter<TypeDetailView> {

    private TypeDetailView typeDetailView;
    private DataManager dataManager;
    private PlanSingleTypeAdapter planSingleTypeAdapter;
    private List<Plan> singleTypeUcPlanList;
    private Type type;
    private String noneUcPlan, oneUcPlan, multiUcPlan;

    public TypeDetailPresenter(TypeDetailView typeDetailView, int position) {
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void detachView() {
        typeDetailView = null;
        EventBus.getDefault().unregister(this);
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
            EventBus.getDefault().post(new PlanCreatedEvent(
                    dataManager.getRecentlyCreatedPlan().getPlanCode(),
                    dataManager.getRecentlyCreatedPlanLocation()
            ));

            //通知HomePresenter（更新侧栏header）
            EventBus.getDefault().post(new UcPlanCountChangedEvent());

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
        EventBus.getDefault().post(new PlanDetailChangedEvent(plan.getPlanCode(), posInPlanList, false, false, -1));
    }

    public void notifyPlanCompleted(int position) {

        Plan plan = singleTypeUcPlanList.get(position);

        //操作list，用posInPlanList（而不是position）标记位置
        int posInPlanList = dataManager.getPlanLocationInPlanList(plan.getPlanCode());

        dataManager.notifyPlanStatusChanged(posInPlanList);

        //通知HomePresenter（更新侧栏header）
        EventBus.getDefault().post(new UcPlanCountChangedEvent());

        //通知AllPlansPresenter（更新计划列表）、AllTypesPresenter（更新类型列表）与本Presenter更新界面
        EventBus.getDefault().post(new PlanDetailChangedEvent(plan.getPlanCode(), posInPlanList, false, true, position));
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

        //刚才发生变化的计划在singleTypeUcPlanList中的位置
        int position;

        //NOTE: planCode和posInStUcPlanList中，有且仅有一个存在
        if (event.getPosInStUcPlanList() == -1) {
            //event中没有singleTypeUcPlanList中的位置信息，用planCode去获取
            position = getPosInSingleTypeUcPlanList(event.getPlanCode());
        } else {
            //event中有singleTypeUcPlanList中的位置信息，直接使用
            position = event.getPosInStUcPlanList();
        }

        if (event.isTypeOfPlanChanged() || event.isPlanStatusChanged()) {
            //说明有类型或完成情况的更新，需要从singleTypeUcPlanList中移除

            //更新此列表
            singleTypeUcPlanList.remove(position);
            planSingleTypeAdapter.notifyItemRemoved(position);

            //更新显示的未完成计划数量
            typeDetailView.onUcPlanCountChanged(getUcPlanCountStr(type.getTypeCode()));
        } else {
            //普通更新，需要刷新singleTypeUcPlanList
            planSingleTypeAdapter.notifyItemChanged(position);
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {

        //计算刚才删除的计划在这个list中的位置
        int position = getPosInSingleTypeUcPlanList(event.getPlanCode());

        //刷新list
        singleTypeUcPlanList.remove(position);
        planSingleTypeAdapter.notifyItemRemoved(position);

        if (!event.isCompleted()) {
            //说明刚才删除的是个未完成的计划，需要修改界面上的内容
            typeDetailView.onUcPlanCountChanged(getUcPlanCountStr(type.getTypeCode()));
        }
    }
}
