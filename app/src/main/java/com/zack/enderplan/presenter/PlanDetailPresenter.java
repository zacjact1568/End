package com.zack.enderplan.presenter;

import android.graphics.Color;
import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.StringUtil;
import com.zack.enderplan.util.TimeUtil;
import com.zack.enderplan.event.PlanDeletedEvent;
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
    private int mTypeListPosition;
    private FormattedType mFormattedType;
    private int mAppBarMaxRange;
    private float mLastHeaderAlpha = 1f;
    private int mAppBarState = APP_BAR_STATE_EXPANDED;

    @Inject
    PlanDetailPresenter(PlanDetailViewContract planDetailViewContract, int planListPosition, DataManager dataManager, EventBus eventBus) {
        mPlanDetailViewContract = planDetailViewContract;
        mPlanListPosition = planListPosition;
        mDataManager = dataManager;
        mEventBus = eventBus;

        mPlan = mDataManager.getPlan(mPlanListPosition);

        mFormattedType = new FormattedType();
        mTypeListPosition = mDataManager.getTypeLocationInTypeList(mPlan.getTypeCode());
        updateFormattedType();
    }

    @Override
    public void attach() {
        mEventBus.register(this);
        mPlanDetailViewContract.showInitialView(new FormattedPlan(
                mPlan.getContent(),
                mPlan.isStarred(),
                mDataManager.getTypeLocationInTypeList(mPlan.getTypeCode()),
                formatDateTime(mPlan.getDeadline()),
                formatDateTime(mPlan.getReminderTime()),
                mPlan.isCompleted()
        ), mFormattedType);
    }

    @Override
    public void detach() {
        mPlanDetailViewContract = null;
        mEventBus.unregister(this);
    }

    public void notifyPreDrawingAppBar(int appBarMaxRange) {
        mAppBarMaxRange = appBarMaxRange;
    }

    public void notifyContentEditingButtonClicked() {
        mPlanDetailViewContract.showContentEditorDialog(mPlan.getContent());
    }

    public void notifyPlanDeletionButtonClicked() {
        mPlanDetailViewContract.showPlanDeletionDialog(mPlan.getContent());
    }

    public void notifyAppBarScrolled(int offset) {

        if (mAppBarMaxRange == 0) return;

        int absOffset = Math.abs(offset);
        float headerAlpha = 1f - absOffset * 1.3f / mAppBarMaxRange;
        if (headerAlpha < 0) headerAlpha = 0;

        if ((headerAlpha == 0 || mLastHeaderAlpha == 0) && headerAlpha != mLastHeaderAlpha) {
            //刚退出透明状态或刚进入透明状态
            mPlanDetailViewContract.onAppBarScrolledToCriticalPoint(headerAlpha == 0 ? ResourceUtil.getString(R.string.title_activity_plan_detail) : " ");
            mLastHeaderAlpha = headerAlpha;
        }

        //调整HeaderLayout的透明度
        mPlanDetailViewContract.onAppBarScrolled(headerAlpha);

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

    public void notifyTypeOfPlanChanged(int typeListPos) {
        String oldTypeCode = mPlan.getTypeCode();
        String newTypeCode = mDataManager.getType(typeListPos).getTypeCode();
        if (!newTypeCode.equals(oldTypeCode)) {
            mDataManager.notifyTypeOfPlanChanged(mPlanListPosition, oldTypeCode, newTypeCode);
            mTypeListPosition = typeListPos;
            updateFormattedType();
            mPlanDetailViewContract.onTypeOfPlanChanged(mFormattedType);
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
            mPlanDetailViewContract.onReminderTimeChanged(ResourceUtil.getString(R.string.dscpt_unsettled));
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

    public void notifySettingTypeOfPlan() {
        mPlanDetailViewContract.showTypePickerDialog(mTypeListPosition);
    }

    public void notifySettingDeadline() {
        mPlanDetailViewContract.showDeadlinePickerDialog(TimeUtil.getDateTimePickerDefaultTime(mPlan.getDeadline()));
    }

    public void notifySettingReminder() {
        mPlanDetailViewContract.showReminderTimePickerDialog(TimeUtil.getDateTimePickerDefaultTime(mPlan.getReminderTime()));
    }

    public void notifyDeadlineChanged(long deadline) {
        if (mPlan.getDeadline() == deadline) return;
        mDataManager.notifyDeadlineChanged(mPlanListPosition, deadline);
        mPlanDetailViewContract.onDeadlineChanged(formatDateTime(deadline));
        postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_DEADLINE);
    }

    public void notifyReminderTimeChanged(long reminderTime) {
        if (mPlan.getReminderTime() == reminderTime) return;
        if (TimeUtil.isValidTime(reminderTime)) {
            mDataManager.notifyReminderTimeChanged(mPlanListPosition, reminderTime);
            mPlanDetailViewContract.onReminderTimeChanged(formatDateTime(reminderTime));
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_REMINDER_TIME);
        } else {
            mPlanDetailViewContract.showToast(R.string.toast_past_reminder_time);
        }
    }

    /** NOTE: 需要在更新mTypeListPosition后，调用此方法更新mFormattedType */
    private void updateFormattedType() {
        Type type = mDataManager.getType(mTypeListPosition);
        mFormattedType.setTypeMarkColorInt(Color.parseColor(type.getTypeMarkColor()));
        mFormattedType.setHasTypeMarkPattern(type.hasTypeMarkPattern());
        mFormattedType.setTypeMarkPatternResId(ResourceUtil.getDrawableResourceId(type.getTypeMarkPattern()));
        mFormattedType.setTypeName(type.getTypeName());
        mFormattedType.setFirstChar(StringUtil.getFirstChar(type.getTypeName()));
    }

    private void postPlanDetailChangedEvent(int changedField) {
        mEventBus.post(new PlanDetailChangedEvent(getPresenterName(), mPlan.getPlanCode(), mPlanListPosition, changedField));
    }

    private CharSequence formatDateTime(long timeInMillis) {
        String time = TimeUtil.formatTime(timeInMillis);
        CharSequence formatted;
        if (time == null) {
            formatted = ResourceUtil.getString(R.string.dscpt_touch_to_set);
        } else if (TimeUtil.isFutureTime(timeInMillis)) {
            formatted = time;
        } else {
            formatted = StringUtil.addSpan(time, StringUtil.SPAN_STRIKETHROUGH);
        }
        return formatted;
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
                    mTypeListPosition = mDataManager.getTypeLocationInTypeList(mPlan.getTypeCode());
                    updateFormattedType();
                    mPlanDetailViewContract.onTypeOfPlanChanged(mFormattedType);
                    break;
                case PlanDetailChangedEvent.FIELD_PLAN_STATUS:
                    //更新界面
                    mPlanDetailViewContract.onPlanStatusChanged(mPlan.isCompleted());
                    //更新position
                    mPlanListPosition = event.getPosition();
                    break;
                case PlanDetailChangedEvent.FIELD_DEADLINE:
                    mPlanDetailViewContract.onDeadlineChanged(formatDateTime(mPlan.getDeadline()));
                    break;
                case PlanDetailChangedEvent.FIELD_STAR_STATUS:
                    mPlanDetailViewContract.onStarStatusChanged(mPlan.isStarred());
                    break;
                case PlanDetailChangedEvent.FIELD_REMINDER_TIME:
                    mPlanDetailViewContract.onReminderTimeChanged(formatDateTime(mPlan.getReminderTime()));
                    break;
            }
        }
    }

    @Subscribe
    public void onTypeCreated(TypeCreatedEvent event) {
        //TODO 判断是从TypePickerDialog里进入创建类型的才执行以下语句（暂时不需要）
        notifyTypeOfPlanChanged(event.getPosition());
    }
}
