package com.zack.enderplan.domain.view;

import android.content.Intent;

import com.zack.enderplan.domain.fragment.CalendarDialogFragment;
import com.zack.enderplan.domain.fragment.DateTimePickerDialogFragment;
import com.zack.enderplan.interactor.adapter.TypeSpinnerAdapter;

public interface PlanDetailView {

    void showInitialView(String content, boolean isStarred, TypeSpinnerAdapter typeSpinnerAdapter,
                         int posInSpinner, boolean hasDeadline, String deadline, boolean hasReminder,
                         String reminderTime, boolean isCompleted, String spsButtonText);

    void showPlanDeletionAlertDialog(String content);

    void onPlanDeleted(int position, String planCode, String content, boolean isCompleted);

    void onPlanStatusChanged(boolean isCompleted, String newSpsButtonText);

    void showContentEditDialog(String content);

    void onContentEditSuccess(String content);

    void onContentEditFailed();

    void onStarStatusChanged(boolean isStarred);

    void onCreateDeadlineDialog(CalendarDialogFragment deadlineDialog);

    void onCreateReminderDialog(DateTimePickerDialogFragment reminderDialog);

    void onActivityFinished(Intent intent);

    void onDeadlineSelected(boolean isSetFirstTime, String deadline);

    void onDeadlineRemoved();

    void onReminderTimeSelected(boolean isSetFirstTime, String reminderTime);

    void onReminderRemoved();
}
