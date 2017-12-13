package me.imzack.app.end.presenter

import me.imzack.app.end.R
import me.imzack.app.end.event.PlanCreatedEvent
import me.imzack.app.end.event.TypeCreatedEvent
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.model.bean.Plan
import me.imzack.app.end.model.bean.Type
import me.imzack.app.end.util.ColorUtil
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.view.contract.GuideViewContract
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class GuidePresenter @Inject constructor(
        private var mGuideViewContract: GuideViewContract?,
        private val mEventBus: EventBus
) : BasePresenter() {

    override fun attach() {
        mGuideViewContract!!.showInitialView()
    }

    override fun detach() {
        mGuideViewContract = null
    }

    fun notifyEndingGuide(isNormally: Boolean) {
        if (isNormally) {
            createDefaultData()
            DataManager.preferenceHelper.needGuideValue = false
        }
        mGuideViewContract!!.exitWithResult(isNormally)
    }

    private fun createDefaultData() {
        //Default types
        val nameResIds = intArrayOf(R.string.def_type_1, R.string.def_type_2, R.string.def_type_3, R.string.def_type_4)
        val colorResIds = intArrayOf(
                R.color.indigo,
                R.color.red,
                R.color.orange,
                R.color.green
        )
        val patternFns = arrayOf("ic_computer_black_24dp", "ic_home_black_24dp", "ic_work_black_24dp", "ic_school_black_24dp")
        for (i in 0..3) {
            val type = Type(
                    name = ResourceUtil.getString(nameResIds[i]),
                    markColor = ColorUtil.parseColor(ResourceUtil.getColor(colorResIds[i])),
                    markPattern = patternFns[i]
            )
            DataManager.notifyTypeCreated(type)
            mEventBus.post(TypeCreatedEvent(presenterName, type.code, DataManager.recentlyCreatedTypeLocation))
        }

        //Default plans
        val contentResIds = intArrayOf(R.string.def_plan_4, R.string.def_plan_3, R.string.def_plan_2, R.string.def_plan_1)
        val defaultTypeCode = DataManager.getType(0).code
        for (i in 0..3) {
            val plan = Plan(
                    content = ResourceUtil.getString(contentResIds[i]),
                    typeCode = defaultTypeCode
            )
            DataManager.notifyPlanCreated(plan)
            mEventBus.post(PlanCreatedEvent(presenterName, plan.code, DataManager.recentlyCreatedPlanLocation))
        }
    }
}
