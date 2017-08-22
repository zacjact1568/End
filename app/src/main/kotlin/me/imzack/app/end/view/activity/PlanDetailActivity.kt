package me.imzack.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.injector.component.DaggerPlanDetailComponent
import me.imzack.app.end.injector.module.PlanDetailPresenterModule
import me.imzack.app.end.model.bean.FormattedPlan
import me.imzack.app.end.model.bean.FormattedType
import me.imzack.app.end.presenter.PlanDetailPresenter
import me.imzack.app.end.util.ColorUtil
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.view.contract.PlanDetailViewContract
import me.imzack.app.end.view.dialog.*
import me.imzack.app.end.view.widget.CircleColorView
import me.imzack.app.end.view.widget.ItemView
import javax.inject.Inject

class PlanDetailActivity : BaseActivity(), PlanDetailViewContract {

    companion object {

        fun start(context: Context, planListPosition: Int) {
            context.startActivity(
                    Intent(context, PlanDetailActivity::class.java)
                            .putExtra(Constant.PLAN_LIST_POSITION, planListPosition)
            )
        }
    }

    @BindView(R.id.layout_app_bar)
    lateinit var mAppBarLayout: AppBarLayout
    @BindView(R.id.layout_collapsing_toolbar)
    lateinit var mCollapsingToolbarLayout: CollapsingToolbarLayout
    @BindView(R.id.bg_header)
    lateinit var mHeaderBackground: ImageView
    @BindView(R.id.layout_header)
    lateinit var mHeaderLayout: LinearLayout
    @BindView(R.id.text_content)
    lateinit var contentText: TextView
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.ic_type_mark)
    lateinit var mTypeMarkIcon: CircleColorView
    @BindView(R.id.text_type_name)
    lateinit var mTypeNameText: TextView
    @BindView(R.id.fab_star)
    lateinit var mStarFab: FloatingActionButton
    @BindView(R.id.item_type)
    lateinit var mTypeItem: ItemView
    @BindView(R.id.item_deadline)
    lateinit var mDeadlineItem: ItemView
    @BindView(R.id.item_reminder)
    lateinit var mReminderItem: ItemView
    @BindView(R.id.btn_switch_plan_status)
    lateinit var switchPlanStatusButton: TextView

    @Inject
    lateinit var planDetailPresenter: PlanDetailPresenter

    private val mAccentColor = ResourceUtil.getColor(R.color.colorAccent)
    private val mGrey600Color = ResourceUtil.getColor(R.color.grey_600)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        planDetailPresenter.attach()
    }

    override fun onInjectPresenter() {
        DaggerPlanDetailComponent.builder()
                .planDetailPresenterModule(PlanDetailPresenterModule(this, intent.getIntExtra(Constant.PLAN_LIST_POSITION, -1)))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        planDetailPresenter.detach()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_plan_detail, menu)
        //在这里改变图标的tint，因为没法在xml文件中改
        menu.findItem(R.id.action_edit).icon.setTint(Color.WHITE)
        menu.findItem(R.id.action_delete).icon.setTint(Color.WHITE)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_star -> planDetailPresenter.notifyStarStatusChanged()
            R.id.action_edit -> planDetailPresenter.notifyContentEditingButtonClicked()
            R.id.action_delete -> planDetailPresenter.notifyPlanDeletionButtonClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        planDetailPresenter.notifyBackPressed()
    }

    override fun showInitialView(formattedPlan: FormattedPlan, formattedType: FormattedType) {

        setContentView(R.layout.activity_plan_detail)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        setupActionBar()

        //注释掉这一句使AppBar可折叠
        (mCollapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams).scrollFlags = 0

        mAppBarLayout.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                mAppBarLayout.viewTreeObserver.removeOnPreDrawListener(this)
                planDetailPresenter.notifyPreDrawingAppBar(mAppBarLayout.totalScrollRange)
                return false
            }
        })

        mAppBarLayout.addOnOffsetChangedListener { _, verticalOffset -> planDetailPresenter.notifyAppBarScrolled(verticalOffset) }

        onContentChanged(formattedPlan.content)
        onStarStatusChanged(formattedPlan.isStarred)
        onTypeOfPlanChanged(formattedType)
        onDeadlineChanged(formattedPlan.deadline)
        onReminderTimeChanged(formattedPlan.reminderTime)
        onPlanStatusChanged(formattedPlan.isCompleted)
    }

    override fun onAppBarScrolled(headerLayoutAlpha: Float) {
        mHeaderLayout.alpha = headerLayoutAlpha
    }

    override fun onAppBarScrolledToCriticalPoint(toolbarTitle: String) {
        toolbar.title = toolbarTitle
    }

    override fun showPlanDeletionDialog(content: String) {
        MessageDialogFragment.Builder()
                .setMessage("${getString(R.string.msg_dialog_delete_plan_pt1)}\n$content")
                .setTitle(R.string.title_dialog_delete_plan)
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.delete, object : BaseDialogFragment.OnButtonClickListener {
                    override fun onClick(): Boolean {
                        planDetailPresenter.notifyPlanDeleted()
                        return true
                    }
                })
                .show(supportFragmentManager)
    }

    override fun onPlanStatusChanged(isCompleted: Boolean) {
        mReminderItem.isClickable = !isCompleted
        mReminderItem.alpha = if (isCompleted) 0.6f else 1f
        switchPlanStatusButton.setText(if (isCompleted) R.string.text_make_plan_uc else R.string.text_make_plan_c)
    }

    override fun showContentEditorDialog(content: String) {
        EditorDialogFragment.Builder()
                .setEditorText(content)
                .setEditorHint(R.string.hint_content_editor_edit)
                .setPositiveButton(R.string.button_ok, object : EditorDialogFragment.OnTextEditedListener {
                    override fun onTextEdited(text: String) {
                        planDetailPresenter.notifyContentChanged(text)
                    }
                })
                .setTitle(R.string.title_dialog_content_editor)
                .setNegativeButton(R.string.button_cancel, null)
                .show(supportFragmentManager)
    }

    override fun onContentChanged(newContent: String) {
        contentText.text = newContent
    }

    override fun onStarStatusChanged(isStarred: Boolean) {
        mStarFab.setImageResource(if (isStarred) R.drawable.ic_star_black_24dp else R.drawable.ic_star_border_black_24dp)
        mStarFab.imageTintList = ColorStateList.valueOf(if (isStarred) mAccentColor else mGrey600Color)
    }

    override fun onTypeOfPlanChanged(formattedType: FormattedType) {
        val typeMarkColorInt = formattedType.typeMarkColorInt
        val headerColorInt = ColorUtil.reduceSaturation(typeMarkColorInt, 0.85f)
        window.navigationBarColor = typeMarkColorInt
        mCollapsingToolbarLayout.setContentScrimColor(headerColorInt)
        mCollapsingToolbarLayout.setStatusBarScrimColor(typeMarkColorInt)
        mHeaderBackground.setImageDrawable(ColorDrawable(headerColorInt))
        mTypeMarkIcon.setFillColor(typeMarkColorInt)
        mTypeMarkIcon.setInnerIcon(if (formattedType.hasTypeMarkPattern) getDrawable(formattedType.typeMarkPatternResId) else null)
        mTypeMarkIcon.setInnerText(formattedType.firstChar)
        mTypeNameText.text = formattedType.typeName
        mTypeItem.setDescriptionText(formattedType.typeName)
        mTypeItem.setThemeColor(typeMarkColorInt)
        mDeadlineItem.setThemeColor(typeMarkColorInt)
        mReminderItem.setThemeColor(typeMarkColorInt)
    }

    override fun showTypePickerDialog(defaultTypeListPos: Int) {
        TypePickerDialogFragment.newInstance(
                defaultTypeListPos,
                object : TypePickerDialogFragment.OnTypePickedListener {
                    override fun onTypePicked(position: Int) {
                        planDetailPresenter.notifyTypeOfPlanChanged(position)
                    }
                }
        ).show(supportFragmentManager)
    }

    override fun showDeadlinePickerDialog(defaultDeadline: Long) {
        DateTimePickerDialogFragment.newInstance(
                defaultDeadline,
                object : DateTimePickerDialogFragment.OnDateTimePickedListener {
                    override fun onDateTimePicked(timeInMillis: Long) {
                        planDetailPresenter.notifyDeadlineChanged(timeInMillis)
                    }
                }
        ).show(supportFragmentManager)
    }

    override fun showReminderTimePickerDialog(defaultReminderTime: Long) {
        DateTimePickerDialogFragment.newInstance(
                defaultReminderTime,
                object : DateTimePickerDialogFragment.OnDateTimePickedListener {
                    override fun onDateTimePicked(timeInMillis: Long) {
                        planDetailPresenter.notifyReminderTimeChanged(timeInMillis)
                    }
                }
        ).show(supportFragmentManager)
    }

    override fun onDeadlineChanged(newDeadline: CharSequence) {
        mDeadlineItem.setDescriptionText(newDeadline)
    }

    override fun onReminderTimeChanged(newReminderTime: CharSequence) {
        mReminderItem.setDescriptionText(newReminderTime)
    }

    override fun backToTop() {
        mAppBarLayout.setExpanded(true)
    }

    override fun pressBack() {
        super.onBackPressed()
    }

    @OnClick(R.id.item_type, R.id.item_deadline, R.id.item_reminder, R.id.fab_star, R.id.btn_switch_plan_status)
    fun onClick(view: View) {
        when (view.id) {
            R.id.item_type -> planDetailPresenter.notifySettingTypeOfPlan()
            R.id.item_deadline -> planDetailPresenter.notifySettingDeadline()
            R.id.item_reminder -> planDetailPresenter.notifySettingReminder()
            R.id.fab_star -> planDetailPresenter.notifyStarStatusChanged()
            R.id.btn_switch_plan_status -> planDetailPresenter.notifyPlanStatusChanged()
        }
    }
}
