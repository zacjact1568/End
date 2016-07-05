package com.zack.enderplan.domain.view;

import com.zack.enderplan.model.bean.Plan;

public interface AllPlansView {

    void onPlanItemClicked(int position);

    void onPlanDeleted(String content, int position, Plan planUseForTakingBack);
}
