package com.zack.enderplan.domain.view;

import com.zack.enderplan.domain.fragment.CalendarDialogFragment;
import com.zack.enderplan.domain.fragment.DateTimePickerDialogFragment;
import com.zack.enderplan.interactor.adapter.TypeSpinnerAdapter;

public interface CreatePlanView {

    void showInitialView(TypeSpinnerAdapter typeSpinnerAdapter);

    void onStarStatusChanged(boolean isStarred);

    void onCreateDeadlineDialog(CalendarDialogFragment deadlineDialog);

    void onCreateReminderDialog(DateTimePickerDialogFragment reminderDialog);
}
