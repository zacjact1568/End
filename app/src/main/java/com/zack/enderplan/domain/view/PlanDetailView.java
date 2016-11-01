package com.zack.enderplan.domain.view;

import android.support.annotation.StringRes;

import com.zack.enderplan.interactor.adapter.SimpleTypeAdapter;
import com.zack.enderplan.model.bean.FormattedPlan;

public interface PlanDetailView {

    void showInitialView(FormattedPlan formattedPlan, SimpleTypeAdapter simpleTypeAdapter);

    void updateStarMenuItem(boolean isStarred);

    void onAppBarScrolled(float headerLayoutAlpha, float contentLayoutTransY);

    void updateToolbar(String title, boolean isStarMenuItemVisible);

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

    void showToast(@StringRes int msgResId);

    void exitPlanDetail();
}
