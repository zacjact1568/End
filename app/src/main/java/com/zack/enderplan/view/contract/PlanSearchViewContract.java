package com.zack.enderplan.view.contract;

import com.zack.enderplan.view.adapter.PlanSearchListAdapter;

public interface PlanSearchViewContract extends BaseViewContract {

    void showInitialView(int planCount, PlanSearchListAdapter planSearchListAdapter);

    void onSearchChanged(boolean isNoSearchInput, boolean isPlanSearchEmpty);

    void onPlanItemClicked(int planListPos);
}
