package me.imzack.app.end.view.contract

import android.support.annotation.DrawableRes
import android.support.v7.widget.helper.ItemTouchHelper
import me.imzack.app.end.model.bean.FormattedType
import me.imzack.app.end.model.bean.Plan
import me.imzack.app.end.view.adapter.SingleTypePlanListAdapter

interface TypeDetailViewContract : BaseViewContract {

    fun showInitialView(formattedType: FormattedType, ucPlanCountStr: String, singleTypePlanListAdapter: SingleTypePlanListAdapter, itemTouchHelper: ItemTouchHelper)

    fun onTypeNameChanged(typeName: String, firstChar: String)

    fun onTypeMarkColorChanged(colorInt: Int)

    fun onTypeMarkPatternChanged(hasPattern: Boolean, @DrawableRes patternResId: Int)

    fun onPlanCreated()

    fun onUcPlanCountChanged(ucPlanCountStr: String)

    fun onPlanDeleted(deletedPlan: Plan, position: Int, planListPos: Int, shouldShowSnackbar: Boolean)

    fun onPlanItemClicked(posInPlanList: Int)

    fun onAppBarScrolled(headerLayoutAlpha: Float)

    fun onAppBarScrolledToCriticalPoint(toolbarTitle: String, editorLayoutTransY: Float)

    fun changeContentEditorClearTextIconVisibility(isVisible: Boolean)

    fun backToTop()

    fun pressBack()

    fun enterEditType(position: Int, enableTransition: Boolean)

    fun onDetectedDeletingLastType()

    fun onDetectedTypeNotEmpty(planCount: Int)

    fun showMovePlanDialog(typeCode: String)

    fun showTypeDeletionConfirmationDialog(typeName: String)

    fun showPlanMigrationConfirmationDialog(fromTypeName: String, planCount: Int, toTypeName: String, toTypeCode: String)
}
