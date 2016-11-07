package com.zack.enderplan.domain.view;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.zack.enderplan.interactor.adapter.SingleTypePlanAdapter;
import com.zack.enderplan.interactor.adapter.SimpleTypeAdapter;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.Plan;

public interface TypeDetailView {

    void showInitialView(FormattedType formattedType, String ucPlanCountStr, SingleTypePlanAdapter singleTypePlanAdapter);

    void onTypeNameChanged(String typeName, String firstChar);

    void onTypeMarkColorChanged(int colorInt);

    void onTypeMarkPatternChanged(boolean hasPattern, @DrawableRes int patternResId);

    void onPlanCreated();

    void onUcPlanCountChanged(String ucPlanCountStr);

    void onPlanDeleted(Plan deletedPlan, int position, int planListPos, boolean shouldShowSnackbar);

    void onPlanItemClicked(int posInPlanList);

    void onAppBarScrolled(float headerLayoutAlpha);

    void onAppBarScrolledToCriticalPoint(String toolbarTitle, float editorLayoutTransY);

    void backToTop();

    void pressBack();

    void enterEditType(int position, boolean shouldPlaySharedElementTransition);

    void showToast(@StringRes int msgResId);

    void onDetectedDeletingLastType();

    void onDetectedTypeNotEmpty();

    void showMovePlanDialog(int planCount, SimpleTypeAdapter simpleTypeAdapter);

    void showTypeDeletionConfirmationDialog(String typeName);

    void showPlanMigrationConfirmationDialog(String fromTypeName, String toTypeName, String toTypeCode);

    void exitTypeDetail();
}
