package com.zack.enderplan.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.application.EnderPlanApp;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.manager.DataManager;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.util.Util;
import com.zack.enderplan.view.TypeDetailView;
import com.zack.enderplan.widget.PlanSingleTypeAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class TypeDetailPresenter implements Presenter<TypeDetailView> {

    private TypeDetailView typeDetailView;
    private DataManager dataManager;
    private EnderPlanDB enderplanDB;
    private PlanSingleTypeAdapter planSingleTypeAdapter;
    private List<Plan> singleTypeUcPlanList;
    private Type type;
    private ReminderManager reminderManager;
    //private int planItemClickPosition;
    private String noneUcPlan, oneUcPlan, multiUcPlan;

    public TypeDetailPresenter(TypeDetailView typeDetailView, int position) {
        attachView(typeDetailView);
        dataManager = DataManager.getInstance();
        enderplanDB = EnderPlanDB.getInstance();

        type = dataManager.getType(position);

        singleTypeUcPlanList = dataManager.getSingleTypeUcPlanList(type.getTypeCode());
        planSingleTypeAdapter = new PlanSingleTypeAdapter(singleTypeUcPlanList);

        Context context = EnderPlanApp.getGlobalContext();
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

            //存储至list
            dataManager.addToPlanList(0, plan);
            //通知AllPlansPresenter（更新计划列表）与AllTypesPresenter（更新类型列表）
            EventBus.getDefault().post(new PlanCreatedEvent());

            dataManager.updateUcPlanCount(1);
            dataManager.updateUcPlanCountOfEachTypeMap(type.getTypeCode(), 1);
            //通知HomePresenter（更新侧栏header）
            EventBus.getDefault().post(new UcPlanCountChangedEvent());

            //存储至数据库
            enderplanDB.savePlan(plan);

            //更新此fragment中的数据
            singleTypeUcPlanList.add(0, plan);
            planSingleTypeAdapter.notifyItemInserted(0);
            typeDetailView.onPlanCreationSuccess(getUcPlanCountStr(type.getTypeCode()));
        }
    }

    public void notifyPlanItemClicked(int position) {
        //planItemClickPosition = position;
        int posInPlanList = dataManager.getPlanLocationInPlanList(singleTypeUcPlanList.get(position).getPlanCode());
        typeDetailView.onPlanItemClicked(posInPlanList);
    }

    public void notifyPlanStarStatusChanged(int position) {
        //获取plan
        Plan plan = singleTypeUcPlanList.get(position);
        //获取原始状态，计算变化后的状态
        boolean isStarred = plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED;
        int newStarStatus = isStarred ? Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED;
        //更新plan
        plan.setStarStatus(newStarStatus);
        //更新界面
        planSingleTypeAdapter.notifyItemChanged(position);
        //通知AllPlans界面更新
        int posInPlanList = dataManager.getPlanLocationInPlanList(plan.getPlanCode());
        EventBus.getDefault().post(new PlanDetailChangedEvent(posInPlanList, plan.getPlanCode(), false, false, -1));
        //数据库存储
        ContentValues values = new ContentValues();
        values.put("star_status", newStarStatus);
        enderplanDB.editPlan(plan.getPlanCode(), values);
    }

    public void notifyPlanCompleted(int position) {

        ContentValues values = new ContentValues();

        Plan plan = singleTypeUcPlanList.get(position);

        //更新此列表
        /*singleTypeUcPlanList.remove(position);
        planSingleTypeAdapter.notifyItemRemoved(position);*/

        //更新Maps
        dataManager.updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), -1);
        dataManager.updateUcPlanCount(-1);

        //更新显示的未完成计划数量
        //typeDetailView.onUcPlanCountChanged(getPlanCountStr(type.getTypeCode()));

        //通知HomePresenter（更新侧栏header）
        EventBus.getDefault().post(new UcPlanCountChangedEvent());

        //操作list，用posInPlanList（而不是position）标记位置
        int posInPlanList = dataManager.getPlanLocationInPlanList(plan.getPlanCode());

        dataManager.removeFromPlanList(posInPlanList);

        //取消提醒
        if (plan.getReminderTime() != 0) {
            ReminderManager.getInstance().cancelAlarm(plan.getPlanCode());
            plan.setReminderTime(0);
            values.put(EnderPlanDB.DB_STR_REMINDER_TIME, 0);
            //removeReminder(plan.getPlanCode());
        }

        long newCompletionTime = System.currentTimeMillis();

        plan.setCreationTime(0);
        plan.setCompletionTime(newCompletionTime);

        dataManager.addToPlanList(dataManager.getUcPlanCount(), plan);

        //通知AllPlansPresenter（更新计划列表）、AllTypesPresenter（更新类型列表）与本Presenter更新界面
        EventBus.getDefault().post(new PlanDetailChangedEvent(posInPlanList, plan.getPlanCode(), false, true, position));

        //数据库存储
        values.put(EnderPlanDB.DB_STR_CREATION_TIME, 0);
        values.put(EnderPlanDB.DB_STR_COMPLETION_TIME, newCompletionTime);
        enderplanDB.editPlan(plan.getPlanCode(), values);
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
        if (event.posInStUcPlanList == -1) {
            //event中没有singleTypeUcPlanList中的位置信息，用planCode去获取
            position = getPosInSingleTypeUcPlanList(event.planCode);
        } else {
            //event中有singleTypeUcPlanList中的位置信息，直接使用
            position = event.posInStUcPlanList;
        }

        if (event.isTypeOfPlanChanged || event.isPlanStatusChanged) {
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
        int position = getPosInSingleTypeUcPlanList(event.planCode);

        //刷新list
        singleTypeUcPlanList.remove(position);
        planSingleTypeAdapter.notifyItemRemoved(position);

        if (!event.isCompleted) {
            //说明刚才删除的是个未完成的计划，需要修改界面上的内容
            typeDetailView.onUcPlanCountChanged(getUcPlanCountStr(type.getTypeCode()));
        }
    }
}
