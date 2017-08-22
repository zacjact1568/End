package me.imzack.app.end.presenter

import android.content.SharedPreferences
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.event.DataLoadedEvent
import me.imzack.app.end.event.PlanCreatedEvent
import me.imzack.app.end.event.PlanDeletedEvent
import me.imzack.app.end.event.PlanDetailChangedEvent
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.view.contract.HomeViewContract
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class HomePresenter @Inject constructor(
        private var mHomeViewContract: HomeViewContract?,
        private val mEventBus: EventBus
) : BasePresenter(), SharedPreferences.OnSharedPreferenceChangeListener {

    //setTextSize传入的就是sp
    private val mPlanCountTextSizes = intArrayOf(52, 44, 32)
    private var mLastBackKeyPressedTime = 0L

    override fun attach() {
        mEventBus.register(this)
        DataManager.preferenceHelper.registerOnChangeListener(this)
        val count = planCount
        mHomeViewContract!!.showInitialView(count, getPlanCountTextSize(count), planCountDscpt)
    }

    override fun detach() {
        mHomeViewContract = null
        DataManager.preferenceHelper.unregisterOnChangeListener(this)
        mEventBus.unregister(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == Constant.PREF_KEY_DRAWER_HEADER_DISPLAY) {
            val count = planCount
            mHomeViewContract!!.changeDrawerHeaderDisplay(count, getPlanCountTextSize(count), planCountDscpt)
        }
    }

    fun notifyStartingUpCompleted() {
        if (DataManager.preferenceHelper.needGuideValue) {
            mHomeViewContract!!.enterActivity(Constant.GUIDE)
        }
    }

    fun notifyBackPressed(isDrawerOpen: Boolean, isOnRootFragment: Boolean) {
        val currentTime = System.currentTimeMillis()
        when {
            isDrawerOpen -> mHomeViewContract!!.closeDrawer()
            //不是在根Fragment（可以直接退出的Fragment）上，回到根Fragment
            !isOnRootFragment -> mHomeViewContract!!.showFragment(Constant.MY_PLANS)
            //连续点击间隔在1.5s以内，执行back键操作
            currentTime - mLastBackKeyPressedTime < 1500 -> mHomeViewContract!!.onPressBackKey()
            //否则更新上次点击back键的时间，并显示一个toast
            else -> {
                mLastBackKeyPressedTime = currentTime
                mHomeViewContract!!.showToast(R.string.toast_double_press_exit)
            }
        }
    }

    private val planCount: String
        get() {
            val planCount: Int
            val value = DataManager.preferenceHelper.drawerHeaderDisplayValue
            planCount = when (value) {
                Constant.PREF_VALUE_DHD_UPC -> DataManager.ucPlanCount
                Constant.PREF_VALUE_DHD_PC -> DataManager.planCount
                Constant.PREF_VALUE_DHD_TUPC -> DataManager.todayUcPlanCount
                else -> throw RuntimeException("\"$value\" is not one of any values of key \"drawer_header_display\"")
            }
            return if (planCount > 99) "99+" else planCount.toString()
        }

    private val planCountDscpt: String
        get() {
            val value = DataManager.preferenceHelper.drawerHeaderDisplayValue
            return ResourceUtil.getString(when (value) {
                Constant.PREF_VALUE_DHD_UPC -> R.string.text_uc_plan_count
                Constant.PREF_VALUE_DHD_PC -> R.string.text_plan_count
                Constant.PREF_VALUE_DHD_TUPC -> R.string.text_today_uc_plan_count
                else -> throw IllegalArgumentException("\"$value\" is not one of any values of key \"drawer_header_display\"")
            })
        }

    private fun getPlanCountTextSize(planCount: String) = when (planCount.length) {
        1 -> mPlanCountTextSizes[0]
        2 -> mPlanCountTextSizes[1]
        else -> mPlanCountTextSizes[2]
    }

    private fun changePlanCount() {
        val count = planCount
        mHomeViewContract!!.changePlanCount(count, getPlanCountTextSize(count))
    }

    @Subscribe
    fun onDataLoaded(event: DataLoadedEvent) {
        changePlanCount()
    }

    @Subscribe
    fun onPlanCreated(event: PlanCreatedEvent) {
        if (event.eventSource == presenterName) return
        //由于判断是否需要更新的逻辑略麻烦，简单起见，就不进行判断，直接更新了
        changePlanCount()
    }

    @Subscribe
    fun onPlanDeleted(event: PlanDeletedEvent) {
        if (event.eventSource == presenterName) return
        changePlanCount()
    }

    @Subscribe
    fun onPlanDetailChanged(event: PlanDetailChangedEvent) {
        if (event.eventSource == presenterName) return
        when (event.changedField) {
            PlanDetailChangedEvent.FIELD_PLAN_STATUS, PlanDetailChangedEvent.FIELD_DEADLINE -> changePlanCount()
        }
    }
}
