package net.zackzhang.app.end.view.activity

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_type_detail.*
import kotlinx.android.synthetic.main.content_type_detail.*
import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import net.zackzhang.app.end.common.Constant
import net.zackzhang.app.end.injector.component.DaggerTypeDetailComponent
import net.zackzhang.app.end.injector.module.TypeDetailPresenterModule
import net.zackzhang.app.end.model.bean.FormattedType
import net.zackzhang.app.end.model.bean.Plan
import net.zackzhang.app.end.presenter.TypeDetailPresenter
import net.zackzhang.app.end.util.ColorUtil
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.util.StringUtil
import net.zackzhang.app.end.view.adapter.SingleTypePlanListAdapter
import net.zackzhang.app.end.view.contract.TypeDetailViewContract
import net.zackzhang.app.end.view.dialog.MessageDialogFragment
import net.zackzhang.app.end.view.dialog.TypePickerForPlanMigrationDialogFragment
import javax.inject.Inject

class TypeDetailActivity : BaseActivity(), TypeDetailViewContract {

    companion object {

        private const val TAG_DETECTED_TYPE_NOT_EMPTY = "detected_type_not_empty"
        private const val TAG_MOVE_PLAN = "move_plan"
        private const val TAG_TYPE_DELETION_CONFIRMATION = "type_deletion_confirmation"
        private const val TAG_PLAN_MIGRATION_CONFIRMATION = "type_migration_confirmation"

        private const val KEY_PLAN_MIGRATION_TO_TYPE_CODE = "plan_migration_to_type_code"

        fun start(activity: Activity, typeListPosition: Int, enableTransition: Boolean, sharedElement: View?, transitionName: String?) {
            val intent = Intent(activity, TypeDetailActivity::class.java)
            intent.putExtra(Constant.TYPE_LIST_POSITION, typeListPosition)
            intent.putExtra(Constant.ENABLE_TRANSITION, enableTransition)
            intent.putExtra(Constant.TRANSITION_NAME, transitionName)
            if (enableTransition) {
                activity.startActivity(
                        intent,
                        ActivityOptions.makeSceneTransitionAnimation(activity, sharedElement, transitionName).toBundle()
                )
            } else {
                activity.startActivity(intent)
            }
        }
    }

