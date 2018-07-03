package net.zackzhang.app.end.presenter

import android.graphics.Color
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import net.zackzhang.app.end.R
import net.zackzhang.app.end.common.Constant
import net.zackzhang.app.end.event.*
import net.zackzhang.app.end.model.DataManager
import net.zackzhang.app.end.model.bean.FormattedType
import net.zackzhang.app.end.model.bean.Plan
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.util.StringUtil
import net.zackzhang.app.end.util.SystemUtil
import net.zackzhang.app.end.view.adapter.SingleTypePlanListAdapter
import net.zackzhang.app.end.view.callback.BaseItemTouchCallback
import net.zackzhang.app.end.view.callback.SingleTypePlanListItemTouchCallback
import net.zackzhang.app.end.view.contract.TypeDetailViewContract
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import javax.inject.Inject

class TypeDetailPresenter @Inject constructor(
        private var mTypeDetailViewContract: TypeDetailViewContract?,
        private val mTypeListPosition: Int,
        private val mEventBus: EventBus
) : BasePresenter() {
    
    private val mType = DataManager.getType(mTypeListPosition)
    private val mSingleTypePlanList = DataManager.getSingleTypePlanList(mType.code)
    private val mSingleTypePlanListAdapter = SingleTypePlanListAdapter(mSingleTypePlanList)
    private var mAppBarMaxRange = 0
    private var mEditorLayoutHeight = 0
    private var mLastHeaderAlpha = 1f
    private var mAppBarState = Constant.APP_BAR_STATE_EXPANDED
    private var mViewVisible = true

    init {
        mSingleTypePlanListAdapter.mOnPlanItemClickListener = {
            mTypeDetailViewContract!!.onPlanItemClicked(DataManager.getPlanLocationInPlanList(mSingleTypePlanList[it].code))
        }
        mSingleTypePlanListAdapter.mOnStarStatusChangedListener = {
            val (code) = mSingleTypePlanList[it]
            val planListPos = DataManager.getPlanLocationInPlanList(code)
            DataManager.notifyStarStatusChanged(planListPos)
            mEventBus.post(PlanDetailChangedEvent(
                    presenterName,
                    code,
                    planListPos,
                    PlanDetailChangedEvent.FIELD_STAR_STATUS
            ))
        }
    }

    override fun attach() {
        mEventBus.register(this)

        val singleTypePlanListItemTouchCallback = SingleTypePlanListItemTouchCallback(mSingleTypePlanList)
        singleTypePlanListItemTouchCallback.mOnItemSwipedListener = { position, direction ->
            when (direction) {
                BaseItemTouchCallback.DIR_START -> notifyDeletingPlan(position)
                BaseItemTouchCallback.DIR_END -> notifySwitchingPlanStatus(position)
            }
        }
        singleTypePlanListItemTouchCallback.mOnItemMovedListener = { fromPosition, toPosition ->
            notifyPlanSequenceChanged(fromPosition, toPosition)
        }

        mTypeDetailViewContract!!.showInitialView(FormattedType(
                typeMarkColorInt = Color.parseColor(mType.markColor),
                hasTypeMarkPattern = mType.hasMarkPattern,
                typeMarkPatternResId = if (mType.hasMarkPattern) ResourceUtil.getDrawableResourceId(mType.markPattern!!) else 0,
                typeName = mType.name,
                firstChar = StringUtil.getFirstChar(mType.name)
        ), getUcPlanCountStr(mType.code), mSingleTypePlanListAdapter, ItemTouchHelper(singleTypePlanListItemTouchCallback))
    }

    override fun detach() {
        mTypeDetailViewContract = null
        mEventBus.unregister(this)
    }

    fun notifyPreDrawingAppBar(appBarMaxRange: Int) {
        mAppBarMaxRange = appBarMaxRange
    }

    fun notifyPreDrawingEditorLayout(editorLayoutHeight: Int) {
        mEditorLayoutHeight = editorLayoutHeight
    }

    fun notifySwitchingViewVisibility(isVisible: Boolean) {
        mViewVisible = isVisible
    }

    fun notifyAppBarScrolled(offset: Int) {

        if (mAppBarMaxRange == 0 || mEditorLayoutHeight == 0) return

        val absOffset = Math.abs(offset)
        var headerAlpha = 1f - absOffset * 1.3f / mAppBarMaxRange
        if (headerAlpha < 0) headerAlpha = 0f

        if ((headerAlpha == 0f || mLastHeaderAlpha == 0f) && headerAlpha != mLastHeaderAlpha) {
            //刚退出透明状态或刚进入透明状态（即临界点）
            mTypeDetailViewContract!!.onAppBarScrolledToCriticalPoint(
                    if (headerAlpha == 0f) mType.name else " ",
                    if (headerAlpha > 0) 0f else mEditorLayoutHeight.toFloat()
            )
            mLastHeaderAlpha = headerAlpha
        }

        mTypeDetailViewContract!!.onAppBarScrolled(headerAlpha)

        mAppBarState = when (absOffset) {
            0 -> Constant.APP_BAR_STATE_EXPANDED
            mAppBarMaxRange -> Constant.APP_BAR_STATE_COLLAPSED
            else -> Constant.APP_BAR_STATE_INTERMEDIATE
        }
    }

    fun notifyPlanListScrolled(top: Boolean, bottom: Boolean) {
        mSingleTypePlanListAdapter.notifyListScrolled(when {
            //触顶
            top -> Constant.SCROLL_EDGE_TOP
            //触底
            bottom -> Constant.SCROLL_EDGE_BOTTOM
            //中间
            else -> Constant.SCROLL_EDGE_MIDDLE
        })
    }

    fun notifyContentEditorTextChanged(text: String) {
        mTypeDetailViewContract!!.changeContentEditorClearTextIconVisibility(!TextUtils.isEmpty(text))
    }

    fun notifyBackPressed() {
        if (mAppBarState == Constant.APP_BAR_STATE_EXPANDED) {
            mTypeDetailViewContract!!.pressBack()
        } else {
            mTypeDetailViewContract!!.backToTop()
        }
    }

    fun notifyCreatingPlan(newPlan: Plan, position: Int, planListPos: Int) {
        //刷新全局列表
        DataManager.notifyPlanCreated(newPlan, planListPos)

        //刷新此界面
        mSingleTypePlanList.add(position, newPlan)
        mSingleTypePlanListAdapter.notifyItemInsertedAndChangingFooter(position)
        //下面这句一定要在「刷新全局列表」后
        mTypeDetailViewContract!!.onUcPlanCountChanged(getUcPlanCountStr(newPlan.typeCode))

        mEventBus.post(PlanCreatedEvent(presenterName, newPlan.code, planListPos))
    }

    fun notifyCreatingPlan(newContent: String) {
        if (TextUtils.isEmpty(newContent)) {
            //内容为空
            mTypeDetailViewContract!!.showToast(R.string.toast_create_plan_failed)
        } else {
            //创建新计划
            notifyCreatingPlan(Plan(content = newContent, typeCode = mType.code), 0, 0)

            mTypeDetailViewContract!!.showToast(R.string.toast_create_plan_success)
            mTypeDetailViewContract!!.onPlanCreated()
        }
    }

    fun notifyDeletingPlan(position: Int) {

        SystemUtil.makeShortVibrate()

        val plan = mSingleTypePlanList[position]
        val planListPos = DataManager.getPlanLocationInPlanList(plan.code)

        //刷新全局列表
        DataManager.notifyPlanDeleted(planListPos)

        //刷新此界面上的列表
        mSingleTypePlanList.removeAt(position)
        mSingleTypePlanListAdapter.notifyItemRemovedAndChangingFooter(position)
        mTypeDetailViewContract!!.onPlanDeleted(plan, position, planListPos, mViewVisible)
        //下面这句一定要在「刷新全局列表」后
        if (!plan.isCompleted) {
            //说明刚才删除的是个未完成的计划，需要修改界面上的内容
            mTypeDetailViewContract!!.onUcPlanCountChanged(getUcPlanCountStr(mType.code))
        }

        //刷新其他组件
        mEventBus.post(PlanDeletedEvent(presenterName, plan.code, planListPos, plan))
    }

    fun notifySwitchingPlanStatus(position: Int) {

        val plan = mSingleTypePlanList[position]
        val planListPos = DataManager.getPlanLocationInPlanList(plan.code)

        //检测是否设置了提醒
        if (plan.hasReminder) {
            DataManager.notifyReminderTimeChanged(planListPos, 0L)
            mEventBus.post(PlanDetailChangedEvent(presenterName, plan.code, planListPos, PlanDetailChangedEvent.FIELD_REMINDER_TIME))
        }
        //刷新全局列表
        DataManager.notifyPlanStatusChanged(planListPos)

        //刷新此界面上的列表
        mSingleTypePlanList.removeAt(position)
        mSingleTypePlanListAdapter.notifyItemRemoved(position)
        //下面这句一定要在「刷新全局列表」后
        val newPosition = if (plan.isCompleted) DataManager.getUcPlanCountOfOneType(plan.typeCode) else 0
        mSingleTypePlanList.add(newPosition, plan)
        mSingleTypePlanListAdapter.notifyItemInserted(newPosition)
        mTypeDetailViewContract!!.onUcPlanCountChanged(getUcPlanCountStr(mType.code))

        //发送事件，更新其他组件
        mEventBus.post(PlanDetailChangedEvent(
                presenterName,
                plan.code,
                if (plan.isCompleted) DataManager.ucPlanCount else 0,
                PlanDetailChangedEvent.FIELD_PLAN_STATUS
        ))
    }

    fun notifyPlanSequenceChanged(fromPosition: Int, toPosition: Int) {
        val fromPlanListPos = DataManager.getPlanLocationInPlanList(mSingleTypePlanList[fromPosition].code)
        val toPlanListPos = DataManager.getPlanLocationInPlanList(mSingleTypePlanList[toPosition].code)
        if (fromPlanListPos < DataManager.ucPlanCount != toPlanListPos < DataManager.ucPlanCount) return
        //更新全局列表
        DataManager.swapPlansInPlanList(fromPlanListPos, toPlanListPos)
        //更新此列表
        Collections.swap(mSingleTypePlanList, fromPosition, toPosition)
        mSingleTypePlanListAdapter.notifyItemMoved(fromPosition, toPosition)
    }

    fun notifyTypeEditingButtonClicked() {
        mTypeDetailViewContract!!.enterEditType(mTypeListPosition, mAppBarState == Constant.APP_BAR_STATE_EXPANDED)
    }

    fun notifyTypeDeletionButtonClicked(deletePlan: Boolean) {
        if (DataManager.typeCount == 1) {
            //如果只剩一个类型，禁止删除
            mTypeDetailViewContract!!.onDetectedDeletingLastType()
            return
        }
        if (!deletePlan && !DataManager.isTypeEmpty(mType.code)) {
            //如果不删除类型连带的计划，且检测到类型非空，弹移动计划到其他类型的对话框
            mTypeDetailViewContract!!.onDetectedTypeNotEmpty(DataManager.getPlanCountOfOneType(mType.code))
        } else {
            //如果决定要删除连带的计划，或检测到类型没有连带的计划，直接弹删除确认对话框
            mTypeDetailViewContract!!.showTypeDeletionConfirmationDialog(mType.name)
        }
    }

    fun notifyMovePlanButtonClicked() {
        mTypeDetailViewContract!!.showMovePlanDialog(mType.code)
    }

    fun notifyTypeItemInMovePlanDialogClicked(typeCode: String, typeName: String) {
        mTypeDetailViewContract!!.showPlanMigrationConfirmationDialog(mType.name, DataManager.getPlanCountOfOneType(mType.code), typeName, typeCode)
    }

    /**
     * 通知model删除类型
     * @param migration 是否将与此类型有关的计划迁移到另一类型
     * *
     * @param toTypeCode 要迁移到的类型代码
     */
    fun notifyDeletingType(migration: Boolean, toTypeCode: String?) {
        if (migration) {
            val planPosList = DataManager.getPlanLocationListOfOneType(mType.code)
            DataManager.migratePlan(planPosList, toTypeCode!!)
            for (position in planPosList) {
                mEventBus.post(PlanDetailChangedEvent(
                        presenterName,
                        DataManager.getPlan(position).code,
                        position,
                        PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN
                ))
            }
        }
        DataManager.notifyTypeDeleted(mTypeListPosition)
        mEventBus.post(TypeDeletedEvent(presenterName, mType.code, mTypeListPosition, mType))
        mTypeDetailViewContract!!.exit()
    }

    /** 注意！必须在更新UcPlanCountOfEachTypeMap之后调用才有效果  */
    private fun getUcPlanCountStr(code: String): String {
        return ResourceUtil.getQuantityString(
                R.string.text_uncompleted_plan_count,
                R.plurals.text_plan_count,
                DataManager.getUcPlanCountOfOneType(code)
        )
    }

    /** 计算给定planCode的计划在singleTypePlanList中的位置  */
    private fun getPosInSingleTypePlanList(code: String) = mSingleTypePlanList.indices.firstOrNull { mSingleTypePlanList[it].code == code } ?: -1

    /** 计算新加的plan在singleTypePlanList中的插入位置  */
    private fun getInsertionPosInSingleTypePlanList(creationTime: Long, completionTime: Long) = mSingleTypePlanList.indices.firstOrNull {
        val (_, _, _, creationTimeInList, _, completionTimeInList) = mSingleTypePlanList[it]
        creationTimeInList != 0L && completionTimeInList == 0L && creationTimeInList < creationTime
                || creationTimeInList == 0L && completionTimeInList != 0L && completionTimeInList < completionTime
    } ?: -1

    @Subscribe
    fun onTypeDetailChanged(event: TypeDetailChangedEvent) {
        if (mType.code != event.typeCode || event.eventSource == presenterName) return
        when (event.changedField) {
            TypeDetailChangedEvent.FIELD_TYPE_NAME -> mTypeDetailViewContract!!.onTypeNameChanged(mType.name, StringUtil.getFirstChar(mType.name))
            TypeDetailChangedEvent.FIELD_TYPE_MARK_COLOR -> mTypeDetailViewContract!!.onTypeMarkColorChanged(Color.parseColor(mType.markColor))
            TypeDetailChangedEvent.FIELD_TYPE_MARK_PATTERN -> mTypeDetailViewContract!!.onTypeMarkPatternChanged(mType.hasMarkPattern, if (mType.hasMarkPattern) ResourceUtil.getDrawableResourceId(mType.markPattern!!) else 0)
        }
    }

    @Subscribe
    fun onPlanDetailChanged(event: PlanDetailChangedEvent) {
        if (event.eventSource == presenterName) return

        //刚才发生变化的计划在singleTypePlanList中的位置
        var position = getPosInSingleTypePlanList(event.planCode)
        //Plan从DataManager中取，因为上面的position可能为-1
        val plan = DataManager.getPlan(event.position)

        //若变化的plan不在mSingleTypePlanList中（position == -1），则说明此plan是外来的
        //则还需要判断【类型】是否由【其他类型】变为【当前类型】，则判断（改变的是不是【类型】&& 改变后的类型是不是【当前类型】）
        if (position == -1 && !(event.changedField == PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN && plan.typeCode == mType.code)) return

        when (event.changedField) {
            PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN -> {
                if (plan.typeCode == mType.code) {
                    //某个plan由其他类型变成了当前页的类型，需要添加到singleTypePlanList中
                    //计算插入位置
                    position = getInsertionPosInSingleTypePlanList(plan.creationTime, plan.completionTime)
                    mSingleTypePlanList.add(position, plan)
                    mSingleTypePlanListAdapter.notifyItemInsertedAndChangingFooter(position)
                } else {
                    //某个plan由当前页的类型变成了其他类型，需要从singleTypePlanList中移除
                    mSingleTypePlanList.removeAt(position)
                    mSingleTypePlanListAdapter.notifyItemRemovedAndChangingFooter(position)
                }
                //更新显示的未完成计划数量
                mTypeDetailViewContract!!.onUcPlanCountChanged(getUcPlanCountStr(mType.code))
            }
            //有完成情况的改变，直接全部刷新
            PlanDetailChangedEvent.FIELD_PLAN_STATUS -> mSingleTypePlanListAdapter.notifyDataSetChanged()
            //其他改变的刷新，不加payload，直接刷新整个item
            else -> mSingleTypePlanListAdapter.notifyItemChanged(position)
        }
    }

    @Subscribe
    fun onPlanDeleted(event: PlanDeletedEvent) {
        if (event.eventSource == presenterName) return

        //计算刚才删除的计划在这个list中的位置
        val position = getPosInSingleTypePlanList(event.planCode)

        //刷新list
        mSingleTypePlanList.removeAt(position)
        mSingleTypePlanListAdapter.notifyItemRemovedAndChangingFooter(position)

        if (!event.deletedPlan.isCompleted) {
            //说明刚才删除的是个未完成的计划，需要修改界面上的内容
            mTypeDetailViewContract!!.onUcPlanCountChanged(getUcPlanCountStr(mType.code))
        }
    }
}
