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
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.PlanStatusChangedEvent;
import com.zack.enderplan.event.RemindedEvent;
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.manager.DataManager;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.util.LogUtil;
import com.zack.enderplan.view.PlanDetailView;
import com.zack.enderplan.widget.TypeSpinnerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PlanDetailPresenter implements Presenter<PlanDetailView> {

    private static final String LOG_TAG = "PlanDetailPresenter";

    private PlanDetailView planDetailView;
    private DataManager dataManager;
    private int position;
    private Plan plan;
    private EnderPlanDB enderplanDB;
    //private ContentValues contentValues;
    private ReminderManager reminderManager;
    private String dateFormatStr, dateTimeFormatStr;
    private String makePlanCStr, makePlanUcStr;
    private boolean isPlanDetailChanged, isPlanStatusChanged;

    public PlanDetailPresenter(PlanDetailView planDetailView, int position) {
        attachView(planDetailView);
        this.position = position;
        dataManager = DataManager.getInstance();
        plan = dataManager.getPlan(position);
        enderplanDB = EnderPlanDB.getInstance();
        reminderManager = ReminderManager.getInstance();

        Context context = EnderPlanApp.getGlobalContext();
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

    /*public void syncWithDatabase() {
        if (contentValues != null && contentValues.size() != 0) {
            enderplanDB.editPlan(plan.getPlanCode(), contentValues);
            contentValues.clear();
        }
    }*/

    /*private ContentValues getContentValues() {
        if (contentValues == null) {
            contentValues = new ContentValues();
        }
        return contentValues;
    }*/

    /*private ReminderManager getReminderManager() {
        if (reminderManager == null) {
            reminderManager = new ReminderManager();
        }
        return reminderManager;
    }*/

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
        isPlanDetailChanged = true;
        enderplanDB.editTypeOfPlan(plan.getPlanCode(), newTypeCode);
        //getContentValues().put("type_code", newTypeCode);
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
            //EventBus.getDefault().post(new UcPlanCountChangedEvent());
        }
        if (plan.getReminderTime() != 0) {
            //说明该计划有提醒，需要将提醒取消
            reminderManager.cancelAlarm(plan.getPlanCode());
        }
        dataManager.removeFromPlanList(position);
        enderplanDB.deletePlan(plan.getPlanCode());

        /*if (contentValues != null) {
            contentValues.clear();
        }*/
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
            enderplanDB.editContent(plan.getPlanCode(), newContent);
            //getContentValues().put("content", newContent);
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
        enderplanDB.editStarStatus(plan.getPlanCode(), newStarStatus);
        //getContentValues().put("star_status", newStarStatus);
        planDetailView.onStarStatusChanged(!isStarred);
    }

    public void notifyPlanStatusChanged() {

        ContentValues values = new ContentValues();

        //旧的完成状态
        boolean isCompletedPast = plan.getCompletionTime() != 0;
        //更新Maps
        dataManager.updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), isCompletedPast ? 1 : -1);
        dataManager.updateUcPlanCount(isCompletedPast ? 1 : -1);
        //EventBus.getDefault().post(new UcPlanCountChangedEvent());

        if (plan.getReminderTime() != 0) {
            //有设置提醒，需要移除
            reminderManager.cancelAlarm(plan.getPlanCode());
            plan.setReminderTime(0);
            isPlanDetailChanged = true;
            //enderplanDB.editReminderTime(plan.getPlanCode(), 0);
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

        //通知AllPlansPresenter（更新计划列表）与AllTypesPresenter（更新类型列表）
        //EventBus.getDefault().post(new PlanStatusChangedEvent());

        //设立标志
        isPlanStatusChanged = true;

        //数据库存储
        values.put("creation_time", newCreationTime);
        values.put("completion_time", newCompletionTime);
        enderplanDB.editPlan(plan.getPlanCode(), values);

        //更新界面（NOTE：新的完成状态是旧的完成状态取反）
        planDetailView.onPlanStatusChanged(isCompletedPast ? makePlanCStr : makePlanUcStr);
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
        if (isPlanDetailChanged || isPlanStatusChanged) {
            //说明计划详情有改变
            planDetailView.onActivityFinished(position, plan.getPlanCode(), isPlanDetailChanged, isPlanStatusChanged);
        }
    }

    public void notifyDeadlineChanged(long newDeadline) {
        planDetailView.onDeadlineSelected(plan.getDeadline() == 0, DateFormat.format(dateFormatStr, newDeadline).toString());
        plan.setDeadline(newDeadline);
        //TODO 暂时不用通知更新界面，因为没有其他组件的界面显示有这个属性
        enderplanDB.editDeadline(plan.getPlanCode(), newDeadline);
        //getContentValues().put("deadline", newDeadline);
    }

    public void notifyDeadlineRemoved() {
        if (plan.getDeadline() != 0) {
            plan.setDeadline(0);
            //TODO 暂时不用 ...
            enderplanDB.editDeadline(plan.getPlanCode(), 0);
            //getContentValues().put("deadline", 0);
            planDetailView.onDeadlineRemoved();
        }
    }

    public void notifyReminderTimeChanged(long newReminderTime) {
        reminderManager.setAlarm(plan.getPlanCode(), newReminderTime);
        planDetailView.onReminderTimeSelected(plan.getReminderTime() == 0, DateFormat.format(dateTimeFormatStr, newReminderTime).toString());
        plan.setReminderTime(newReminderTime);
        isPlanDetailChanged = true;
        enderplanDB.editReminderTime(plan.getPlanCode(), newReminderTime);
        //getContentValues().put("reminder_time", newReminderTime);
    }

    public void notifyReminderRemoved() {
        if (plan.getReminderTime() != 0) {
            reminderManager.cancelAlarm(plan.getPlanCode());
            plan.setReminderTime(0);
            isPlanDetailChanged = true;
            enderplanDB.editReminderTime(plan.getPlanCode(), 0);
            //getContentValues().put("reminder_time", 0);
            planDetailView.onReminderRemoved();
        }
    }

    /*public void notifyReminderOff(String planCode) {

    }*/

    @Subscribe
    public void onReminded(RemindedEvent event) {
        //这里用position来识别可能不准确
        if (position == event.position) {
            //是当前计划的提醒
            planDetailView.onReminderRemoved();

            //plan.setReminderTime(0)这一句其实放在这里也可以，只是不知道AllPlansPresenter中的订阅和这个订阅谁先执行
            //如果plan.setReminderTime(0)这一句放在这里，且AllPlansPresenter中的订阅先执行，那么AllPlansList将得不到刷新

            /*plan.setReminderTime(0);
            isPlanDetailChanged = true;*/
            /*if (getContentValues().containsKey("reminder_time")) {
                getContentValues().remove("reminder_time");
            }*/
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (position == event.position) {
            //是当前计划的详细信息改变了（目前只可能是提醒时间）TODO 后续加入判断
            //修改显示的提醒时间
            planDetailView.onReminderTimeSelected(
                    true,
                    DateFormat.format(dateTimeFormatStr, plan.getReminderTime()).toString()
            );
        }
    }

    @Subscribe
    public void onPlanStatusChanged(PlanStatusChangedEvent event) {
        if (position == event.position) {
            //是当前计划的完成情况改变了

            //更新position
            position = dataManager.getUcPlanCount();

            //修改改变完成情况的按钮文本
            planDetailView.onPlanStatusChanged(makePlanUcStr);
        }
    }

    /*private void postPlanDetailChangedEvent() {
        EventBus.getDefault().post(new PlanDetailChangedEvent(position));
    }

    private void postPlanStatusChangedEvent() {
        EventBus.getDefault().post(new PlanStatusChangedEvent());
    }*/
}
