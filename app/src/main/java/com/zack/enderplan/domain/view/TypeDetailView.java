package com.zack.enderplan.domain.view;

import android.support.annotation.StringRes;

import com.zack.enderplan.interactor.adapter.PlanSingleTypeAdapter;
import com.zack.enderplan.model.bean.FormattedType;

public interface TypeDetailView {

    void showInitialView(FormattedType formattedType, PlanSingleTypeAdapter planSingleTypeAdapter);

    void onPlanCreationSuccess(String ucPlanCountStr);

    void onPlanCreationFailed();

    void onUcPlanCountChanged(String ucPlanCountStr);

    void onPlanItemClicked(int posInPlanList);

    void changeHeaderOpacity(float alpha);

    void changeTitle(String title);

    void changeEditorVisibility(boolean isVisible);

    void backToTop();

    void pressBack();

    void enterEditType(int position);

    void showToast(@StringRes int msgResId);

    void showDeletionConfirmationDialog(String typeName);

    void exitTypeDetail();
}
