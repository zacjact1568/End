package com.zack.enderplan.view.contract;

import com.zack.enderplan.view.adapter.SimpleTypeAdapter;

public interface CreatePlanViewContract extends BaseViewContract {

    void showInitialView(SimpleTypeAdapter simpleTypeAdapter);

    void onContentChanged(boolean isValid);

    void onStarStatusChanged(boolean isStarred);

    void showDeadlinePickerDialog(long defaultDeadline);

    void onDeadlineChanged(boolean hasDeadline, String deadline);

    void showReminderTimePickerDialog(long defaultReminderTime);

    void onReminderTimeChanged(boolean hasReminder, String reminderTime);

    void onDetectedEmptyContent();
}
