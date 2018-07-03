package net.zackzhang.app.end.view.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_my_plans.*
import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import net.zackzhang.app.end.injector.component.DaggerMyPlansComponent
import net.zackzhang.app.end.injector.module.MyPlansPresenterModule
import net.zackzhang.app.end.model.bean.Plan
import net.zackzhang.app.end.presenter.MyPlansPresenter
import net.zackzhang.app.end.view.activity.PlanDetailActivity
import net.zackzhang.app.end.view.adapter.PlanListAdapter
import net.zackzhang.app.end.view.contract.MyPlansViewContract
import javax.inject.Inject

class MyPlansFragment : BaseListFragment(), MyPlansViewContract {

    @Inject
    lateinit var mMyPlansPresenter: MyPlansPresenter

    override fun onInjectPresenter() {
        DaggerMyPlansComponent.builder()
                .myPlansPresenterModule(MyPlansPresenterModule(this))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_my_plans, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMyPlansPresenter.attach()
    }

    override fun onResume() {
        super.onResume()
        mMyPlansPresenter.notifySwitchingViewVisibility(true)
    }

    override fun onPause() {
        super.onPause()
        mMyPlansPresenter.notifySwitchingViewVisibility(false)
    }

    override fun onDetach() {
        super.onDetach()
        mMyPlansPresenter.detach()
    }

    override fun showInitialView(planListAdapter: PlanListAdapter, itemTouchHelper: ItemTouchHelper, isPlanItemEmpty: Boolean) {
        list_plan.adapter = planListAdapter
        list_plan.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                onListScrolled(dy)
                mMyPlansPresenter.notifyPlanListScrolled(
                        !list_plan.canScrollVertically(-1),
                        !list_plan.canScrollVertically(1)
                )
            }
        })
        itemTouchHelper.attachToRecyclerView(list_plan)

        onPlanItemEmptyStateChanged(isPlanItemEmpty)
    }

    override fun onPlanItemClicked(position: Int) {
        PlanDetailActivity.start(context!!, position)
    }

    override fun onPlanCreated() {
        list_plan.scrollToPosition(0)
    }

    override fun onPlanDeleted(deletedPlan: Plan, position: Int, shouldShowSnackbar: Boolean) {
        if (shouldShowSnackbar) {
            Snackbar.make(list_plan, String.format(getString(R.string.snackbar_delete_format), deletedPlan.content), Snackbar.LENGTH_LONG)
                    .setAction(R.string.button_undo) { mMyPlansPresenter.notifyCreatingPlan(deletedPlan, position) }
                    .show()
        }
    }

    override fun onPlanItemEmptyStateChanged(isEmpty: Boolean) {
        list_plan.visibility = if (isEmpty) View.GONE else View.VISIBLE
        layout_empty.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}