    @Inject
    lateinit var mTypeDetailPresenter: TypeDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTypeDetailPresenter.attach()
    }

    override fun onInjectPresenter() {
        DaggerTypeDetailComponent.builder()
                .typeDetailPresenterModule(TypeDetailPresenterModule(this, intent.getIntExtra(Constant.TYPE_LIST_POSITION, -1)))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onResume() {
        super.onResume()
        mTypeDetailPresenter.notifySwitchingViewVisibility(true)
    }

    override fun onPause() {
        super.onPause()
        mTypeDetailPresenter.notifySwitchingViewVisibility(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        mTypeDetailPresenter.detach()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_type_detail, menu)
        //在这里改变图标的tint，因为没法在xml文件中改
        menu.findItem(R.id.action_edit).icon.setTint(Color.WHITE)
        menu.findItem(R.id.action_delete).icon.setTint(Color.WHITE)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_edit -> mTypeDetailPresenter.notifyTypeEditingButtonClicked()
            //TODO 可能需要收起软键盘
            R.id.action_delete -> mTypeDetailPresenter.notifyTypeDeletionButtonClicked(false)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        mTypeDetailPresenter.notifyBackPressed()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        when (fragment.tag) {
            TAG_DETECTED_TYPE_NOT_EMPTY -> {
                val dialogFragment = fragment as MessageDialogFragment
                dialogFragment.okButtonClickListener = { mTypeDetailPresenter.notifyMovePlanButtonClicked() }
                dialogFragment.thirdButtonClickListener = { mTypeDetailPresenter.notifyTypeDeletionButtonClicked(true) }
            }
            TAG_MOVE_PLAN -> (fragment as TypePickerForPlanMigrationDialogFragment).typePickedListener = { typeCode, typeName -> mTypeDetailPresenter.notifyTypeItemInMovePlanDialogClicked(typeCode, typeName) }
            TAG_TYPE_DELETION_CONFIRMATION -> (fragment as MessageDialogFragment).okButtonClickListener = { mTypeDetailPresenter.notifyDeletingType(false, null) }
            TAG_PLAN_MIGRATION_CONFIRMATION -> {
                val dialogFragment = fragment as MessageDialogFragment
                dialogFragment.okButtonClickListener = { mTypeDetailPresenter.notifyDeletingType(true, dialogFragment.arguments!!.getString(KEY_PLAN_MIGRATION_TO_TYPE_CODE)) }
            }
        }
    }

    override fun showInitialView(formattedType: FormattedType, ucPlanCountStr: String, singleTypePlanListAdapter: SingleTypePlanListAdapter, itemTouchHelper: ItemTouchHelper) {
        val enableTransition = intent.getBooleanExtra(Constant.ENABLE_TRANSITION, false)

        if (enableTransition) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }

        setContentView(R.layout.activity_type_detail)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        setupActionBar()

        layout_app_bar.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                layout_app_bar.viewTreeObserver.removeOnPreDrawListener(this)
                mTypeDetailPresenter.notifyPreDrawingAppBar(layout_app_bar.totalScrollRange)
                return false
            }
        })

        layout_app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset -> mTypeDetailPresenter.notifyAppBarScrolled(verticalOffset) })

        if (enableTransition) {
            ic_type_mark.transitionName = intent.getStringExtra(Constant.TRANSITION_NAME)
        }

        layout_editor.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                layout_editor.viewTreeObserver.removeOnPreDrawListener(this)
                mTypeDetailPresenter.notifyPreDrawingEditorLayout(layout_editor.height)
                return false
            }
        })

        list_single_type_plan.adapter = singleTypePlanListAdapter
        list_single_type_plan.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                mTypeDetailPresenter.notifyPlanListScrolled(
                        !list_single_type_plan.canScrollVertically(-1),
                        !list_single_type_plan.canScrollVertically(1)
                )
            }
        })
        itemTouchHelper.attachToRecyclerView(list_single_type_plan)

        editor_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                mTypeDetailPresenter.notifyContentEditorTextChanged(s.toString())
            }
        })
        editor_content.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mTypeDetailPresenter.notifyCreatingPlan(v.text.toString())
            }
            false
        }

        onTypeNameChanged(formattedType.typeName, formattedType.firstChar)
        onTypeMarkColorChanged(formattedType.typeMarkColorInt)
        onTypeMarkPatternChanged(formattedType.hasTypeMarkPattern, formattedType.typeMarkPatternResId)
        onUcPlanCountChanged(ucPlanCountStr)
    }

    override fun onTypeNameChanged(typeName: String, firstChar: String) {
        ic_type_mark.setInnerText(firstChar)
        text_type_name.text = typeName
        editor_content.hint = String.format(getString(R.string.hint_editor_content_format), typeName)
    }

    override fun onTypeMarkColorChanged(colorInt: Int) {
        window.navigationBarColor = colorInt
        val headerColorInt = ColorUtil.reduceSaturation(colorInt, 0.85f)
        layout_collapsing_toolbar.setContentScrimColor(headerColorInt)
        layout_collapsing_toolbar.setStatusBarScrimColor(colorInt)
        bg_header.setImageDrawable(ColorDrawable(headerColorInt))
        ic_type_mark.setFillColor(colorInt)
    }

    override fun onTypeMarkPatternChanged(hasPattern: Boolean, patternResId: Int) {
        ic_type_mark.setInnerIcon(if (hasPattern) getDrawable(patternResId) else null)
    }

    override fun onPlanCreated() {
        list_single_type_plan.scrollToPosition(0)
        editor_content.text = null
    }

    override fun onUcPlanCountChanged(ucPlanCountStr: String) {
        text_uc_plan_count.text = ucPlanCountStr
    }

    override fun onPlanDeleted(deletedPlan: Plan, position: Int, planListPos: Int, shouldShowSnackbar: Boolean) {
        if (shouldShowSnackbar) {
            Snackbar.make(list_single_type_plan, String.format(getString(R.string.snackbar_delete_format), deletedPlan.content), Snackbar.LENGTH_LONG)
                    .setAction(R.string.button_undo) { mTypeDetailPresenter.notifyCreatingPlan(deletedPlan, position, planListPos) }
                    .show()
        }
    }

    override fun onPlanItemClicked(posInPlanList: Int) {
        PlanDetailActivity.start(this, posInPlanList)
    }

    override fun onAppBarScrolled(headerLayoutAlpha: Float) {
        layout_header.alpha = headerLayoutAlpha
    }

    override fun onAppBarScrolledToCriticalPoint(toolbarTitle: String, editorLayoutTransY: Float) {
        toolbar.title = toolbarTitle
        ObjectAnimator.ofFloat(layout_editor, "translationY", layout_editor.translationY, editorLayoutTransY)
                .setDuration(200)
                .start()
    }

    override fun changeContentEditorClearTextIconVisibility(isVisible: Boolean) {
        ic_clear_text.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun backToTop() {
        layout_app_bar.setExpanded(true)
        //TODO 滑动到顶部
        //list_single_type_plan.scrollToPosition(0);
    }

    override fun pressBack() {
        super.onBackPressed()
    }

    override fun enterEditType(position: Int, enableTransition: Boolean) {
        TypeEditActivity.start(
                this,
                position,
                enableTransition,
                ic_type_mark,
                getString(R.string.transition_type_mark_icon)
        )
    }

    override fun onDetectedDeletingLastType() {
        MessageDialogFragment.Builder()
                .setTitle(R.string.title_dialog_last_type)
                .setMessage(R.string.msg_dialog_last_type)
                .setOkButtonText(android.R.string.ok)
                .show(supportFragmentManager)
    }

    override fun onDetectedTypeNotEmpty(planCount: Int) {
        val buttons = arrayOf(getString(R.string.button_move), getString(R.string.button_delete), getString(R.string.button_cancel))
        MessageDialogFragment.Builder()
                .setTitle(R.string.title_dialog_type_not_empty)
                .setMessage(StringUtil.addSpan(
                        StringUtil.toUpperCase(ResourceUtil.getQuantityString(R.string.msg_dialog_type_not_empty, R.plurals.text_plan_count, planCount), buttons),
                        buttons,
                        StringUtil.SPAN_BOLD_STYLE
                ))
                .setThirdButtonText(buttons[1])
                .setOkButtonText(buttons[0])
                .show(supportFragmentManager, TAG_DETECTED_TYPE_NOT_EMPTY)
    }

    override fun showMovePlanDialog(typeCode: String) {
        TypePickerForPlanMigrationDialogFragment.newInstance(typeCode).show(supportFragmentManager, TAG_MOVE_PLAN)
    }

    override fun showTypeDeletionConfirmationDialog(typeName: String) {
        MessageDialogFragment.Builder()
                .setTitle(typeName)
                .setMessage(R.string.msg_dialog_delete_type)
                .setOkButtonText(R.string.button_delete)
                .showCancelButton()
                .show(supportFragmentManager, TAG_TYPE_DELETION_CONFIRMATION)
    }

    override fun showPlanMigrationConfirmationDialog(fromTypeName: String, planCount: Int, toTypeName: String, toTypeCode: String) {
        val dialogFragment = MessageDialogFragment.Builder()
                .setTitle(fromTypeName)
                .setMessage(StringUtil.addSpan(
                        String.format(ResourceUtil.getString(R.string.msg_dialog_migrate_plan), ResourceUtil.getQuantityString(R.plurals.text_plan_count, planCount), toTypeName, fromTypeName),
                        arrayOf(toTypeName, fromTypeName),
                        StringUtil.SPAN_BOLD_STYLE
                ))
                .setOkButtonText(R.string.btn_dialog_move_and_delete)
                .showCancelButton()
                .build()
        // 将 toTypeCode 直接存入 dialogFragment 的 arguments 中，供确定按钮的回调函数使用
        dialogFragment.arguments!!.putString(KEY_PLAN_MIGRATION_TO_TYPE_CODE, toTypeCode)
        dialogFragment.show(supportFragmentManager, TAG_PLAN_MIGRATION_CONFIRMATION)
    }

    @OnClick(R.id.ic_clear_text)
    fun onClick(view: View) {
        when (view.id) {
            R.id.ic_clear_text -> editor_content.setText("")
        }
    }
}
