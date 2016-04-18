package com.zack.enderplan.view;

import com.zack.enderplan.widget.PlanSingleTypeAdapter;

public interface TypeDetailView {

    void showInitialView(int typeMarkColorRes, String firstChar, String typeName, String planCountStr, PlanSingleTypeAdapter planSingleTypeAdapter);

    void onPlanCreationSuccess(String planCountStr);

    void onPlanCreationFailed();
}
