package com.zack.enderplan.presenter;

import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.TimeUtil;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.view.adapter.SimpleTypeAdapter;
import com.zack.enderplan.model.bean.FormattedPlan;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.contract.PlanDetailViewContract;
import com.zack.enderplan.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class PlanDetailPresenter extends BasePresenter {

    private static final int APP_BAR_STATE_EXPANDED = 1;
    private static final int APP_BAR_STATE_INTERMEDIATE = 0;
    private static final int APP_BAR_STATE_COLLAPSED = -1;

    private PlanDetailViewContract mPlanDetailViewContract;
    private DataManager mDataManager;
    private EventBus mEventBus;
    private int mPlanListPosition;
    private Plan mPlan;
    private int mAppBarMaxRange, mContentCollapsedTextHeight;
    private float mLastHeaderAlpha = 1f;
    private int mAppBarState = APP_BAR_STATE_EXPANDED;

    @Inject
    public PlanDetailPresenter(PlanDetailViewContract planDetailViewContract, int planListPosition, DataManager dataManager, EventBus eventBus) {
        mPlanDetailViewContract = planDetailViewContract;
        mPlanListPosition = planListPosition;
        mDataManager = dataManager;
        mEventBus = eventBus;

        mPlan = mDataManager.getPlan(mPlanListPosition);
    }

    @Override
    public void attach() {
        mEventBus.register(this);
        mPlanDetailViewContract.showInitialView(new FormattedPlan(
                mPlan.getContent(),
                mPlan.isStarred(),
                mDataManager.getTypeLocationInTypeList(mPlan.getTypeCode()),
                mPlan.hasDeadline(),
                formatDateTime(mPlan.getDeadline()),
                mPlan.hasReminder(),
                formatDateTime(mPlan.getReminderTime()),
                mPlan.isCompleted()
        ), new SimpleTypeAdapter(mDataManager.getTypeList(), SimpleTypeAdapter.STYLE_SPINNER));
    }

    @Override
    public void detach() {
        mPlanDetailViewContract = null;
        mEventBus.unregister(this);
    }

    public void notifyMenuCreated() {
        mPlanDetailViewContract.updateStarMenuItem(mPlan.isStarred());
    }

    public void notifyPreDrawingAppBar(int appBarMaxRange) {
        mAppBarMaxRange = appBarMaxRange;
    }

    public void notifyPreDrawingContentCollapsedText(int contentCollapsedTextHeight) {
        mContentCollapsedTextHeight = contentCollapsedTextHeight;
        //借用这个回调方法，初始化ContentLayout的位置（向上平移ContentLayout）
        mPlanDetailViewContract.onAppBarScrolled(1, -mContentCollapsedTextHeight);
    }

    public void notifyContentEditingButtonClicked() {
        mPlanDetailViewContract.showContentEditorDialog(mPlan.getContent());
    }

    public void notifyPlanDeletionButtonClicked() {
        mPlanDetailViewContract.showPlanDeletionDialog(mPlan.getContent());
    }

    public void notifyAppBarScrolled(int offset) {

        if (mAppBarMaxRange == 0 || mContentCollapsedTextHeight == 0) return;

        int absOffset = Math.abs(offset);
        float headerAlpha = 1f - absOffset * 1.3f / mAppBarMaxRange;
        if (headerAlpha < 0) headerAlpha = 0;

        if ((headerAlpha == 0 || mLastHeaderAlpha == 0) && headerAlpha != mLastHeaderAlpha) {
            //刚退出透明状态或刚进入透明状态
            mPlanDetailViewContract.onAppBarScrolledToCriticalPoint(headerAlpha == 0 ? ResourceUtil.getString(R.string.title_activity_plan_detail) : " ", headerAlpha == 0);
            mLastHeaderAlpha = headerAlpha;
        }

        //调整ContentLayout的透明度，下移ContentLayout，使ContentCollapsedLayout显示出来
        mPlanDetailViewContract.onAppBarScrolled(headerAlpha, (float) absOffset * mContentCollapsedTextHeight / mAppBarMaxRange - mContentCollapsedTextHeight);

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
            mPlanDetailViewContract.pressBack();
        } else {
            mPlanDetailViewContract.backToTop();
        }
    }

    public void notifyTypeCodeChanged(int spinnerPos) {
        String oldTypeCode = mPlan.getTypeCode();
        String newTypeCode = mDataManager.getType(spinnerPos).getTypeCode();
        if (!newTypeCode.equals(oldTypeCode)) {
            mDataManager.notifyTypeOfPlanChanged(mPlanListPosition, oldTypeCode, newTypeCode);
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN);
        }
    }

    public void notifyPlanDeleted() {
        mDataManager.notifyPlanDeleted(mPlanListPosition);
        mEventBus.post(new PlanDeletedEvent(getPresenterName(), mPlan.getPlanCode(), mPlanListPosition, mPlan));
        mPlanDetailViewContract.exit();
    }

    public void notifyContentChanged(String newContent) {
        if (!TextUtils.isEmpty(newContent)) {
            //内容不为空，合法
            mDataManager.notifyPlanContentChanged(mPlanListPosition, newContent);
            mPlanDetailViewContract.onContentChanged(newContent);
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_CONTENT);
        } else {
            //内容为空，不合法
            mPlanDetailViewContract.showToast(R.string.toast_empty_content);
        }
    }

    public void notifyStarStatusChanged() {
        mDataManager.notifyStarStatusChanged(mPlanListPosition);
        mPlanDetailViewContract.onStarStatusChanged(mPlan.isStarred());
        postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_STAR_STATUS);
    }

    public void notifyPlanStatusChanged() {
        //首先检测此计划是否有提醒
        if (mPlan.hasReminder()) {
            mDataManager.notifyReminderTimeChanged(mPlanListPosition, Constant.UNDEFINED_TIME);
            mPlanDetailViewContract.onReminderTimeChanged(false, ResourceUtil.getString(R.string.dscpt_unsettled));
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_REMINDER_TIME);
        }
        mDataManager.notifyPlanStatusChanged(mPlanListPosition);
        //更新位置
        mPlanListPosition = mPlan.isCompleted() ? mDataManager.getUcPlanCount() : 0;
        //刷新界面
        mPlanDetailViewContract.onPlanStatusChanged(mPlan.isCompleted());
        //发出事件
        postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_PLAN_STATUS);
    }

    public void notifySettingDeadline() {
        mPlanDetailViewContract.showDeadlinePickerDialog(TimeUtil.getDefaultDateTimePickerTime(mPlan.getDeadline()));
    }

    public void notifySettingReminder() {
        mPlanDetailViewContract.showReminderTimePickerDialog(TimeUtil.getDefaultDateTimePickerTime(mPlan.getReminderTime()));
    }

    public void notifyDeadlineChanged(long deadline) {
        if (mPlan.getDeadline() == deadline) return;
        if (TimeUtil.isValidDateTimePickerTime(deadline)) {
            mDataManager.notifyDeadlineChanged(mPlanListPosition, deadline);
            mPlanDetailViewContract.onDeadlineChanged(mPlan.hasDeadline(), formatDateTime(deadline));
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_DEADLINE);
        } else {
            mPlanDetailViewContract.showToast(R.string.toast_past_deadline);
        }
    }

    public void notifyReminderTimeChanged(long reminderTime) {
        if (mPlan.getReminderTime() == reminderTime) return;
        if (TimeUtil.isValidDateTimePickerTime(reminderTime)) {
            mDataManager.notifyReminderTimeChanged(mPlanListPosition, reminderTime);
            mPlanDetailViewContract.onReminderTimeChanged(mPlan.hasReminder(), formatDateTime(reminderTime));
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_REMINDER_TIME);
        } else {
            mPlanDetailViewContract.showToast(R.string.toast_past_reminder_time);
        }
    }

    private void postPlanDetailChangedEvent(int changedField) {
        mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), mPlan.getPlanCode(), mPlanListPosition, changedField));
    }

    private String formatDateTime(long timeInMillis) {
        String time = TimeUtil.formatTime(timeInMillis);
        return time != null ? time : ResourceUtil.getString(R.string.dscpt_unsettled);
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (mPlan.getPlanCode().equals(event.getPlanCode()) && !event.getEventSource().equals(getPresenterName())) {
            //此计划有内容的改变，且事件来自其他组件
            switch (event.getChangedField()) {
                case PlanDetailChangedEvent.FIELD_CONTENT:
                    mPlanDetailViewContract.onContentChanged(mPlan.getContent());
                    break;
                case PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN:
                    mPlanDetailViewContract.onTypeOfPlanChanged(mDataManager.getTypeLocationInTypeList(mPlan.getTypeCode()));
                    break;
                case PlanDetailChangedEvent.FIELD_PLAN_STATUS:
                    //更新界面
                    mPlanDetailViewContract.onPlanStatusChanged(mPlan.isCompleted());
                    //更新position
                    mPlanListPosition = event.getPosition();
                    break;
                case PlanDetailChangedEvent.FIELD_DEADLINE:
                    mPlanDetailViewContract.onDeadlineChanged(mPlan.hasDeadline(), formatDateTime(mPlan.getDeadline()));
                    break;
                case PlanDetailChangedEvent.FIELD_STAR_STATUS:
                    mPlanDetailViewContract.onStarStatusChanged(mPlan.isStarred());
                    break;
                case PlanDetailChangedEvent.FIELD_REMINDER_TIME:
                    mPlanDetailViewContract.onReminderTimeChanged(mPlan.hasReminder(), formatDateTime(mPlan.getReminderTime()));
                    break;
            }
        }
    }
}
