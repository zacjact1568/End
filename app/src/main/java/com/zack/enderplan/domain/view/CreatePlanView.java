package com.zack.enderplan.domain.view;

import com.zack.enderplan.interactor.adapter.SimpleTypeAdapter;

public interface CreatePlanView {

    void showInitialView(SimpleTypeAdapter simpleTypeAdapter);

    void onContentChanged(boolean isValid);

    void onStarStatusChanged(boolean isStarred);

    void showDeadlinePickerDialog(long defaultDeadline);

    void onDeadlineChanged(boolean hasDeadline, String deadline);

    void showReminderTimePickerDialog(long defaultReminderTime);

    void onReminderTimeChanged(boolean hasReminder, String reminderTime);

    void onDetectedEmptyContent();

    void exitCreatePlan();
}
