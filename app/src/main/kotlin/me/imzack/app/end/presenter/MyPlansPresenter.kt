package me.imzack.app.end.presenter

import android.support.v7.widget.helper.ItemTouchHelper
import me.imzack.app.end.common.Constant
import me.imzack.app.end.event.*
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.model.bean.Plan
import me.imzack.app.end.util.SystemUtil
import me.imzack.app.end.view.adapter.PlanListAdapter
import me.imzack.app.end.view.callback.BaseItemTouchCallback
import me.imzack.app.end.view.callback.PlanListItemTouchCallback
import me.imzack.app.end.view.contract.MyPlansViewContract
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class MyPlansPresenter @Inject constructor(
        private var mMyPlansViewContract: MyPlansViewContract?,
        private val mEventBus: EventBus
) : BasePresenter() {

    private val mPlanListAdapter = PlanListAdapter()
    private val mPlanItemEmpty
        get() = DataManager.planCount == 0

    private var mViewVisible = true

    override fun attach() {
        mEventBus.register(this)

        mPlanListAdapter.mOnPlanItemClickListener = { mMyPlansViewContract!!.onPlanItemClicked(it) }
        mPlanListAdapter.mOnStarStatusChangedListener = {
            DataManager.notifyStarStatusChanged(it)
            mEventBus.post(PlanDetailChangedEvent(
                    presenterName,
                    DataManager.getPlan(it).code,
                    it,
                    PlanDetailChangedEvent.FIELD_STAR_STATUS
            ))
        }

        val planListItemTouchCallback = PlanListItemTouchCallback()
        planListItemTouchCallback.mOnItemSwipedListener = { position, direction ->
            when (direction) {
                BaseItemTouchCallback.DIR_START -> notifyDeletingPlan(position)
                BaseItemTouchCallback.DIR_END -> notifyPlanStatusChanged(position)
            }
        }
        planListItemTouchCallback.mOnItemMovedListener = { fromPosition, toPosition ->
            notifyPlanSequenceChanged(fromPosition, toPosition)
        }

        mMyPlansViewContract!!.showInitialView(mPlanListAdapter, ItemTouchHelper(planListItemTouchCallback), mPlanItemEmpty)
    }

    override fun detach() {
        mMyPlansViewContract = null
        mEventBus.unregister(this)
    }

    fun notifySwitchingViewVisibility(isVisible: Boolean) {
        mViewVisible = isVisible
    }

    /** 两个参数分别指出list是否滚动到顶部和底部 */
    fun notifyPlanListScrolled(top: Boolean, bottom: Boolean) {
        mPlanListAdapter.notifyListScrolled(when {
            //触顶
            top -> Constant.SCROLL_EDGE_TOP
            //触底
            bottom -> Constant.SCROLL_EDGE_BOTTOM
            //中间
            else -> Constant.SCROLL_EDGE_MIDDLE
        })
    }

    fun notifyDeletingPlan(position: Int) {

        SystemUtil.makeShortVibrate()

        //必须先把要删除计划的引用拿到
        val plan = DataManager.getPlan(position)

        DataManager.notifyPlanDeleted(position)

        mPlanListAdapter.notifyItemRemovedAndChangingFooter(position)
        updatePlanItemEmptyState()

        mMyPlansViewContract!!.onPlanDeleted(plan, position, mViewVisible)

        mEventBus.post(PlanDeletedEvent(presenterName, plan.code, position, plan))
    }

    fun notifyCreatingPlan(newPlan: Plan, position: Int) {
        DataManager.notifyPlanCreated(newPlan, position)

        mPlanListAdapter.notifyItemInsertedAndChangingFooter(position)
        updatePlanItemEmptyState()

        mEventBus.post(PlanCreatedEvent(presenterName, newPlan.code, position))
    }

    fun notifyPlanStatusChanged(position: Int) {
        val plan = DataManager.getPlan(position)
        //首先检测此计划是否有提醒
        if (plan.hasReminder) {
            DataManager.notifyReminderTimeChanged(position, 0L)
            mEventBus.post(PlanDetailChangedEvent(presenterName, plan.code, position, PlanDetailChangedEvent.FIELD_REMINDER_TIME))
        }
        //执行以下语句时，只是在view上让position处的plan删除了，实际上还未被删除但也即将被删除
        //NOTE: 不能用notifyItemRemoved，会没有效果
        mPlanListAdapter.notifyItemRemoved(position)
        DataManager.notifyPlanStatusChanged(position)
        //这里，plan的状态已经更新
        val newPosition = if (plan.isCompleted) DataManager.ucPlanCount else 0
        mPlanListAdapter.notifyItemInserted(newPosition)
        //发送事件，更新其他组件
        mEventBus.post(PlanDetailChangedEvent(presenterName, plan.code, newPosition, PlanDetailChangedEvent.FIELD_PLAN_STATUS))
    }

    fun notifyPlanSequenceChanged(fromPosition: Int, toPosition: Int) {
        if (fromPosition < DataManager.ucPlanCount != toPosition < DataManager.ucPlanCount) return
        //只能移动到相同完成状态的计划位置处
        DataManager.swapPlansInPlanList(fromPosition, toPosition)
        mPlanListAdapter.notifyItemMoved(fromPosition, toPosition)
    }

    private fun updatePlanItemEmptyState() {
        mMyPlansViewContract!!.onPlanItemEmptyStateChanged(mPlanItemEmpty)
    }

    @Subscribe
    fun onDataLoaded(event: DataLoadedEvent) {
        mPlanListAdapter.notifyDataSetChanged()
        updatePlanItemEmptyState()
    }

    @Subscribe
    fun onPlanCreated(event: PlanCreatedEvent) {
        if (event.eventSource == presenterName) return

        mPlanListAdapter.notifyItemInsertedAndChangingFooter(event.position)
        updatePlanItemEmptyState()

        mMyPlansViewContract!!.onPlanCreated()
    }

    @Subscribe
    fun onTypeDetailChanged(event: TypeDetailChangedEvent) {
        val singleTypeUcPlanPosList = DataManager.getPlanLocationListOfOneType(event.typeCode)
        for (position in singleTypeUcPlanPosList) {
            //所有属于这个类型的计划都需要刷新
            mPlanListAdapter.notifyItemChanged(position, Constant.PLAN_PAYLOAD_TYPE_CODE)
        }
    }

    @Subscribe
    fun onPlanDetailChanged(event: PlanDetailChangedEvent) {
        if (event.eventSource == presenterName) return
        if (event.changedField == PlanDetailChangedEvent.FIELD_PLAN_STATUS) {
            //有完成情况的改变，直接全部刷新
            mPlanListAdapter.notifyDataSetChanged()
        } else {
            //其他改变的刷新，不加payload，直接刷新整个item
            mPlanListAdapter.notifyItemChanged(event.position)
        }
    }

    @Subscribe
    fun onPlanDeleted(event: PlanDeletedEvent) {
        if (event.eventSource == presenterName) return
        mPlanListAdapter.notifyItemRemovedAndChangingFooter(event.position)
    }
}
