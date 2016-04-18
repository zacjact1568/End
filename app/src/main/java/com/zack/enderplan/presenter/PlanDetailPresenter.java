package com.zack.enderplan.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.zack.enderplan.R;
import com.zack.enderplan.activity.CalendarDialogFragment;
import com.zack.enderplan.activity.DateTimePickerDialogFragment;
import com.zack.enderplan.application.EnderPlanApp;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.manager.DataManager;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.view.PlanDetailView;
import com.zack.enderplan.widget.TypeSpinnerAdapter;

import org.greenrobot.eventbus.EventBus;

public class PlanDetailPresenter implements Presenter<PlanDetailView> {

    private PlanDetailView planDetailView;
    private DataManager dataManager;
    private int position;
    private Plan plan;
    private EnderPlanDB enderplanDB;
    private ContentValues contentValues;
    private ReminderManager reminderManager;
    private String originalTypeCode;
    private String dateFormatStr;
    private String dateTimeFormatStr;

    public PlanDetailPresenter(PlanDetailView planDetailView, int position) {
        attachView(planDetailView);
        this.position = position;
        dataManager = DataManager.getInstance();
        plan = dataManager.getPlan(position);
        enderplanDB = EnderPlanDB.getInstance();
        originalTypeCode = plan.getTypeCode();

        Context context = EnderPlanApp.getGlobalContext();
        dateFormatStr = context.getResources().getString(R.string.date_format);
        dateTimeFormatStr = context.getResources().getString(R.string.date_time_format);
    }

    @Override
    public void attachView(PlanDetailView view) {
        planDetailView = view;
    }

    @Override
    public void detachView() {
        planDetailView = null;
    }

    public void syncWithDatabase() {
        if (contentValues != null && contentValues.size() != 0) {
            enderplanDB.editPlan(plan.getPlanCode(), contentValues);
            contentValues.clear();
        }
    }

    private ContentValues getContentValues() {
        if (contentValues == null) {
            contentValues = new ContentValues();
        }
        return contentValues;
    }

    private ReminderManager getReminderManager() {
        if (reminderManager == null) {
            reminderManager = new ReminderManager();
        }
        return reminderManager;
    }

    public void getInitialDataAndShow() {
        planDetailView.showInitialView(
                plan.getContent(),
                plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED,
                plan.getDeadline() != 0,
                DateFormat.format(dateFormatStr, plan.getDeadline()).toString(),
                plan.getReminderTime() != 0,
                DateFormat.format(dateTimeFormatStr, plan.getReminderTime()).toString()
        );
    }

    public void initSpinner() {
        planDetailView.showInitialSpinner(
                new TypeSpinnerAdapter(dataManager.getTypeList(), dataManager.getTypeMarkAndColorResMap()),
                dataManager.getTypeLocationInTypeList(plan.getTypeCode())
        );
    }

    public void notifyTypeCodeChanged(int posInSpinner) {
        String newTypeCode = dataManager.getType(posInSpinner).getTypeCode();
        plan.setTypeCode(newTypeCode);
        getContentValues().put("type_code", newTypeCode);
    }

    public void notifyPlanDeletion() {
        planDetailView.showPlanDeletionAlertDialog(plan.getContent());
    }

    public void notifyPlanDeleted() {
        if (plan.getCompletionTime() == 0) {
            //说明该计划还未完成
            dataManager.updateUcPlanCount(-1);
            EventBus.getDefault().post(new UcPlanCountChangedEvent());
        }
        if (plan.getReminderTime() != 0) {
            //说明该计划有提醒，需要将提醒取消
            getReminderManager().cancelAlarm(plan.getPlanCode());
        }
        dataManager.updatePlanCountOfEachType(plan.getTypeCode(), -1);
        dataManager.removeFromPlanList(position);
        enderplanDB.deletePlan(plan.getPlanCode());

        if (contentValues != null) {
            contentValues.clear();
        }
        planDetailView.onPlanDeleted(plan.getContent());
    }

    public void notifyContentEdit() {
        planDetailView.showContentEditDialog(plan.getContent());
    }

    public void notifyContentEdited(String newContent) {
        if (!TextUtils.isEmpty(newContent)) {
            plan.setContent(newContent);
            getContentValues().put("content", newContent);
            planDetailView.onContentEditSuccess(newContent);
        } else {
            planDetailView.onContentEditFailed();
        }
    }

    public void notifyStarStatusChanged() {
        boolean isStarred = plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED;
        int newStarStatus = isStarred ? Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED;
        plan.setStarStatus(newStarStatus);
        getContentValues().put("star_status", newStarStatus);
        planDetailView.onStarStatusChanged(!isStarred);
    }

    public void createDeadlineDialog() {
        CalendarDialogFragment deadlineDialog = CalendarDialogFragment.newInstance(plan.getDeadline());
        planDetailView.onCreateDeadlineDialog(deadlineDialog);
    }

    public void createReminderDialog() {
        DateTimePickerDialogFragment reminderDialog = DateTimePickerDialogFragment.newInstance(plan.getReminderTime());
        planDetailView.onCreateReminderDialog(reminderDialog);
    }

    public void notifyActivityFinished() {
        if (contentValues != null) {
            //dataManager.updatePlanInPlanList(position, plan);
            dataManager.updatePlanCountOfEachType(originalTypeCode, plan.getTypeCode());
            planDetailView.onActivityFinished();
        }
    }

    public void notifyDeadlineChanged(long newDeadline) {
        planDetailView.onDeadlineSelected(plan.getDeadline() == 0, DateFormat.format(dateFormatStr, newDeadline).toString());
        plan.setDeadline(newDeadline);
        getContentValues().put("deadline", newDeadline);
    }

    public void notifyDeadlineRemoved() {
        if (plan.getDeadline() != 0) {
            plan.setDeadline(0);
            getContentValues().put("deadline", 0);
            planDetailView.onDeadlineRemoved();
        }
    }

    public void notifyReminderTimeChanged(long newReminderTime) {
        getReminderManager().setAlarm(plan.getPlanCode(), newReminderTime);
        planDetailView.onReminderTimeSelected(plan.getReminderTime() == 0, DateFormat.format(dateTimeFormatStr, newReminderTime).toString());
        plan.setReminderTime(newReminderTime);
        getContentValues().put("reminder_time", newReminderTime);
    }

    public void notifyReminderRemoved() {
        if (plan.getReminderTime() != 0) {
            getReminderManager().cancelAlarm(plan.getPlanCode());
            plan.setReminderTime(0);
            getContentValues().put("reminder_time", 0);
            planDetailView.onReminderRemoved();
        }
    }

    public void notifyReminderOff(String planCode) {
        if (planCode.equals(plan.getPlanCode())) {
            //是当前计划的提醒
            plan.setReminderTime(0);
            if (getContentValues().containsKey("reminder_time")) {
                getContentValues().remove("reminder_time");
            }
            planDetailView.onReminderRemoved();
        }
    }
}
