package com.zack.enderplan.view;

import android.content.Intent;

import com.zack.enderplan.activity.CalendarDialogFragment;
import com.zack.enderplan.activity.DateTimePickerDialogFragment;
import com.zack.enderplan.widget.TypeSpinnerAdapter;

public interface PlanDetailView {

    void showInitialView(String content, boolean isStarred, TypeSpinnerAdapter typeSpinnerAdapter,
                         int posInSpinner, boolean hasDeadline, String deadline, boolean hasReminder,
                         String reminderTime, String spsButtonText);

    void showPlanDeletionAlertDialog(String content);

    void onPlanDeleted(int position, String planCode, String content, boolean isCompleted);

    void onPlanStatusChanged(String newSpsButtonText);

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
