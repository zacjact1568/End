package com.zack.enderplan.interactor.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.fragment.CalendarDialogFragment;
import com.zack.enderplan.domain.fragment.DateTimePickerDialogFragment;
import com.zack.enderplan.App;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.RemindedEvent;
import com.zack.enderplan.model.DataManager;
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
    private String dateFormatStr, dateTimeFormatStr;
    private String makePlanCStr, makePlanUcStr;
    private boolean isPlanDetailChanged, isTypeOfPlanChanged, isPlanStatusChanged;

    public PlanDetailPresenter(PlanDetailView planDetailView, int position) {
        attachView(planDetailView);
        this.position = position;
        dataManager = DataManager.getInstance();
        plan = dataManager.getPlan(position);

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
        dataManager.notifyTypeOfPlanChanged(position, plan.getTypeCode(), dataManager.getType(posInSpinner).getTypeCode());
        isTypeOfPlanChanged = true;
    }

    public void notifyPlanDeletion() {
        planDetailView.showPlanDeletionAlertDialog(plan.getContent());
    }

    public void notifyPlanDeleted() {
        dataManager.notifyPlanDeleted(position);
        planDetailView.onPlanDeleted(position, plan.getPlanCode(), plan.getContent(), plan.getCompletionTime() != 0);
    }

    public void notifyContentEdit() {
        planDetailView.showContentEditDialog(plan.getContent());
    }

    public void notifyContentEdited(String newContent) {
        if (!TextUtils.isEmpty(newContent)) {
            //内容不为空，合法
            dataManager.notifyPlanContentChanged(position, newContent);
            isPlanDetailChanged = true;
            planDetailView.onContentEditSuccess(newContent);
        } else {
            //内容为空，不合法
            planDetailView.onContentEditFailed();
        }
    }

    public void notifyStarStatusChanged() {
        dataManager.notifyStarStatusChanged(position);
        isPlanDetailChanged = true;
        planDetailView.onStarStatusChanged(plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED);
    }

    public void notifyPlanStatusChanged() {

        if (plan.getReminderTime() != 0) {
            isPlanDetailChanged = true;
            planDetailView.onReminderRemoved();
        }

        dataManager.notifyPlanStatusChanged(position);

        //注意：这是新的完成状态
        boolean isCompleted = plan.getCompletionTime() != 0;

        //更新position
        position = isCompleted ? dataManager.getUcPlanCount() : 0;

        //设立标志
        isPlanStatusChanged = true;

        //更新界面
        planDetailView.onPlanStatusChanged(isCompleted, isCompleted ? makePlanUcStr : makePlanCStr);
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

        dataManager.notifyDeadlineChanged(position, newDeadline);

        //TODO 暂时不用通知更新界面，因为没有其他组件的界面显示有这个属性
    }

    public void notifyDeadlineRemoved() {
        if (plan.getDeadline() != 0) {

            dataManager.notifyDeadlineChanged(position, 0);

            //TODO 暂时不用通知更新界面，因为没有其他组件的界面显示有这个属性
            planDetailView.onDeadlineRemoved();
        }
    }

    public void notifyReminderTimeChanged(long newReminderTime) {

        dataManager.notifyReminderTimeChanged(position, newReminderTime);

        planDetailView.onReminderTimeSelected(plan.getReminderTime() == 0, DateFormat.format(dateTimeFormatStr, newReminderTime).toString());
        isPlanDetailChanged = true;
    }

    public void notifyReminderRemoved() {
        if (plan.getReminderTime() != 0) {

            dataManager.notifyReminderTimeChanged(position, 0);

            isPlanDetailChanged = true;
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
        if (position == event.getPosition()) {
            if (event.isPlanStatusChanged()) {
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
