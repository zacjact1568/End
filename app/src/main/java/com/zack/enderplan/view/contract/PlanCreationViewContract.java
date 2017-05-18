package com.zack.enderplan.view.contract;

import com.zack.enderplan.view.adapter.TypeGalleryAdapter;

public interface PlanCreationViewContract extends BaseViewContract {

    void showInitialView(TypeGalleryAdapter typeGalleryAdapter);

    void onContentChanged(boolean isValid);

    void onStarStatusChanged(boolean isStarred);

    void onTypeCreationItemClicked();

    void showDeadlinePickerDialog(long defaultDeadline);

    void onDeadlineChanged(boolean hasDeadline, String deadline);

    void showReminderTimePickerDialog(long defaultReminderTime);

    void onReminderTimeChanged(boolean hasReminder, String reminderTime);
}
