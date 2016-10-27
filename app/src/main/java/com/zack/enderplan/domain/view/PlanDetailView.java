package com.zack.enderplan.domain.view;

import com.zack.enderplan.interactor.adapter.SimpleTypeAdapter;
import com.zack.enderplan.model.bean.FormattedPlan;

public interface PlanDetailView {

    void showInitialView(FormattedPlan formattedPlan, SimpleTypeAdapter simpleTypeAdapter);

    void showPlanDeletionDialog(String content);

    void onPlanStatusChanged(boolean isCompleted);

    void showContentEditorDialog(String content);

    void onContentEditedSuccessfully(String newContent);

    void onContentEditedAbortively();

    void onStarStatusChanged(boolean isStarred);

    void onTypeOfPlanChanged(int posInTypeList);

    void showDeadlinePickerDialog(long defaultDeadline);

    void showReminderTimePickerDialog(long defaultReminderTime);

    void onDeadlineSelected(String newDeadlineText);

    void onDeadlineRemoved();

    void onReminderTimeSelected(String newReminderTimeText);

    void onReminderRemoved();

    void exitPlanDetail();
}
