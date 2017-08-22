package me.imzack.app.end.view.activity

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.TextView
import butterknife.BindString
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.injector.component.DaggerTypeEditComponent
import me.imzack.app.end.injector.module.TypeEditPresenterModule
import me.imzack.app.end.model.bean.FormattedType
import me.imzack.app.end.model.bean.TypeMarkColor
import me.imzack.app.end.model.bean.TypeMarkPattern
import me.imzack.app.end.presenter.TypeEditPresenter
import me.imzack.app.end.util.ColorUtil
import me.imzack.app.end.view.contract.TypeEditViewContract
import me.imzack.app.end.view.dialog.EditorDialogFragment
import me.imzack.app.end.view.dialog.TypeMarkColorPickerDialogFragment
import me.imzack.app.end.view.dialog.TypeMarkPatternPickerDialogFragment
import me.imzack.app.end.view.widget.CircleColorView
import me.imzack.app.end.view.widget.ItemView
import javax.inject.Inject

class TypeEditActivity : BaseActivity(), TypeEditViewContract {

    companion object {

        fun start(activity: Activity, typeListPosition: Int, enableTransition: Boolean, sharedElement: View, transitionName: String) {
            val intent = Intent(activity, TypeEditActivity::class.java)
            intent.putExtra(Constant.TYPE_LIST_POSITION, typeListPosition)
            intent.putExtra(Constant.ENABLE_TRANSITION, enableTransition)
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

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.ic_type_mark)
    lateinit var mTypeMarkIcon: CircleColorView
    @BindView(R.id.text_type_name)
    lateinit var mTypeNameText: TextView
    @BindView(R.id.item_type_name)
    lateinit var mTypeNameItem: ItemView
    @BindView(R.id.item_type_mark_color)
    lateinit var mTypeMarkColorItem: ItemView
    @BindView(R.id.item_type_mark_pattern)
    lateinit var mTypeMarkPatternItem: ItemView

    @BindString(R.string.dscpt_unsettled)
    lateinit var mUnsettledDscpt: String

    @Inject
    lateinit var mTypeEditPresenter: TypeEditPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTypeEditPresenter.attach()
    }

    override fun onInjectPresenter() {
        DaggerTypeEditComponent.builder()
                .typeEditPresenterModule(TypeEditPresenterModule(
                        this,
                        intent.getIntExtra(Constant.TYPE_LIST_POSITION, -1)
                ))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mTypeEditPresenter.detach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showInitialView(formattedType: FormattedType) {
        if (intent.getBooleanExtra(Constant.ENABLE_TRANSITION, false)) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }

        setContentView(R.layout.activity_type_edit)
        ButterKnife.bind(this)

        setSupportActionBar(mToolbar)
        setupActionBar()

        onTypeNameChanged(formattedType.typeName, formattedType.firstChar)
        onTypeMarkColorChanged(formattedType.typeMarkColorInt, formattedType.typeMarkColorName!!)
        onTypeMarkPatternChanged(formattedType.hasTypeMarkPattern, formattedType.typeMarkPatternResId, formattedType.typeMarkPatternName)
    }

    override fun showTypeNameEditorDialog(originalEditorText: String) {
        EditorDialogFragment.Builder()
                .setEditorText(originalEditorText)
                .setEditorHint(R.string.hint_type_name_editor_edit)
                .setPositiveButton(R.string.button_ok, object : EditorDialogFragment.OnTextEditedListener {
                    override fun onTextEdited(text: String) {
                        mTypeEditPresenter.notifyUpdatingTypeName(text)
                    }
                })
                .setTitle(R.string.title_dialog_type_name_editor)
                .setNegativeButton(R.string.button_cancel, null)
                .show(supportFragmentManager)
    }

    override fun onTypeNameChanged(typeName: String, firstChar: String) {
        mTypeMarkIcon.setInnerText(firstChar)
        mTypeNameText.text = typeName
        mTypeNameItem.setDescriptionText(typeName)
    }

    override fun onTypeMarkColorChanged(colorInt: Int, colorName: String) {
        window.navigationBarColor = colorInt
        window.statusBarColor = colorInt
        mToolbar.setBackgroundColor(ColorUtil.reduceSaturation(colorInt, 0.85f))
        mTypeMarkIcon.setFillColor(colorInt)
        mTypeNameItem.setThemeColor(colorInt)
        mTypeMarkColorItem.setDescriptionText(colorName)
        mTypeMarkColorItem.setThemeColor(colorInt)
        mTypeMarkPatternItem.setThemeColor(colorInt)
    }

    override fun onTypeMarkPatternChanged(hasPattern: Boolean, patternResId: Int, patternName: String?) {
        mTypeMarkIcon.setInnerIcon(if (hasPattern) getDrawable(patternResId) else null)
        mTypeMarkPatternItem.setDescriptionText(if (hasPattern) patternName else mUnsettledDscpt)
    }

    override fun showTypeMarkColorPickerDialog(defaultColor: String) {
        TypeMarkColorPickerDialogFragment.newInstance(
                defaultColor,
                object : TypeMarkColorPickerDialogFragment.OnTypeMarkColorPickedListener {
                    override fun onTypeMarkColorPicked(typeMarkColor: TypeMarkColor) {
                        mTypeEditPresenter.notifyTypeMarkColorSelected(typeMarkColor)
                    }
                }
        ).show(supportFragmentManager)
    }

    override fun showTypeMarkPatternPickerDialog(defaultPattern: String?) {
        TypeMarkPatternPickerDialogFragment.newInstance(
                defaultPattern,
                object : TypeMarkPatternPickerDialogFragment.OnTypeMarkPatternPickedListener {
                    override fun onTypeMarkPatternPicked(typeMarkPattern: TypeMarkPattern?) {
                        mTypeEditPresenter.notifyTypeMarkPatternSelected(typeMarkPattern)
                    }
                }
        ).show(supportFragmentManager)
    }

    @OnClick(R.id.item_type_name, R.id.item_type_mark_color, R.id.item_type_mark_pattern)
    fun onClick(view: View) {
        when (view.id) {
            R.id.item_type_name -> mTypeEditPresenter.notifySettingTypeName()
            R.id.item_type_mark_color -> mTypeEditPresenter.notifySettingTypeMarkColor()
            R.id.item_type_mark_pattern -> mTypeEditPresenter.notifySettingTypeMarkPattern()
        }
    }
}
