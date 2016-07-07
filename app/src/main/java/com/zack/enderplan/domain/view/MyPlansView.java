package com.zack.enderplan.domain.view;

import com.zack.enderplan.interactor.adapter.PlanAdapter;
import com.zack.enderplan.model.bean.Plan;

public interface MyPlansView {

    void showInitialView(PlanAdapter planAdapter);

    void onPlanItemClicked(int position);

    void onPlanDeleted(String content, int position, Plan planUseForTakingBack);
}
