package com.zack.enderplan.interactor.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.fragment.CalendarDialogFragment;
import com.zack.enderplan.domain.fragment.DateTimePickerDialogFragment;
import com.zack.enderplan.application.App;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.database.DatabaseDispatcher;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.RemindedEvent;
import com.zack.enderplan.model.ram.DataManager;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.domain.view.PlanDetailView;
import com.zack.enderplan.interactor.adapter.TypeSpinnerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PlanDetailPresenter implements Presenter<PlanDetailView> {

    private static final String LOG_TAG = "PlanDetailPresenter";

    private PlanDetailView planDetailView;
    private DataManager dataManager;
    private int position;
    private Plan plan;
    private DatabaseDispatcher databaseDispatcher;
    private ReminderManager reminderManager;
    private String dateFormatStr, dateTimeFormatStr;
    private String makePlanCStr, makePlanUcStr;
    private boolean isPlanDetailChanged, isTypeOfPlanChanged, isPlanStatusChanged;

    public PlanDetailPresenter(PlanDetailView planDetailView, int position) {
        attachView(planDetailView);
        this.position = position;
        dataManager = DataManager.getInstance();
        plan = dataManager.getPlan(position);
        databaseDispatcher = DatabaseDispatcher.getInstance();
        reminderManager = ReminderManager.getInstance();

        Context context = App.getGlobalContext();
        dateFormatStr = context.getResources().getString(R.string.date_format);
        dateTimeFormatStr = context.getResources().getString(R.string.date_time_format);
        makePlanCStr = context.getResources().getString(R.string.text_make_plan_c);
        makePlanUcStr = context.getResources().getString(R.string.text_make_plan_uc);
    }

    @Override
    public void attachView(PlanDetailView view) {
        planDetailView = view;
        EventBus.getDefault().register(this);
    }

    @Override
    public void detachView() {
        planDetailView = null;
        EventBus.getDefault().unregister(this);
    }

    public void setInitialView() {
        planDetailView.showInitialView(
                plan.getContent(),
                plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED,
                new TypeSpinnerAdapter(dataManager.getTypeList(), dataManager.getTypeMarkAndColorResMap()),
                dataManager.getTypeLocationInTypeList(plan.getTypeCode()),
                plan.getDeadline() != 0,
                DateFormat.format(dateFormatStr, plan.getDeadline()).toString(),
                plan.getReminderTime() != 0,
                DateFormat.format(dateTimeFormatStr, plan.getReminderTime()).toString(),
                plan.getCompletionTime() != 0,
                plan.getCompletionTime() == 0 ? makePlanCStr : makePlanUcStr
        );
    }

    public void notifyTypeCodeChanged(int posInSpinner) {
        //此时typeCode还没改变
        String oldTypeCode = plan.getTypeCode();
        String newTypeCode = dataManager.getType(posInSpinner).getTypeCode();
        if (plan.getCompletionTime() == 0) {
            //说明此计划还未完成，把此Uc计划的类型改变反映到UcMap
            dataManager.updateUcPlanCountOfEachTypeMap(oldTypeCode, newTypeCode);
        }
        //再来改变typeCode
        plan.setTypeCode(newTypeCode);
        isTypeOfPlanChanged = true;
        databaseDispatcher.editTypeOfPlan(plan.getPlanCode(), newTypeCode);
    }

    public void notifyPlanDeletion() {
        planDetailView.showPlanDeletionAlertDialog(plan.getContent());
    }

    public void notifyPlanDeleted() {

        boolean isCompleted = plan.getCompletionTime() != 0;

        if (!isCompleted) {
            //说明该计划还未完成
            dataManager.updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), -1);
            dataManager.updateUcPlanCount(-1);
        }
        if (plan.getReminderTime() != 0) {
            //说明该计划有提醒，需要将提醒取消
            reminderManager.cancelAlarm(plan.getPlanCode());
        }
        dataManager.removeFromPlanList(position);
        databaseDispatcher.deletePlan(plan.getPlanCode());

        planDetailView.onPlanDeleted(position, plan.getPlanCode(), plan.getContent(), isCompleted);
    }

    public void notifyContentEdit() {
        planDetailView.showContentEditDialog(plan.getContent());
    }

    public void notifyContentEdited(String newContent) {
        if (!TextUtils.isEmpty(newContent)) {
            //内容不为空，合法
            plan.setContent(newContent);
            isPlanDetailChanged = true;
            databaseDispatcher.editContent(plan.getPlanCode(), newContent);
            planDetailView.onContentEditSuccess(newContent);
        } else {
            //内容为空，不合法
            planDetailView.onContentEditFailed();
        }
    }

    public void notifyStarStatusChanged() {
        boolean isStarred = plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED;
        int newStarStatus = isStarred ? Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED;
        plan.setStarStatus(newStarStatus);
        isPlanDetailChanged = true;
        databaseDispatcher.editStarStatus(plan.getPlanCode(), newStarStatus);
        planDetailView.onStarStatusChanged(!isStarred);
    }

    public void notifyPlanStatusChanged() {

        ContentValues values = new ContentValues();

        //旧的完成状态
        boolean isCompletedPast = plan.getCompletionTime() != 0;
        //更新Maps
        dataManager.updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), isCompletedPast ? 1 : -1);
        dataManager.updateUcPlanCount(isCompletedPast ? 1 : -1);

        if (plan.getReminderTime() != 0) {
            //有设置提醒，需要移除
            reminderManager.cancelAlarm(plan.getPlanCode());
            plan.setReminderTime(0);
            isPlanDetailChanged = true;
            values.put("reminder_time", 0);
            planDetailView.onReminderRemoved();
        }

        //操作list
        dataManager.removeFromPlanList(position);

        long currentTimeMillis = System.currentTimeMillis();
        long newCreationTime = isCompletedPast ? currentTimeMillis : 0;
        long newCompletionTime = isCompletedPast ? 0 : currentTimeMillis;

        plan.setCreationTime(newCreationTime);
        plan.setCompletionTime(newCompletionTime);

        int newPosition = isCompletedPast ? 0 : dataManager.getUcPlanCount();
        dataManager.addToPlanList(newPosition, plan);

        //更新position
        position = newPosition;

        //设立标志
        isPlanStatusChanged = true;

        //数据库存储
        values.put("creation_time", newCreationTime);
        values.put("completion_time", newCompletionTime);
        databaseDispatcher.editPlan(plan.getPlanCode(), values);

        //更新界面（NOTE：新的完成状态是旧的完成状态取反）
        planDetailView.onPlanStatusChanged(!isCompletedPast, isCompletedPast ? makePlanCStr : makePlanUcStr);
    }

    public void createDeadlineDialog() {
        CalendarDialogFragment deadlineDialog = CalendarDialogFragment.newInstance(plan.getDeadline());
        planDetailView.onCreateDeadlineDialog(deadlineDialog);
    }

    public void createReminderDialog() {
        DateTimePickerDialogFragment reminderDialog = DateTimePickerDialogFragment.newInstance(plan.getReminderTime());
        planDetailView.onCreateReminderDialog(reminderDialog);
    }

    /** 非删除计划的普通退出 (Setting results are required) */
    public void notifyActivityFinished() {
        if (isPlanDetailChanged || isTypeOfPlanChanged || isPlanStatusChanged) {
            //说明计划详情有改变

            Intent intent = new Intent();
            intent.putExtra("position", position);
            intent.putExtra("plan_code", plan.getPlanCode());

            if (isTypeOfPlanChanged) {
                intent.putExtra("is_type_of_plan_changed", true);
            }
            if (isPlanStatusChanged) {
                intent.putExtra("is_plan_status_changed", true);
            }

            planDetailView.onActivityFinished(intent);
        }
    }

    public void notifyDeadlineChanged(long newDeadline) {
        planDetailView.onDeadlineSelected(plan.getDeadline() == 0, DateFormat.format(dateFormatStr, newDeadline).toString());
        plan.setDeadline(newDeadline);
        //TODO 暂时不用通知更新界面，因为没有其他组件的界面显示有这个属性
        databaseDispatcher.editDeadline(plan.getPlanCode(), newDeadline);
    }

    public void notifyDeadlineRemoved() {
        if (plan.getDeadline() != 0) {
            plan.setDeadline(0);
            //TODO 暂时不用 ...
            databaseDispatcher.editDeadline(plan.getPlanCode(), 0);
            planDetailView.onDeadlineRemoved();
        }
    }

    public void notifyReminderTimeChanged(long newReminderTime) {
        reminderManager.setAlarm(plan.getPlanCode(), newReminderTime);
        planDetailView.onReminderTimeSelected(plan.getReminderTime() == 0, DateFormat.format(dateTimeFormatStr, newReminderTime).toString());
        plan.setReminderTime(newReminderTime);
        isPlanDetailChanged = true;
        databaseDispatcher.editReminderTime(plan.getPlanCode(), newReminderTime);
    }

    public void notifyReminderRemoved() {
        if (plan.getReminderTime() != 0) {
            reminderManager.cancelAlarm(plan.getPlanCode());
            plan.setReminderTime(0);
            isPlanDetailChanged = true;
            databaseDispatcher.editReminderTime(plan.getPlanCode(), 0);
            planDetailView.onReminderRemoved();
        }
    }

    @Subscribe
    public void onReminded(RemindedEvent event) {
        //这里用position来识别可能不准确
        if (position == event.position) {
            //是当前计划的提醒
            planDetailView.onReminderRemoved();

            //plan.setReminderTime(0)这一句其实放在这里也可以，只是不知道AllPlansPresenter中的订阅和这个订阅谁先执行
            //如果plan.setReminderTime(0)这一句放在这里，且AllPlansPresenter中的订阅先执行，那么AllPlansList将得不到刷新
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (position == event.position) {
            if (event.isPlanStatusChanged) {
                //说明完成情况也有改变（目前在这里提醒时间和完成情况不可能同时改变），且只有可能是未完成->完成

                //更新position
                position = dataManager.getUcPlanCount();

                //修改改变完成情况的按钮文本
                planDetailView.onPlanStatusChanged(true, makePlanUcStr);
            } else {
                //是当前计划的普通信息（目前只可能是提醒时间）或者完成情况改变了 TODO 后续加入判断

                //修改显示的提醒时间
                planDetailView.onReminderTimeSelected(
                        true,
                        DateFormat.format(dateTimeFormatStr, plan.getReminderTime()).toString()
                );
            }
        }
    }
}
