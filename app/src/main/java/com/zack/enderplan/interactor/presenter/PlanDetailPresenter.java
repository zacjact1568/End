package com.zack.enderplan.interactor.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.zack.enderplan.R;
import com.zack.enderplan.App;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.interactor.adapter.SimpleTypeAdapter;
import com.zack.enderplan.model.bean.FormattedPlan;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.domain.view.PlanDetailView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PlanDetailPresenter extends BasePresenter implements Presenter<PlanDetailView> {

    private static final int APP_BAR_STATE_EXPANDED = 1;
    private static final int APP_BAR_STATE_INTERMEDIATE = 0;
    private static final int APP_BAR_STATE_COLLAPSED = -1;

    private PlanDetailView planDetailView;
    private DataManager dataManager;
    private EventBus mEventBus;
    private int position;
    private Plan plan;
    private String mTitleCollapsedStr, dateTimeFormatStr, mUnsettledDscpt;
    private int mAppBarMaxRange, mContentCollapsedTextHeight;
    private float mLastHeaderAlpha = 1f;
    private int mAppBarState = APP_BAR_STATE_EXPANDED;

    public PlanDetailPresenter(PlanDetailView planDetailView, int position) {
        mEventBus = EventBus.getDefault();
        attachView(planDetailView);
        this.position = position;
        dataManager = DataManager.getInstance();
        plan = dataManager.getPlan(position);

        Context context = App.getGlobalContext();
        mTitleCollapsedStr = context.getString(R.string.title_activity_plan_detail);
        dateTimeFormatStr = context.getString(R.string.date_time_format);
        mUnsettledDscpt = context.getString(R.string.dscpt_unsettled);
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
                plan.getDeadline() != 0,
                formatDateTime(plan.getDeadline()),
                plan.getReminderTime() != 0,
                formatDateTime(plan.getReminderTime()),
                plan.isCompleted()
        ), new SimpleTypeAdapter(dataManager.getTypeList(), SimpleTypeAdapter.STYLE_SPINNER));
    }

    public void notifyMenuCreated() {
        planDetailView.updateStarMenuItem(plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED);
    }

    public void notifyPreDrawingAppBar(int appBarMaxRange) {
        mAppBarMaxRange = appBarMaxRange;
    }

    public void notifyPreDrawingContentCollapsedText(int contentCollapsedTextHeight) {
        mContentCollapsedTextHeight = contentCollapsedTextHeight;
        //借用这个回调方法，初始化ContentLayout的位置（向上平移ContentLayout）
        planDetailView.onAppBarScrolled(1, -mContentCollapsedTextHeight);
    }

    public void notifyContentEditingButtonClicked() {
        planDetailView.showContentEditorDialog(plan.getContent());
    }

    public void notifyPlanDeletionButtonClicked() {
        planDetailView.showPlanDeletionDialog(plan.getContent());
    }

    public void notifyAppBarScrolled(int offset) {

        if (mAppBarMaxRange == 0 || mContentCollapsedTextHeight == 0) return;

        int absOffset = Math.abs(offset);
        float headerAlpha = 1f - absOffset * 1.3f / mAppBarMaxRange;
        if (headerAlpha < 0) headerAlpha = 0;

        if ((headerAlpha == 0 || mLastHeaderAlpha == 0) && headerAlpha != mLastHeaderAlpha) {
            //刚退出透明状态或刚进入透明状态
            planDetailView.onAppBarScrolledToCriticalPoint(headerAlpha == 0 ? mTitleCollapsedStr : " ", headerAlpha == 0);
            mLastHeaderAlpha = headerAlpha;
        }

        //调整ContentLayout的透明度，下移ContentLayout，使ContentCollapsedLayout显示出来
        planDetailView.onAppBarScrolled(headerAlpha, (float) absOffset * mContentCollapsedTextHeight / mAppBarMaxRange - mContentCollapsedTextHeight);

        //更新AppBar状态
        if (absOffset == 0) {
            mAppBarState = APP_BAR_STATE_EXPANDED;
        } else if (absOffset == mAppBarMaxRange) {
            mAppBarState = APP_BAR_STATE_COLLAPSED;
        } else {
            mAppBarState = APP_BAR_STATE_INTERMEDIATE;
        }
    }

    public void notifyBackPressed() {
        if (mAppBarState == APP_BAR_STATE_EXPANDED) {
            planDetailView.pressBack();
        } else {
            planDetailView.backToTop();
        }
    }

    public void notifyTypeCodeChanged(int spinnerPos) {
        String oldTypeCode = plan.getTypeCode();
        String newTypeCode = dataManager.getType(spinnerPos).getTypeCode();
        if (!newTypeCode.equals(oldTypeCode)) {
            dataManager.notifyTypeOfPlanChanged(position, oldTypeCode, newTypeCode);
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN);
        }
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
            planDetailView.onContentChanged(newContent);
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_CONTENT);
        } else {
            //内容为空，不合法
            planDetailView.showToast(R.string.toast_empty_content);
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
            dataManager.notifyReminderTimeChanged(position, 0);
            planDetailView.onReminderTimeChanged(false, mUnsettledDscpt);
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

    public void notifySettingDeadline() {
        planDetailView.showDeadlinePickerDialog(plan.getDeadline());
    }

    public void notifySettingReminder() {
        planDetailView.showReminderTimePickerDialog(plan.getReminderTime());
    }

    public void notifyDeadlineChanged(long newDeadline) {
        if (plan.getDeadline() == newDeadline) return;
        planDetailView.onDeadlineChanged(newDeadline != 0, formatDateTime(newDeadline));
        dataManager.notifyDeadlineChanged(position, newDeadline);
        postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_DEADLINE);
    }

    public void notifyReminderTimeChanged(long newReminderTime) {
        if (plan.getReminderTime() == newReminderTime) return;
        planDetailView.onReminderTimeChanged(newReminderTime != 0, formatDateTime(newReminderTime));
        dataManager.notifyReminderTimeChanged(position, newReminderTime);
        postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_REMINDER_TIME);
    }

    private void postPlanDetailChangedEvent(int changedField) {
        mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), plan.getPlanCode(), position, changedField));
    }

    private String formatDateTime(long timeInMillis) {
        if (timeInMillis == 0) {
            return mUnsettledDscpt;
        } else {
            return DateFormat.format(dateTimeFormatStr, timeInMillis).toString();
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (plan.getPlanCode().equals(event.getPlanCode()) && !event.getEventSource().equals(getPresenterName())) {
            //此计划有内容的改变，且事件来自其他组件
            switch (event.getChangedField()) {
                case PlanDetailChangedEvent.FIELD_CONTENT:
                    planDetailView.onContentChanged(plan.getContent());
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
                    planDetailView.onDeadlineChanged(plan.getDeadline() != 0, formatDateTime(plan.getDeadline()));
                    break;
                case PlanDetailChangedEvent.FIELD_STAR_STATUS:
                    planDetailView.onStarStatusChanged(plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED);
                    break;
                case PlanDetailChangedEvent.FIELD_REMINDER_TIME:
                    planDetailView.onReminderTimeChanged(plan.getReminderTime() != 0, formatDateTime(plan.getReminderTime()));
                    break;
            }
        }
    }
}
