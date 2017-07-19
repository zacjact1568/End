package me.imzack.app.end.view.contract;

import android.support.v7.widget.helper.ItemTouchHelper;

import me.imzack.app.end.view.adapter.PlanListAdapter;
import me.imzack.app.end.model.bean.Plan;

public interface MyPlansViewContract extends BaseViewContract {

    void showInitialView(PlanListAdapter planListAdapter, ItemTouchHelper itemTouchHelper, boolean isPlanItemEmpty);

    void onPlanItemClicked(int position);

    void onPlanCreated();

    void onPlanDeleted(Plan deletedPlan, int position, boolean shouldShowSnackbar);

    void onPlanItemEmptyStateChanged(boolean isEmpty);
}
