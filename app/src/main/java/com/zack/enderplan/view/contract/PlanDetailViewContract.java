package com.zack.enderplan.view.contract;

import com.zack.enderplan.view.adapter.SimpleTypeAdapter;
import com.zack.enderplan.model.bean.FormattedPlan;

public interface PlanDetailViewContract extends BaseViewContract {

    void showInitialView(FormattedPlan formattedPlan, SimpleTypeAdapter simpleTypeAdapter);

    void updateStarMenuItem(boolean isStarred);

    void onAppBarScrolled(float headerLayoutAlpha, float contentLayoutTransY);

    void onAppBarScrolledToCriticalPoint(String toolbarTitle, boolean isStarMenuItemVisible);

    void showPlanDeletionDialog(String content);

    void onPlanStatusChanged(boolean isCompleted);

    void showContentEditorDialog(String content);

    void onContentChanged(String newContent);

    void onStarStatusChanged(boolean isStarred);

    void onTypeOfPlanChanged(int posInTypeList);

    void showDeadlinePickerDialog(long defaultDeadline);

    void showReminderTimePickerDialog(long defaultReminderTime);

    void onDeadlineChanged(boolean hasDeadline, String newDeadline);

    void onReminderTimeChanged(boolean hasReminder, String newReminderTime);

    void backToTop();

    void pressBack();
}
