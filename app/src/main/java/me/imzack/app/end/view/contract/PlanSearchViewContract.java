package me.imzack.app.end.view.contract;

import me.imzack.app.end.view.adapter.PlanSearchListAdapter;

public interface PlanSearchViewContract extends BaseViewContract {

    void showInitialView(int planCount, PlanSearchListAdapter planSearchListAdapter);

    void onSearchChanged(boolean isNoSearchInput, boolean isPlanSearchEmpty);

    void onPlanItemClicked(int planListPos);
}
