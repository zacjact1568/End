package net.zackzhang.app.end.presenter

import android.graphics.Color
import android.text.TextUtils
import net.zackzhang.app.end.R
import net.zackzhang.app.end.event.PlanCreatedEvent
import net.zackzhang.app.end.event.TypeCreatedEvent
import net.zackzhang.app.end.model.DataManager
import net.zackzhang.app.end.model.bean.FormattedType
import net.zackzhang.app.end.model.bean.Plan
import net.zackzhang.app.end.model.bean.Type
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.util.StringUtil
import net.zackzhang.app.end.util.TimeUtil
import net.zackzhang.app.end.view.adapter.TypeGalleryAdapter
import net.zackzhang.app.end.view.contract.PlanCreationViewContract
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class PlanCreationPresenter @Inject constructor(
        private var mPlanCreationViewContract: PlanCreationViewContract?,
        private val mEventBus: EventBus
) : BasePresenter() {

    private val mPlan = Plan(creationTime = 0L)
    private val mTypeGalleryAdapter = TypeGalleryAdapter(0)
    private val mFormattedType = FormattedType()

    override fun attach() {
        mEventBus.register(this)

        mTypeGalleryAdapter.mOnItemClickListener = { notifyTypeOfPlanChanged(it) }
        mTypeGalleryAdapter.mOnFooterClickListener = { mPlanCreationViewContract!!.onTypeCreationItemClicked() }

        updateFormattedType(DataManager.getType(0))

        mPlanCreationViewContract!!.showInitialView(mTypeGalleryAdapter, mFormattedType)
    }

    override fun detach() {
        mPlanCreationViewContract = null
        mEventBus.unregister(this)
    }

    fun notifyContentChanged(content: String) {
        mPlan.content = content
        mPlanCreationViewContract!!.onContentChanged(!TextUtils.isEmpty(content))
    }

    fun notifyTypeOfPlanChanged(typeListPos: Int) {
        val type = DataManager.getType(typeListPos)
        mPlan.typeCode = type.code
        updateFormattedType(type)
        mPlanCreationViewContract!!.onTypeOfPlanChanged(mFormattedType)
    }

    fun notifyDeadlineChanged(deadline: Long) {
        if (mPlan.deadline == deadline) return
        mPlan.deadline = deadline
        mPlanCreationViewContract!!.onDeadlineChanged(formatDateTime(deadline))
    }

    fun notifyReminderTimeChanged(reminderTime: Long) {
        if (mPlan.reminderTime == reminderTime) return
        if (TimeUtil.isValidTime(reminderTime)) {
            mPlan.reminderTime = reminderTime
            mPlanCreationViewContract!!.onReminderTimeChanged(formatDateTime(reminderTime))
        } else {
            mPlanCreationViewContract!!.showToast(R.string.toast_past_reminder_time)
        }
    }

    fun notifyStarStatusChanged() {
        mPlan.invertStarStatus()
        mPlanCreationViewContract!!.onStarStatusChanged(mPlan.isStarred)
    }

    //TODO 以后都用这种形式，即notifySetting***，更换控件就不用改方法名了
    fun notifySettingDeadline() {
        mPlanCreationViewContract!!.showDeadlinePickerDialog(TimeUtil.getDateTimePickerDefaultTime(mPlan.deadline))
    }

    fun notifySettingReminder() {
        mPlanCreationViewContract!!.showReminderTimePickerDialog(TimeUtil.getDateTimePickerDefaultTime(mPlan.reminderTime))
    }

    fun notifyCreatingPlan() {
        when {
            TextUtils.isEmpty(mPlan.content) -> mPlanCreationViewContract!!.showToast(R.string.toast_empty_content)
            !TimeUtil.isValidTime(mPlan.reminderTime) -> {
                mPlanCreationViewContract!!.showToast(R.string.toast_past_reminder_time)
                notifyReminderTimeChanged(0L)
            }
            else -> {
                mPlan.creationTime = System.currentTimeMillis()
                DataManager.notifyPlanCreated(mPlan)
                mEventBus.post(PlanCreatedEvent(
                        presenterName,
                        mPlan.code,
                        DataManager.recentlyCreatedPlanLocation
                ))
                mPlanCreationViewContract!!.exit()
            }
        }
    }

    fun notifyPlanCreationCanceled() {
        //TODO 判断是否已编辑过
        mPlanCreationViewContract!!.exit()
    }

    private fun updateFormattedType(type: Type) {
        mFormattedType.typeMarkColorInt = Color.parseColor(type.markColor)
        mFormattedType.hasTypeMarkPattern = type.hasMarkPattern
        mFormattedType.typeMarkPatternResId = if (type.hasMarkPattern) ResourceUtil.getDrawableResourceId(type.markPattern!!) else 0
        mFormattedType.typeName = type.name
        mFormattedType.firstChar = StringUtil.getFirstChar(type.name)
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
    fun onTypeCreated(event: TypeCreatedEvent) {
        val position = DataManager.recentlyCreatedTypeLocation
        mTypeGalleryAdapter.notifyItemInsertedAndNeedingSelection(position)
        notifyTypeOfPlanChanged(position)
    }
}
