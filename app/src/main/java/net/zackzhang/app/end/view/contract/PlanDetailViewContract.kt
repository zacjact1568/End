package net.zackzhang.app.end.view.contract

import net.zackzhang.app.end.model.bean.FormattedPlan
import net.zackzhang.app.end.model.bean.FormattedType

interface PlanDetailViewContract : BaseViewContract {

    fun showInitialView(formattedPlan: FormattedPlan, formattedType: FormattedType)

    fun onAppBarScrolled(headerLayoutAlpha: Float)

    fun onAppBarScrolledToCriticalPoint(toolbarTitle: String)

    fun showPlanDeletionDialog(content: String)

    fun onPlanStatusChanged(isCompleted: Boolean)

    fun showContentEditorDialog(content: String)

    fun onContentChanged(newContent: String)

    fun onStarStatusChanged(isStarred: Boolean)

    fun onTypeOfPlanChanged(formattedType: FormattedType)

    fun showTypePickerDialog(defaultTypeListPos: Int)

    fun showDeadlinePickerDialog(defaultDeadline: Long)

    fun showReminderTimePickerDialog(defaultReminderTime: Long)

    fun onDeadlineChanged(newDeadline: CharSequence)

    fun onReminderTimeChanged(newReminderTime: CharSequence)

    fun backToTop()

    fun pressBack()
}
