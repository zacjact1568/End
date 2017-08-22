package me.imzack.app.end.presenter

import android.content.SharedPreferences
import android.support.v7.widget.helper.ItemTouchHelper
import me.imzack.app.end.common.Constant
import me.imzack.app.end.event.*
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.view.adapter.TypeListAdapter
import me.imzack.app.end.view.callback.TypeListItemTouchCallback
import me.imzack.app.end.view.contract.AllTypesViewContract
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class AllTypesPresenter @Inject constructor(
        private var mAllTypesViewContract: AllTypesViewContract?,
        private val mEventBus: EventBus
) : BasePresenter(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val mTypeListAdapter = TypeListAdapter()

    override fun attach() {
        mEventBus.register(this)
        DataManager.preferenceHelper.registerOnChangeListener(this)

        mTypeListAdapter.mOnTypeItemClickListener = { position, typeItem ->
            mAllTypesViewContract!!.onTypeItemClicked(position, typeItem)
        }

        val typeListItemTouchCallback = TypeListItemTouchCallback()
        typeListItemTouchCallback.mOnItemMovedListener = { fromPosition, toPosition ->
            DataManager.swapTypesInTypeList(fromPosition, toPosition)
            mTypeListAdapter.notifyItemMoved(fromPosition, toPosition)
        }

        mAllTypesViewContract!!.showInitialView(mTypeListAdapter, ItemTouchHelper(typeListItemTouchCallback))
    }

    override fun detach() {
        mAllTypesViewContract = null
        DataManager.preferenceHelper.unregisterOnChangeListener(this)
        mEventBus.unregister(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY) {
            //直接全部刷新
            mTypeListAdapter.notifyDataSetChanged()
        }
    }

    fun notifyPlanListScrolled(top: Boolean, bottom: Boolean) {
        mTypeListAdapter.notifyListScrolled(when {
            //触顶
            top -> Constant.SCROLL_EDGE_TOP
            //触底
            bottom -> Constant.SCROLL_EDGE_BOTTOM
            //中间
            else -> Constant.SCROLL_EDGE_MIDDLE
        })
    }

    @Subscribe
    fun onDataLoaded(event: DataLoadedEvent) {
        mTypeListAdapter.notifyDataSetChanged()
    }

    @Subscribe
    fun onTypeCreated(event: TypeCreatedEvent) {
        val position = DataManager.recentlyCreatedTypeLocation
        mTypeListAdapter.notifyItemInsertedAndChangingFooter(position)
        mAllTypesViewContract!!.onTypeCreated(position)
    }

    @Subscribe
    fun onPlanCreated(event: PlanCreatedEvent) {
        if (event.eventSource == presenterName) return
        //由于判断是否需要更新的逻辑略麻烦，简单起见，就不进行判断，直接更新了
        mTypeListAdapter.notifyItemChanged(DataManager.getTypeLocationInTypeList(DataManager.getPlan(event.position).typeCode), Constant.TYPE_PAYLOAD_PLAN_COUNT)
    }

    @Subscribe
    fun onTypeDetailChanged(event: TypeDetailChangedEvent) {
        if (event.eventSource == presenterName) return
        var payload: Any? = null
        when (event.changedField) {
            TypeDetailChangedEvent.FIELD_TYPE_NAME -> payload = Constant.TYPE_PAYLOAD_NAME
            TypeDetailChangedEvent.FIELD_TYPE_MARK_COLOR -> payload = Constant.TYPE_PAYLOAD_MARK_COLOR
            TypeDetailChangedEvent.FIELD_TYPE_MARK_PATTERN -> payload = Constant.TYPE_PAYLOAD_MARK_PATTERN
        }
        mTypeListAdapter.notifyItemChanged(event.position, payload)
    }

    @Subscribe
    fun onTypeDeleted(event: TypeDeletedEvent) {
        mTypeListAdapter.notifyItemRemovedAndChangingFooter(event.position)
    }

    @Subscribe
    fun onPlanDetailChanged(event: PlanDetailChangedEvent) {
        if (event.changedField == PlanDetailChangedEvent.FIELD_PLAN_STATUS || event.changedField == PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN) {
            //类型或完成情况改变后的刷新（其他改变未在此界面上呈现）
            //因为可能有多个item需要刷新，比较麻烦，所以直接全部刷新了
            //这里不能用notifyDataSetChanged，会造成shared element transition返回动画错位
            mTypeListAdapter.notifyAllItemsChanged(Constant.TYPE_PAYLOAD_PLAN_COUNT)
        }
    }

    @Subscribe
    fun onPlanDeleted(event: PlanDeletedEvent) {
        if (event.eventSource == presenterName) return
        //由于判断是否需要更新的逻辑略麻烦，简单起见，就不进行判断，直接更新了
        mTypeListAdapter.notifyItemChanged(DataManager.getTypeLocationInTypeList(event.deletedPlan.typeCode), Constant.TYPE_PAYLOAD_PLAN_COUNT)
    }
}
