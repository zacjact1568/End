package com.zack.enderplan.domain.view;

import com.zack.enderplan.domain.fragment.CalendarDialogFragment;
import com.zack.enderplan.domain.fragment.DateTimePickerDialogFragment;

public interface CreatePlanView {

    void onStarStatusChanged(boolean isStarred);

    void onCreateDeadlineDialog(CalendarDialogFragment deadlineDialog);

    void onCreateReminderDialog(DateTimePickerDialogFragment reminderDialog);
}
