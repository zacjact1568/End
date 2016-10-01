package com.zack.enderplan.interactor.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.zack.enderplan.R;
import com.zack.enderplan.App;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.model.bean.FormattedPlan;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.RemindedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.domain.view.PlanDetailView;
import com.zack.enderplan.interactor.adapter.TypeSpinnerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PlanDetailPresenter extends BasePresenter implements Presenter<PlanDetailView> {

    private PlanDetailView planDetailView;
    private DataManager dataManager;
    private EventBus mEventBus;
    private int position;
    private Plan plan;
    private String dateFormatStr, dateTimeFormatStr;

    public PlanDetailPresenter(PlanDetailView planDetailView, int position) {
        mEventBus = EventBus.getDefault();
        attachView(planDetailView);
        this.position = position;
        dataManager = DataManager.getInstance();
        plan = dataManager.getPlan(position);

        Context context = App.getGlobalContext();
        dateFormatStr = context.getResources().getString(R.string.date_format);
        dateTimeFormatStr = context.getResources().getString(R.string.date_time_format);
    }

    @Override
    public void attachView(PlanDetailView view) {
        planDetailView = view;
        mEventBus.register(this);
    }

    @Override
    public void detachView() {
        planDetailView = null;
        mEventBus.unregister(this);
    }

    public void setInitialView() {
        planDetailView.showInitialView(new FormattedPlan(
                plan.getContent(),
                plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED,
                dataManager.getTypeLocationInTypeList(plan.getTypeCode()),
                DateFormat.format(dateFormatStr, plan.getDeadline()).toString(),
                DateFormat.format(dateTimeFormatStr, plan.getReminderTime()).toString(),
                plan.getCompletionTime() != 0
        ), new TypeSpinnerAdapter(dataManager.getTypeList(), dataManager.getTypeMarkAndColorResMap()));
    }

    public void notifyViewClicked(int viewId) {
        switch (viewId) {
            case R.id.text_content:
                planDetailView.showContentEditorDialog(plan.getContent());
                break;
            case R.id.item_view_deadline:
                planDetailView.showDeadlineDialog(plan.getDeadline());
                break;
            case R.id.item_view_reminder:
                planDetailView.showReminderTimeDialog(plan.getReminderTime());
                break;
            case R.id.fab:
                notifyStarStatusChanged();
                break;
            case R.id.btn_switch_plan_status:
                notifyPlanStatusChanged();
                break;
        }
    }

    public void notifyMenuItemSelected(int itemId) {
        switch (itemId) {
            case android.R.id.home:
                planDetailView.exitPlanDetail();
                break;
            case R.id.action_delete:
                planDetailView.showPlanDeletionDialog(plan.getContent());
                break;
        }
    }

    public void notifyTypeCodeChanged(int spinnerPos) {
        dataManager.notifyTypeOfPlanChanged(position, plan.getTypeCode(), dataManager.getType(spinnerPos).getTypeCode());
        postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN);
    }

    public void notifyPlanDeleted() {
        dataManager.notifyPlanDeleted(position);
        mEventBus.post(new PlanDeletedEvent(getPresenterName(), plan.getPlanCode(), position, plan));
        planDetailView.exitPlanDetail();
    }

    public void notifyContentChanged(String newContent) {
        if (!TextUtils.isEmpty(newContent)) {
            //内容不为空，合法
            dataManager.notifyPlanContentChanged(position, newContent);
            planDetailView.onContentEditedSuccessfully(newContent);
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_CONTENT);
        } else {
            //内容为空，不合法
            planDetailView.onContentEditedAbortively();
        }
    }

    public void notifyStarStatusChanged() {
        dataManager.notifyStarStatusChanged(position);
        planDetailView.onStarStatusChanged(plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED);
        postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_STAR_STATUS);
    }

    public void notifyPlanStatusChanged() {
        //首先检测此计划是否有提醒
        if (plan.getReminderTime() != 0) {
            planDetailView.onReminderRemoved();
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_REMINDER_TIME);
        }
        dataManager.notifyPlanStatusChanged(position);
        //更新位置
        position = plan.getCompletionTime() != 0 ? dataManager.getUcPlanCount() : 0;
        //刷新界面
        planDetailView.onPlanStatusChanged(plan.getCompletionTime() != 0);
        //发出事件
        postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_PLAN_STATUS);
    }

    public void notifyDeadlineChanged(long newDeadline) {
        if (plan.getDeadline() != newDeadline) {
            planDetailView.onDeadlineSelected(DateFormat.format(dateFormatStr, newDeadline).toString());
            dataManager.notifyDeadlineChanged(position, newDeadline);
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_DEADLINE);
        }
    }

    public void notifyDeadlineRemoved() {
        if (plan.getDeadline() != 0) {
            planDetailView.onDeadlineRemoved();
            dataManager.notifyDeadlineChanged(position, 0);
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_DEADLINE);
        }
    }

    public void notifyReminderTimeChanged(long newReminderTime) {
        if (plan.getReminderTime() != newReminderTime) {
            dataManager.notifyReminderTimeChanged(position, newReminderTime);
            planDetailView.onReminderTimeSelected(DateFormat.format(dateTimeFormatStr, newReminderTime).toString());
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_REMINDER_TIME);
        }
    }

    public void notifyReminderRemoved() {
        if (plan.getReminderTime() != 0) {
            dataManager.notifyReminderTimeChanged(position, 0);
            planDetailView.onReminderRemoved();
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_REMINDER_TIME);
        }
    }

    private void postPlanDetailChangedEvent(int changedField) {
        mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), plan.getPlanCode(), position, changedField));
    }

    @Subscribe
    public void onReminded(RemindedEvent event) {
        if (plan.getPlanCode().equals(event.getPlanCode())) {
            //是当前计划的提醒
            planDetailView.onReminderRemoved();

            //plan.setReminderTime(0)这一句其实放在这里也可以，只是不知道AllPlansPresenter中的订阅和这个订阅谁先执行
            //如果plan.setReminderTime(0)这一句放在这里，且AllPlansPresenter中的订阅先执行，那么AllPlansList将得不到刷新
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (plan.getPlanCode().equals(event.getPlanCode()) && !event.getEventSource().equals(getPresenterName())) {
            //此计划有内容的改变，且事件来自其他组件
            switch (event.getChangedField()) {
                case PlanDetailChangedEvent.FIELD_CONTENT:
                    planDetailView.onContentEditedSuccessfully(plan.getContent());
                    break;
                case PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN:
                    planDetailView.onTypeOfPlanChanged(dataManager.getTypeLocationInTypeList(plan.getTypeCode()));
                    break;
                case PlanDetailChangedEvent.FIELD_PLAN_STATUS:
                    //更新界面
                    planDetailView.onPlanStatusChanged(plan.getCompletionTime() != 0);
                    //更新position
                    position = event.getPosition();
                    break;
                case PlanDetailChangedEvent.FIELD_DEADLINE:
                    if (plan.getDeadline() != 0) {
                        planDetailView.onDeadlineSelected(DateFormat.format(dateFormatStr, plan.getDeadline()).toString());
                    } else {
                        planDetailView.onDeadlineRemoved();
                    }
                    break;
                case PlanDetailChangedEvent.FIELD_STAR_STATUS:
                    planDetailView.onStarStatusChanged(plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED);
                    break;
                case PlanDetailChangedEvent.FIELD_REMINDER_TIME:
                    if (plan.getReminderTime() != 0) {
                        planDetailView.onReminderTimeSelected(DateFormat.format(dateTimeFormatStr, plan.getReminderTime()).toString());
                    } else {
                        planDetailView.onReminderRemoved();
                    }
                    break;
            }
        }
    }
}
