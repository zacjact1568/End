package me.imzack.app.end.view.activity

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import butterknife.BindString
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.injector.component.DaggerTypeDetailComponent
import me.imzack.app.end.injector.module.TypeDetailPresenterModule
import me.imzack.app.end.model.bean.FormattedType
import me.imzack.app.end.model.bean.Plan
import me.imzack.app.end.presenter.TypeDetailPresenter
import me.imzack.app.end.util.ColorUtil
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.StringUtil
import me.imzack.app.end.view.adapter.SingleTypePlanListAdapter
import me.imzack.app.end.view.contract.TypeDetailViewContract
import me.imzack.app.end.view.dialog.BaseDialogFragment
import me.imzack.app.end.view.dialog.MessageDialogFragment
import me.imzack.app.end.view.dialog.TypePickerForPlanMigrationDialogFragment
import me.imzack.app.end.view.widget.CircleColorView
import javax.inject.Inject

class TypeDetailActivity : BaseActivity(), TypeDetailViewContract {

    companion object {

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

    @BindView(R.id.layout_app_bar)
    lateinit var mAppBarLayout: AppBarLayout
    @BindView(R.id.layout_collapsing_toolbar)
    lateinit var mCollapsingToolbarLayout: CollapsingToolbarLayout
    @BindView(R.id.bg_header)
    lateinit var mHeaderBackground: ImageView
    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.layout_header)
    lateinit var mHeaderLayout: LinearLayout
    @BindView(R.id.ic_type_mark)
    lateinit var mTypeMarkIcon: CircleColorView
    @BindView(R.id.text_type_name)
    lateinit var mTypeNameText: TextView
    @BindView(R.id.text_uc_plan_count)
    lateinit var mUcPlanCountText: TextView
    @BindView(R.id.editor_content)
    lateinit var mContentEditor: EditText
    @BindView(R.id.ic_clear_text)
    lateinit var mClearTextIcon: ImageView
    @BindView(R.id.list_single_type_plan)
    lateinit var mSingleTypePlanList: RecyclerView
    @BindView(R.id.layout_editor)
    lateinit var mEditorLayout: FrameLayout

    @BindString(R.string.hint_editor_content_format)
    lateinit var mContentEditorHintFormat: String
    @BindString(R.string.snackbar_delete_format)
    lateinit var mSnackbarDeleteFormat: String
    @BindString(R.string.transition_type_mark_icon)
    lateinit var mTypeMarkIconSetName: String

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

    override fun showInitialView(formattedType: FormattedType, ucPlanCountStr: String, singleTypePlanListAdapter: SingleTypePlanListAdapter, itemTouchHelper: ItemTouchHelper) {
        val enableTransition = intent.getBooleanExtra(Constant.ENABLE_TRANSITION, false)

        if (enableTransition) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }

        setContentView(R.layout.activity_type_detail)
        ButterKnife.bind(this)

        setSupportActionBar(mToolbar)
        setupActionBar()

