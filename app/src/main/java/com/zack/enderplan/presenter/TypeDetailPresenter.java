package com.zack.enderplan.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.application.EnderPlanApp;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.event.PlanCompletedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.PlanItemClickedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.manager.DataManager;
import com.zack.enderplan.util.Util;
import com.zack.enderplan.view.TypeDetailView;
import com.zack.enderplan.widget.PlanSingleTypeAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class TypeDetailPresenter implements Presenter<TypeDetailView> {

    private TypeDetailView typeDetailView;
    private DataManager dataManager;
    private PlanSingleTypeAdapter planSingleTypeAdapter;
    private List<Plan> singleTypeUcPlanList;
    private Type type;
    private int planItemClickPosition;
    private String nonePlan;
    private String onePlan;
    private String multiPlan;

    public TypeDetailPresenter(TypeDetailView typeDetailView, int position) {
        attachView(typeDetailView);
        dataManager = DataManager.getInstance();

        type = dataManager.getType(position);

        singleTypeUcPlanList = dataManager.getSingleTypeUcPlanList(type.getTypeCode());
        planSingleTypeAdapter = new PlanSingleTypeAdapter(singleTypeUcPlanList);

        Context context = EnderPlanApp.getGlobalContext();
        nonePlan = context.getResources().getString(R.string.plan_count_of_each_type_none);
        onePlan = context.getResources().getString(R.string.plan_count_of_each_type_one);
        multiPlan = context.getResources().getString(R.string.plan_count_of_each_type_multi);
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
                getPlanCountStr(type.getTypeCode()),
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

            //存储至list、数据库
            dataManager.addToPlanList(0, plan);
            dataManager.updateUcPlanCount(1);
            dataManager.updatePlanCountOfEachType(type.getTypeCode(), 1);
            EnderPlanDB.getInstance().savePlan(plan);

            //更新此fragment中的数据
            singleTypeUcPlanList.add(0, plan);
            planSingleTypeAdapter.notifyItemInserted(0);
            typeDetailView.onPlanCreationSuccess(getPlanCountStr(type.getTypeCode()));

            //通知其他组件更新
            //通知AllPlansPresenter（更新计划列表）与AllTypesPresenter（更新类型列表）
            EventBus.getDefault().post(new PlanCreatedEvent());
            //通知HomePresenter（更新侧栏header）
            EventBus.getDefault().post(new UcPlanCountChangedEvent());
        }
    }

    public void notifyPlanItemClicked(int position) {
        planItemClickPosition = position;
        int posInPlanList = dataManager.getPlanLocationInPlanList(singleTypeUcPlanList.get(position).getPlanCode());
        EventBus.getDefault().post(new PlanItemClickedEvent(posInPlanList));
    }

    public void notifyPlanCompleted(int position) {
        int posInPlanList = dataManager.getPlanLocationInPlanList(singleTypeUcPlanList.get(position).getPlanCode());
        singleTypeUcPlanList.remove(position);
        planSingleTypeAdapter.notifyItemRemoved(position);
        EventBus.getDefault().post(new PlanCompletedEvent(posInPlanList));
    }

    //TODO 与TypeAdapter中的另一个合并
    private String getPlanCountStr(String typeCode) {
        Integer count = dataManager.getPlanCountOfEachTypeMap().get(typeCode);
        if (count == null) {
            count = 0;
        }
        switch (count) {
            case 0:
                return nonePlan;
            case 1:
                return onePlan;
            default:
                return String.format("%d " + multiPlan, count);
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        planSingleTypeAdapter.notifyItemChanged(planItemClickPosition);
    }
}
