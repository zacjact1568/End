package me.imzack.app.ender.view.contract;

import android.support.v7.widget.helper.ItemTouchHelper;

import me.imzack.app.ender.view.adapter.PlanListAdapter;
import me.imzack.app.ender.model.bean.Plan;

public interface MyPlansViewContract extends BaseViewContract {

    void showInitialView(PlanListAdapter planListAdapter, ItemTouchHelper itemTouchHelper, boolean isPlanItemEmpty);

    void onPlanItemClicked(int position);

    void onPlanCreated();

    void onPlanDeleted(Plan deletedPlan, int position, boolean shouldShowSnackbar);

    void onPlanItemEmptyStateChanged(boolean isEmpty);
}
