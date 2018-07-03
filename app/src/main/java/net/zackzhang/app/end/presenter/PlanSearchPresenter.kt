package net.zackzhang.app.end.presenter

import android.text.TextUtils

import net.zackzhang.app.end.model.DataManager
import net.zackzhang.app.end.model.bean.Plan
import net.zackzhang.app.end.view.adapter.PlanSearchListAdapter
import net.zackzhang.app.end.view.contract.PlanSearchViewContract

import javax.inject.Inject

class PlanSearchPresenter @Inject constructor(
        private var mPlanSearchViewContract: PlanSearchViewContract?
) : BasePresenter() {

    private val mPlanSearchList = mutableListOf<Plan>()
    private val mPlanSearchListAdapter = PlanSearchListAdapter(mPlanSearchList)

    override fun attach() {
        mPlanSearchListAdapter.mOnPlanItemClickListener = { mPlanSearchViewContract!!.onPlanItemClicked(it) }

        mPlanSearchViewContract!!.showInitialView(DataManager.planCount, mPlanSearchListAdapter)
    }

    override fun detach() {
        mPlanSearchViewContract = null
    }

    fun notifySearchTextChanged(searchText: String) {
        DataManager.searchPlan(mPlanSearchList, searchText)
        mPlanSearchListAdapter.notifyDataSetChanged()
        mPlanSearchViewContract!!.onSearchChanged(TextUtils.isEmpty(searchText), mPlanSearchList.size == 0)
    }
}