        mAppBarLayout.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                mAppBarLayout.viewTreeObserver.removeOnPreDrawListener(this)
                mTypeDetailPresenter.notifyPreDrawingAppBar(mAppBarLayout.totalScrollRange)
                return false
            }
        })

        mAppBarLayout.addOnOffsetChangedListener { _, verticalOffset -> mTypeDetailPresenter.notifyAppBarScrolled(verticalOffset) }

        if (enableTransition) {
            mTypeMarkIcon.transitionName = intent.getStringExtra(Constant.TRANSITION_NAME)
        }

        mEditorLayout.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                mEditorLayout.viewTreeObserver.removeOnPreDrawListener(this)
                mTypeDetailPresenter.notifyPreDrawingEditorLayout(mEditorLayout.height)
                return false
            }
        })

        mSingleTypePlanList.adapter = singleTypePlanListAdapter
        mSingleTypePlanList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                mTypeDetailPresenter.notifyPlanListScrolled(
                        !mSingleTypePlanList.canScrollVertically(-1),
                        !mSingleTypePlanList.canScrollVertically(1)
                )
            }
        })
        itemTouchHelper.attachToRecyclerView(mSingleTypePlanList)

        mContentEditor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                mTypeDetailPresenter.notifyContentEditorTextChanged(s.toString())
            }
        })
        mContentEditor.setOnEditorActionListener { v, actionId, _ ->
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
        mTypeMarkIcon.setInnerText(firstChar)
        mTypeNameText.text = typeName
        mContentEditor.hint = String.format(mContentEditorHintFormat, typeName)
    }

    override fun onTypeMarkColorChanged(colorInt: Int) {
        window.navigationBarColor = colorInt
        val headerColorInt = ColorUtil.reduceSaturation(colorInt, 0.85f)
        mCollapsingToolbarLayout.setContentScrimColor(headerColorInt)
        mCollapsingToolbarLayout.setStatusBarScrimColor(colorInt)
        mHeaderBackground.setImageDrawable(ColorDrawable(headerColorInt))
        mTypeMarkIcon.setFillColor(colorInt)
    }

    override fun onTypeMarkPatternChanged(hasPattern: Boolean, patternResId: Int) {
        mTypeMarkIcon.setInnerIcon(if (hasPattern) getDrawable(patternResId) else null)
    }

    override fun onPlanCreated() {
        mSingleTypePlanList.scrollToPosition(0)
        mContentEditor.setText(null)
    }

    override fun onUcPlanCountChanged(ucPlanCountStr: String) {
        mUcPlanCountText.text = ucPlanCountStr
    }

    override fun onPlanDeleted(deletedPlan: Plan, position: Int, planListPos: Int, shouldShowSnackbar: Boolean) {
        if (shouldShowSnackbar) {
            Snackbar.make(mSingleTypePlanList, String.format(mSnackbarDeleteFormat, deletedPlan.content), Snackbar.LENGTH_LONG)
                    .setAction(R.string.button_undo) { mTypeDetailPresenter.notifyCreatingPlan(deletedPlan, position, planListPos) }
                    .show()
        }
    }

    override fun onPlanItemClicked(posInPlanList: Int) {
        PlanDetailActivity.start(this, posInPlanList)
    }

    override fun onAppBarScrolled(headerLayoutAlpha: Float) {
        mHeaderLayout.alpha = headerLayoutAlpha
    }

    override fun onAppBarScrolledToCriticalPoint(toolbarTitle: String, editorLayoutTransY: Float) {
        mToolbar.title = toolbarTitle
        ObjectAnimator.ofFloat(mEditorLayout, "translationY", mEditorLayout.translationY, editorLayoutTransY)
                .setDuration(200)
                .start()
    }

    override fun changeContentEditorClearTextIconVisibility(isVisible: Boolean) {
        mClearTextIcon.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun backToTop() {
        mAppBarLayout.setExpanded(true)
        //TODO 滑动到顶部
        //mSingleTypePlanList.scrollToPosition(0);
    }

    override fun pressBack() {
        super.onBackPressed()
    }

    override fun enterEditType(position: Int, enableTransition: Boolean) {
        TypeEditActivity.start(
                this,
                position,
                enableTransition,
                mTypeMarkIcon,
                mTypeMarkIconSetName
        )
    }

    override fun onDetectedDeletingLastType() {
        MessageDialogFragment.Builder()
                .setMessage(R.string.msg_dialog_last_type)
                .setTitle(R.string.title_dialog_last_type)
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_ok, null)
                .show(supportFragmentManager)
    }

    override fun onDetectedTypeNotEmpty(planCount: Int) {
        val buttons = arrayOf(getString(R.string.button_move), getString(R.string.button_delete), getString(R.string.button_cancel))
        MessageDialogFragment.Builder()
                .setMessage(StringUtil.addSpan(
                        StringUtil.toUpperCase(ResourceUtil.getQuantityString(R.string.msg_dialog_type_not_empty, R.plurals.text_plan_count, planCount), buttons),
                        buttons,
                        StringUtil.SPAN_BOLD_STYLE
                ))
                .setTitle(R.string.title_dialog_type_not_empty)
                .setNegativeButton(buttons[1], object : BaseDialogFragment.OnButtonClickListener {
                    override fun onClick(): Boolean {
                        mTypeDetailPresenter.notifyTypeDeletionButtonClicked(true)
                        return true
                    }
                })
                .setNegativeButton(buttons[2], null)
                .setPositiveButton(buttons[0], object : BaseDialogFragment.OnButtonClickListener {
                    override fun onClick(): Boolean {
                        mTypeDetailPresenter.notifyMovePlanButtonClicked()
                        return true
                    }
                })
                .show(supportFragmentManager)
    }

    override fun showMovePlanDialog(typeCode: String) {
        TypePickerForPlanMigrationDialogFragment.newInstance(
                typeCode,
                object : TypePickerForPlanMigrationDialogFragment.OnTypePickedListener {
                    override fun onTypePicked(typeCode: String, typeName: String) {
                        mTypeDetailPresenter.notifyTypeItemInMovePlanDialogClicked(typeCode, typeName)
                    }
                }
        ).show(supportFragmentManager)
    }

    override fun showTypeDeletionConfirmationDialog(typeName: String) {
        MessageDialogFragment.Builder()
                .setMessage(R.string.msg_dialog_delete_type)
                .setTitle(typeName)
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_delete, object : BaseDialogFragment.OnButtonClickListener {
                    override fun onClick(): Boolean {
                        mTypeDetailPresenter.notifyDeletingType(false, null)
                        return true
                    }
                })
                .show(supportFragmentManager)
    }

    override fun showPlanMigrationConfirmationDialog(fromTypeName: String, planCount: Int, toTypeName: String, toTypeCode: String) {
        MessageDialogFragment.Builder()
                .setMessage(StringUtil.addSpan(
                        String.format(ResourceUtil.getString(R.string.msg_dialog_migrate_plan), ResourceUtil.getQuantityString(R.plurals.text_plan_count, planCount), toTypeName, fromTypeName),
                        arrayOf(toTypeName, fromTypeName),
                        StringUtil.SPAN_BOLD_STYLE
                ))
                .setTitle(fromTypeName)
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.btn_dialog_move_and_delete, object : BaseDialogFragment.OnButtonClickListener {
                    override fun onClick(): Boolean {
                        mTypeDetailPresenter.notifyDeletingType(true, toTypeCode)
                        return true
                    }
                })
                .show(supportFragmentManager)
    }

    @OnClick(R.id.ic_clear_text)
    fun onClick(view: View) {
        when (view.id) {
            R.id.ic_clear_text -> mContentEditor.setText("")
        }
    }
}
