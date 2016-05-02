package com.zack.enderplan.presenter;

import android.graphics.Color;

import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.view.ReminderView;

public class ReminderPresenter implements Presenter<ReminderView> {

    private ReminderView reminderView;
    private Plan plan;
    private EnderPlanDB enderplanDB;

    public ReminderPresenter(ReminderView reminderView, Plan plan) {
        attachView(reminderView);
        this.plan = plan;

        enderplanDB = EnderPlanDB.getInstance();
    }

    @Override
    public void attachView(ReminderView view) {
        reminderView = view;
    }

    @Override
    public void detachView() {
        reminderView = null;
    }

    public void setInitialView() {
        reminderView.showInitialView(
                plan.getContent(),
                Color.parseColor(enderplanDB.queryTypeMarkByTypeCode(plan.getTypeCode()))
        );
    }

    public void notifyReminderDelayed() {
        //TODO ...
        reminderView.onReminderDelayed("1300年13月13日13点13分");
    }

    public void notifyReminderCanceled() {
        reminderView.onReminderCanceled();
    }

    public void notifyPlanCompleted() {
        //TODO ...
        reminderView.onPlanCompleted(plan.getContent());
    }
}
