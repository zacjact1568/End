package me.imzack.app.end.presenter

import android.graphics.Color
import android.text.TextUtils
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.event.PlanDeletedEvent
import me.imzack.app.end.event.PlanDetailChangedEvent
import me.imzack.app.end.event.TypeCreatedEvent
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.model.bean.FormattedPlan
import me.imzack.app.end.model.bean.FormattedType
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.StringUtil
import me.imzack.app.end.util.TimeUtil
import me.imzack.app.end.view.contract.PlanDetailViewContract
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class PlanDetailPresenter @Inject constructor(
        private var mPlanDetailViewContract: PlanDetailViewContract?,
        private var mPlanListPosition: Int,
        private val mEventBus: EventBus
) : BasePresenter() {

    private val mPlan = DataManager.getPlan(mPlanListPosition)
    private var mTypeListPosition = DataManager.getTypeLocationInTypeList(mPlan.typeCode)
    private val mFormattedType = FormattedType()
    private var mAppBarMaxRange: Int = 0
    private var mLastHeaderAlpha = 1f
    private var mAppBarState = Constant.APP_BAR_STATE_EXPANDED

    override fun attach() {
        mEventBus.register(this)

        updateFormattedType()

        mPlanDetailViewContract!!.showInitialView(FormattedPlan(
                mPlan.content,
                mPlan.isStarred,
                DataManager.getTypeLocationInTypeList(mPlan.typeCode),
                formatDateTime(mPlan.deadline),
                formatDateTime(mPlan.reminderTime),
                mPlan.isCompleted
        ), mFormattedType)
    }

    override fun detach() {
        mPlanDetailViewContract = null
        mEventBus.unregister(this)
    }

    fun notifyPreDrawingAppBar(appBarMaxRange: Int) {
        mAppBarMaxRange = appBarMaxRange
    }

    fun notifyContentEditingButtonClicked() {
        mPlanDetailViewContract!!.showContentEditorDialog(mPlan.content)
    }

    fun notifyPlanDeletionButtonClicked() {
        mPlanDetailViewContract!!.showPlanDeletionDialog(mPlan.content)
    }

    fun notifyAppBarScrolled(offset: Int) {

        if (mAppBarMaxRange == 0) return

        val absOffset = Math.abs(offset)
        var headerAlpha = 1f - absOffset * 1.3f / mAppBarMaxRange
        if (headerAlpha < 0) headerAlpha = 0f

        if ((headerAlpha == 0f || mLastHeaderAlpha == 0f) && headerAlpha != mLastHeaderAlpha) {
            //刚退出透明状态或刚进入透明状态
            mPlanDetailViewContract!!.onAppBarScrolledToCriticalPoint(if (headerAlpha == 0f) ResourceUtil.getString(R.string.title_activity_plan_detail) else " ")
            mLastHeaderAlpha = headerAlpha
        }

        //调整HeaderLayout的透明度
        mPlanDetailViewContract!!.onAppBarScrolled(headerAlpha)

        //更新AppBar状态
        mAppBarState = when (absOffset) {
            0 -> Constant.APP_BAR_STATE_EXPANDED
            mAppBarMaxRange -> Constant.APP_BAR_STATE_COLLAPSED
            else -> Constant.APP_BAR_STATE_INTERMEDIATE
        }
    }

    fun notifyBackPressed() {
        if (mAppBarState == Constant.APP_BAR_STATE_EXPANDED) {
            mPlanDetailViewContract!!.pressBack()
        } else {
            mPlanDetailViewContract!!.backToTop()
        }
    }

    fun notifyTypeOfPlanChanged(typeListPos: Int) {
        val oldTypeCode = mPlan.typeCode
        val newTypeCode = DataManager.getType(typeListPos).code
        if (newTypeCode != oldTypeCode) {
            DataManager.notifyTypeOfPlanChanged(mPlanListPosition, oldTypeCode, newTypeCode)
            mTypeListPosition = typeListPos
            updateFormattedType()
            mPlanDetailViewContract!!.onTypeOfPlanChanged(mFormattedType)
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN)
        }
    }

    fun notifyPlanDeleted() {
        DataManager.notifyPlanDeleted(mPlanListPosition)
        mEventBus.post(PlanDeletedEvent(presenterName, mPlan.code, mPlanListPosition, mPlan))
        mPlanDetailViewContract!!.exit()
    }

    fun notifyContentChanged(newContent: String) {
        if (!TextUtils.isEmpty(newContent)) {
            //内容不为空，合法
            DataManager.notifyPlanContentChanged(mPlanListPosition, newContent)
            mPlanDetailViewContract!!.onContentChanged(newContent)
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_CONTENT)
        } else {
            //内容为空，不合法
            mPlanDetailViewContract!!.showToast(R.string.toast_empty_content)
        }
    }

    fun notifyStarStatusChanged() {
        DataManager.notifyStarStatusChanged(mPlanListPosition)
        mPlanDetailViewContract!!.onStarStatusChanged(mPlan.isStarred)
        postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_STAR_STATUS)
    }

    fun notifyPlanStatusChanged() {
        //首先检测此计划是否有提醒
        if (mPlan.hasReminder) {
            DataManager.notifyReminderTimeChanged(mPlanListPosition, 0L)
            mPlanDetailViewContract!!.onReminderTimeChanged(ResourceUtil.getString(R.string.dscpt_unsettled))
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_REMINDER_TIME)
        }
        DataManager.notifyPlanStatusChanged(mPlanListPosition)
        //更新位置
        mPlanListPosition = if (mPlan.isCompleted) DataManager.ucPlanCount else 0
        //刷新界面
        mPlanDetailViewContract!!.onPlanStatusChanged(mPlan.isCompleted)
        //发出事件
        postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_PLAN_STATUS)
    }

    fun notifySettingTypeOfPlan() {
        mPlanDetailViewContract!!.showTypePickerDialog(mTypeListPosition)
    }

    fun notifySettingDeadline() {
        mPlanDetailViewContract!!.showDeadlinePickerDialog(TimeUtil.getDateTimePickerDefaultTime(mPlan.deadline))
    }

    fun notifySettingReminder() {
        mPlanDetailViewContract!!.showReminderTimePickerDialog(TimeUtil.getDateTimePickerDefaultTime(mPlan.reminderTime))
    }

    fun notifyDeadlineChanged(deadline: Long) {
        if (mPlan.deadline == deadline) return
        DataManager.notifyDeadlineChanged(mPlanListPosition, deadline)
        mPlanDetailViewContract!!.onDeadlineChanged(formatDateTime(deadline))
        postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_DEADLINE)
    }

    fun notifyReminderTimeChanged(reminderTime: Long) {
        if (mPlan.reminderTime == reminderTime) return
        if (TimeUtil.isValidTime(reminderTime)) {
            DataManager.notifyReminderTimeChanged(mPlanListPosition, reminderTime)
            mPlanDetailViewContract!!.onReminderTimeChanged(formatDateTime(reminderTime))
            postPlanDetailChangedEvent(PlanDetailChangedEvent.FIELD_REMINDER_TIME)
        } else {
            mPlanDetailViewContract!!.showToast(R.string.toast_past_reminder_time)
        }
    }

    /** NOTE: 需要在更新mTypeListPosition后，调用此方法更新mFormattedType  */
    private fun updateFormattedType() {
        val type = DataManager.getType(mTypeListPosition)
        mFormattedType.typeMarkColorInt = Color.parseColor(type.markColor)
        mFormattedType.hasTypeMarkPattern = type.hasMarkPattern
        mFormattedType.typeMarkPatternResId = if (type.hasMarkPattern) ResourceUtil.getDrawableResourceId(type.markPattern!!) else 0
        mFormattedType.typeName = type.name
        mFormattedType.firstChar = StringUtil.getFirstChar(type.name)
    }

    private fun postPlanDetailChangedEvent(changedField: Int) {
        mEventBus.post(PlanDetailChangedEvent(presenterName, mPlan.code, mPlanListPosition, changedField))
    }

    private fun formatDateTime(timeInMillis: Long): CharSequence {
        val time = TimeUtil.formatDateTime(timeInMillis)
        return when {
            time == null -> ResourceUtil.getString(R.string.dscpt_touch_to_set)
            TimeUtil.isFutureTime(timeInMillis) -> time
            else -> StringUtil.addSpan(time, StringUtil.SPAN_STRIKETHROUGH)
        }
    }

    @Subscribe
    fun onPlanDetailChanged(event: PlanDetailChangedEvent) {
        if (mPlan.code == event.planCode && event.eventSource != presenterName) {
            //此计划有内容的改变，且事件来自其他组件
            when (event.changedField) {
                PlanDetailChangedEvent.FIELD_CONTENT -> mPlanDetailViewContract!!.onContentChanged(mPlan.content)
                PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN -> {
                    mTypeListPosition = DataManager.getTypeLocationInTypeList(mPlan.typeCode)
                    updateFormattedType()
                    mPlanDetailViewContract!!.onTypeOfPlanChanged(mFormattedType)
                }
                PlanDetailChangedEvent.FIELD_PLAN_STATUS -> {
                    //更新界面
                    mPlanDetailViewContract!!.onPlanStatusChanged(mPlan.isCompleted)
                    //更新position
                    mPlanListPosition = event.position
                }
                PlanDetailChangedEvent.FIELD_DEADLINE -> mPlanDetailViewContract!!.onDeadlineChanged(formatDateTime(mPlan.deadline))
                PlanDetailChangedEvent.FIELD_STAR_STATUS -> mPlanDetailViewContract!!.onStarStatusChanged(mPlan.isStarred)
                PlanDetailChangedEvent.FIELD_REMINDER_TIME -> mPlanDetailViewContract!!.onReminderTimeChanged(formatDateTime(mPlan.reminderTime))
            }
        }
    }

    @Subscribe
    fun onTypeCreated(event: TypeCreatedEvent) {
        //TODO 判断是从TypePickerDialog里进入创建类型的才执行以下语句（暂时不需要）
        notifyTypeOfPlanChanged(event.position)
    }
}
