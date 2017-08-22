package me.imzack.app.end.view.contract

import me.imzack.app.end.view.adapter.PlanSearchListAdapter

interface PlanSearchViewContract : BaseViewContract {

    fun showInitialView(planCount: Int, planSearchListAdapter: PlanSearchListAdapter)

    fun onSearchChanged(isNoSearchInput: Boolean, isPlanSearchEmpty: Boolean)

    fun onPlanItemClicked(planListPos: Int)
}
