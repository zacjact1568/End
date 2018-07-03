package net.zackzhang.app.end.presenter

import android.content.SharedPreferences
import android.os.Bundle
import net.zackzhang.app.end.R
import net.zackzhang.app.end.common.Constant
import net.zackzhang.app.end.event.DataLoadedEvent
import net.zackzhang.app.end.event.PlanCreatedEvent
import net.zackzhang.app.end.event.PlanDeletedEvent
import net.zackzhang.app.end.event.PlanDetailChangedEvent
import net.zackzhang.app.end.model.DataManager
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.view.contract.HomeViewContract
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class HomePresenter @Inject constructor(
        private var homeViewContract: HomeViewContract?,
        private val eventBus: EventBus
) : BasePresenter(), SharedPreferences.OnSharedPreferenceChangeListener {

    //setTextSize传入的就是sp
    private val planCountTextSizes = intArrayOf(52, 44, 32)
    private var lastBackKeyPressedTime = 0L
    private var isRestored = false
    private var currentFragmentTag = Constant.MY_PLANS

    override fun attach() {
        eventBus.register(this)
        DataManager.preferenceHelper.registerOnChangeListener(this)
        val count = planCount
        homeViewContract!!.showInitialView(count, getPlanCountTextSize(count), planCountDscpt)
    }

    override fun detach() {
        homeViewContract = null
        DataManager.preferenceHelper.unregisterOnChangeListener(this)
        eventBus.unregister(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == Constant.PREF_KEY_DRAWER_HEADER_DISPLAY) {
            val count = planCount
            homeViewContract!!.changeDrawerHeaderDisplay(count, getPlanCountTextSize(count), planCountDscpt)
        }
    }

    fun notifyInstanceStateRestored(restoredFragmentTag: String) {
        isRestored = true
        currentFragmentTag = restoredFragmentTag
    }

    fun notifyStartingUpCompleted() {
        if (DataManager.preferenceHelper.needGuideValue) {
            homeViewContract!!.startActivity(Constant.GUIDE)
        }
        homeViewContract!!.showInitialFragment(isRestored, currentFragmentTag)
    }

    fun notifySavingInstanceState(outState: Bundle) {
        outState.putString(Constant.CURRENT_FRAGMENT, currentFragmentTag)
    }

    fun notifySwitchingFragment(toTag: String) {
        switchFragment(toTag)
    }

    fun notifySearchButtonTouched() {
        homeViewContract!!.startActivity(
                when (currentFragmentTag) {
                    Constant.MY_PLANS -> Constant.PLAN_SEARCH
                    Constant.ALL_TYPES -> Constant.TYPE_SEARCH
                    else -> throw IllegalArgumentException("No corresponding fragment found for the tag \"$currentFragmentTag\"")
                }
        )
    }

    fun notifyCreationButtonTouched() {
        homeViewContract!!.startActivity(
                when (currentFragmentTag) {
                    Constant.MY_PLANS -> Constant.PLAN_CREATION
                    Constant.ALL_TYPES -> Constant.TYPE_CREATION
                    else -> throw IllegalArgumentException("No corresponding fragment found for the tag \"$currentFragmentTag\"")
                }
        )
    }

    fun notifyBackPressed(isDrawerOpen: Boolean, isOnRootFragment: Boolean) {
        val currentTime = System.currentTimeMillis()
        when {
            isDrawerOpen -> homeViewContract!!.closeDrawer()
            //不是在根Fragment（可以直接退出的Fragment）上，回到根Fragment
            !isOnRootFragment -> switchFragment(Constant.MY_PLANS)
            //连续点击间隔在1.5s以内，执行back键操作
            currentTime - lastBackKeyPressedTime < 1500 -> homeViewContract!!.onPressBackKey()
            //否则更新上次点击back键的时间，并显示一个toast
            else -> {
                lastBackKeyPressedTime = currentTime
                homeViewContract!!.showToast(R.string.toast_double_press_exit)
            }
        }
    }

    private fun switchFragment(toTag: String) {
        if (currentFragmentTag == toTag) return
        homeViewContract!!.switchFragment(currentFragmentTag, toTag)
        currentFragmentTag = toTag
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
        1 -> planCountTextSizes[0]
        2 -> planCountTextSizes[1]
        else -> planCountTextSizes[2]
    }

    private fun changePlanCount() {
        val count = planCount
        homeViewContract!!.changePlanCount(count, getPlanCountTextSize(count))
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
