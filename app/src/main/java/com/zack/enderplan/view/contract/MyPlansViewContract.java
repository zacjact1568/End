package com.zack.enderplan.view.contract;

import com.zack.enderplan.view.adapter.PlanAdapter;
import com.zack.enderplan.model.bean.Plan;

public interface MyPlansViewContract extends BaseViewContract {

    void showInitialView(PlanAdapter planAdapter);

    void onPlanItemClicked(int position);

    void onPlanDeleted(Plan deletedPlan, int position, boolean shouldShowSnackbar);
}
