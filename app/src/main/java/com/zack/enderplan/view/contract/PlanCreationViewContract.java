package com.zack.enderplan.view.contract;

import com.zack.enderplan.view.adapter.SimpleTypeListAdapter;

public interface PlanCreationViewContract extends BaseViewContract {

    void showInitialView(SimpleTypeListAdapter simpleTypeListAdapter);

    void onContentChanged(boolean isValid);

    void onStarStatusChanged(boolean isStarred);

    void showDeadlinePickerDialog(long defaultDeadline);

    void onDeadlineChanged(boolean hasDeadline, String deadline);

    void showReminderTimePickerDialog(long defaultReminderTime);

    void onReminderTimeChanged(boolean hasReminder, String reminderTime);
}
