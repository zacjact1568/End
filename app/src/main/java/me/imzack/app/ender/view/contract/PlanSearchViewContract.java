package me.imzack.app.ender.view.contract;

import me.imzack.app.ender.view.adapter.PlanSearchListAdapter;

public interface PlanSearchViewContract extends BaseViewContract {

    void showInitialView(int planCount, PlanSearchListAdapter planSearchListAdapter);

    void onSearchChanged(boolean isNoSearchInput, boolean isPlanSearchEmpty);

    void onPlanItemClicked(int planListPos);
}
