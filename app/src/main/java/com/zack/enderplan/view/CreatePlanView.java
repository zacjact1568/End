package com.zack.enderplan.view;

import com.zack.enderplan.activity.CalendarDialogFragment;
import com.zack.enderplan.activity.DateTimePickerDialogFragment;

public interface CreatePlanView {

    void onStarStatusChanged(boolean isStarred);

    void onCreateDeadlineDialog(CalendarDialogFragment deadlineDialog);

    void onCreateReminderDialog(DateTimePickerDialogFragment reminderDialog);
}
