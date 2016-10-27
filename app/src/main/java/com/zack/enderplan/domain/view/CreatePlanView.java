package com.zack.enderplan.domain.view;

import com.zack.enderplan.interactor.adapter.SimpleTypeAdapter;

public interface CreatePlanView {

    void showInitialView(SimpleTypeAdapter simpleTypeAdapter);

    void onStarStatusChanged(boolean isStarred);

    void showDeadlinePickerDialog(long defaultDeadline);

    void showReminderTimePickerDialog(long defaultReminderTime);
}
