package me.imzack.app.end.view.contract;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.helper.ItemTouchHelper;

import me.imzack.app.end.view.adapter.SingleTypePlanListAdapter;
import me.imzack.app.end.model.bean.FormattedType;
import me.imzack.app.end.model.bean.Plan;

public interface TypeDetailViewContract extends BaseViewContract {

    void showInitialView(FormattedType formattedType, String ucPlanCountStr, SingleTypePlanListAdapter singleTypePlanListAdapter, ItemTouchHelper itemTouchHelper);

    void onTypeNameChanged(String typeName, String firstChar);

    void onTypeMarkColorChanged(int colorInt);

    void onTypeMarkPatternChanged(boolean hasPattern, @DrawableRes int patternResId);

    void onPlanCreated();

    void onUcPlanCountChanged(String ucPlanCountStr);

    void onPlanDeleted(Plan deletedPlan, int position, int planListPos, boolean shouldShowSnackbar);

    void onPlanItemClicked(int posInPlanList);

    void onAppBarScrolled(float headerLayoutAlpha);

    void onAppBarScrolledToCriticalPoint(String toolbarTitle, float editorLayoutTransY);

    void changeContentEditorClearTextIconVisibility(boolean isVisible);

    void backToTop();

    void pressBack();

    void enterEditType(int position, boolean enableTransition);

    void onDetectedDeletingLastType();

    void onDetectedTypeNotEmpty(int planCount);

    void showMovePlanDialog(String typeCode);

    void showTypeDeletionConfirmationDialog(String typeName);

    void showPlanMigrationConfirmationDialog(String fromTypeName, int planCount, String toTypeName, String toTypeCode);
}
