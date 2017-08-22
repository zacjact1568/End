package me.imzack.app.end.view.contract

import android.support.v7.widget.helper.ItemTouchHelper
import me.imzack.app.end.model.bean.Plan
import me.imzack.app.end.view.adapter.PlanListAdapter

interface MyPlansViewContract : BaseViewContract {

    fun showInitialView(planListAdapter: PlanListAdapter, itemTouchHelper: ItemTouchHelper, isPlanItemEmpty: Boolean)

    fun onPlanItemClicked(position: Int)

    fun onPlanCreated()

    fun onPlanDeleted(deletedPlan: Plan, position: Int, shouldShowSnackbar: Boolean)

    fun onPlanItemEmptyStateChanged(isEmpty: Boolean)
}
