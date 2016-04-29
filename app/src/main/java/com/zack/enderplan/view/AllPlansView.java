package com.zack.enderplan.view;

import com.zack.enderplan.bean.Plan;

public interface AllPlansView {

    void onPlanItemClicked(int position);

    void onPlanDeleted(String content, int position, Plan planUseForTakingBack);
}
