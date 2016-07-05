package com.zack.enderplan.domain.view;

import com.zack.enderplan.interactor.adapter.PlanSingleTypeAdapter;

public interface TypeDetailView {

    void showInitialView(int typeMarkColorRes, String firstChar, String typeName, String ucPlanCountStr,
                         PlanSingleTypeAdapter planSingleTypeAdapter);

    void onPlanCreationSuccess(String ucPlanCountStr);

    void onPlanCreationFailed();

    void onUcPlanCountChanged(String ucPlanCountStr);

    void onPlanItemClicked(int posInPlanList);
}
