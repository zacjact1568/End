package com.zack.enderplan.presenter;

import android.graphics.Color;
import android.text.TextUtils;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.StringUtil;
import com.zack.enderplan.util.TimeUtil;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.adapter.TypeGalleryAdapter;
import com.zack.enderplan.view.contract.PlanCreationViewContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class PlanCreationPresenter extends BasePresenter {

    private PlanCreationViewContract mPlanCreationViewContract;
    private DataManager mDataManager;
    private Plan mPlan;
    private EventBus mEventBus;
    private TypeGalleryAdapter mTypeGalleryAdapter;
    private FormattedType mFormattedType;

    @Inject
    PlanCreationPresenter(PlanCreationViewContract planCreationViewContract, Plan plan, DataManager dataManager, EventBus eventBus) {
        mPlanCreationViewContract = planCreationViewContract;
        mPlan = plan;
        mDataManager = dataManager;
        mEventBus = eventBus;

        //默认选中第一个
        int typeListPos = 0;

        mTypeGalleryAdapter = new TypeGalleryAdapter(mDataManager, typeListPos);
        mTypeGalleryAdapter.setOnItemClickListener(new TypeGalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                notifyTypeOfPlanChanged(position);
            }
        });
        mTypeGalleryAdapter.setOnFooterClickListener(new TypeGalleryAdapter.OnFooterClickListener() {
            @Override
            public void onFooterClick() {
                mPlanCreationViewContract.onTypeCreationItemClicked();
            }
        });

        mFormattedType = new FormattedType();
        updateFormattedType(mDataManager.getType(typeListPos));
    }

    @Override
    public void attach() {
        mEventBus.register(this);
        mPlanCreationViewContract.showInitialView(mTypeGalleryAdapter, mFormattedType);
    }

    @Override
    public void detach() {
        mPlanCreationViewContract = null;
        mEventBus.unregister(this);
    }

    public void notifyContentChanged(String content) {
        mPlan.setContent(content);
        mPlanCreationViewContract.onContentChanged(!TextUtils.isEmpty(content));
    }

    public void notifyTypeOfPlanChanged(int typeListPos) {
        Type type = mDataManager.getType(typeListPos);
        mPlan.setTypeCode(type.getTypeCode());
        updateFormattedType(type);
        mPlanCreationViewContract.onTypeOfPlanChanged(mFormattedType);
    }

    public void notifyDeadlineChanged(long deadline) {
        if (mPlan.getDeadline() == deadline) return;
        mPlan.setDeadline(deadline);
        mPlanCreationViewContract.onDeadlineChanged(formatDateTime(deadline));
    }

    public void notifyReminderTimeChanged(long reminderTime) {
        if (mPlan.getReminderTime() == reminderTime) return;
        if (TimeUtil.isValidTime(reminderTime)) {
            mPlan.setReminderTime(reminderTime);
            mPlanCreationViewContract.onReminderTimeChanged(formatDateTime(reminderTime));
        } else {
            mPlanCreationViewContract.showToast(R.string.toast_past_reminder_time);
        }
    }

    public void notifyStarStatusChanged() {
        mPlan.invertStarStatus();
        mPlanCreationViewContract.onStarStatusChanged(mPlan.isStarred());
    }

    //TODO 以后都用这种形式，即notifySetting***，更换控件就不用改方法名了
    public void notifySettingDeadline() {
        mPlanCreationViewContract.showDeadlinePickerDialog(TimeUtil.getDateTimePickerDefaultTime(mPlan.getDeadline()));
    }

    public void notifySettingReminder() {
        mPlanCreationViewContract.showReminderTimePickerDialog(TimeUtil.getDateTimePickerDefaultTime(mPlan.getReminderTime()));
    }

    public void notifyCreatingPlan() {
        if (TextUtils.isEmpty(mPlan.getContent())) {
            mPlanCreationViewContract.showToast(R.string.toast_empty_content);
        } else if (!TimeUtil.isValidTime(mPlan.getReminderTime())) {
            mPlanCreationViewContract.showToast(R.string.toast_past_reminder_time);
            notifyReminderTimeChanged(Constant.UNDEFINED_TIME);
        } else {
            mPlan.setCreationTime(System.currentTimeMillis());
            mDataManager.notifyPlanCreated(mPlan);
            EventBus.getDefault().post(new PlanCreatedEvent(
                    getPresenterName(),
                    mPlan.getPlanCode(),
                    mDataManager.getRecentlyCreatedPlanLocation()
            ));
            mPlanCreationViewContract.exit();
        }
    }

    public void notifyPlanCreationCanceled() {
        //TODO 判断是否已编辑过
        mPlanCreationViewContract.exit();
    }

    private void updateFormattedType(Type type) {
        mFormattedType.setTypeMarkColorInt(Color.parseColor(type.getTypeMarkColor()));
        mFormattedType.setHasTypeMarkPattern(type.hasTypeMarkPattern());
        mFormattedType.setTypeMarkPatternResId(ResourceUtil.getDrawableResourceId(type.getTypeMarkPattern()));
        mFormattedType.setTypeName(type.getTypeName());
        mFormattedType.setFirstChar(StringUtil.getFirstChar(type.getTypeName()));
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
    public void onTypeCreated(TypeCreatedEvent event) {
        int position = mDataManager.getRecentlyCreatedTypeLocation();
        mTypeGalleryAdapter.notifyItemInsertedAndNeedingSelection(position);
        notifyTypeOfPlanChanged(position);
    }
}
