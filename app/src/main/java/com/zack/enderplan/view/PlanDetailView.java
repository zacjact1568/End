package com.zack.enderplan.view;

import com.zack.enderplan.activity.CalendarDialogFragment;
import com.zack.enderplan.activity.DateTimePickerDialogFragment;
import com.zack.enderplan.widget.TypeSpinnerAdapter;

public interface PlanDetailView {

    void showInitialView(String content, boolean isStarred, boolean hasDeadline, String deadline,
                             boolean hasReminder, String reminderTime);

    void showInitialSpinner(TypeSpinnerAdapter typeSpinnerAdapter, int posInSpinner);

    void showPlanDeletionAlertDialog(String content);

    void onPlanDeleted(String content);

    void showContentEditDialog(String content);

    void onContentEditSuccess(String content);

    void onContentEditFailed();

    void onStarStatusChanged(boolean isStarred);

    void onCreateDeadlineDialog(CalendarDialogFragment deadlineDialog);

    void onCreateReminderDialog(DateTimePickerDialogFragment reminderDialog);

    void onActivityFinished();

    void onDeadlineSelected(boolean isSetFirstTime, String deadline);

    void onDeadlineRemoved();

    void onReminderTimeSelected(boolean isSetFirstTime, String reminderTime);

    void onReminderRemoved();
}
