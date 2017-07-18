package me.imzack.app.ender.view.contract;

import me.imzack.app.ender.model.bean.FormattedType;
import me.imzack.app.ender.view.adapter.TypeGalleryAdapter;

public interface PlanCreationViewContract extends BaseViewContract {

    void showInitialView(TypeGalleryAdapter typeGalleryAdapter, FormattedType formattedType);

    void onContentChanged(boolean isValid);

    void onStarStatusChanged(boolean isStarred);

    void onTypeOfPlanChanged(FormattedType formattedType);

    void onTypeCreationItemClicked();

    void showDeadlinePickerDialog(long defaultDeadline);

    void onDeadlineChanged(CharSequence deadline);

    void showReminderTimePickerDialog(long defaultReminderTime);

    void onReminderTimeChanged(CharSequence reminderTime);
}
