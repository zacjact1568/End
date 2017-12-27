package me.imzack.app.end.view.activity

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_type_edit.*
import kotlinx.android.synthetic.main.content_type_edit.*
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

        setSupportActionBar(toolbar)
        setupActionBar()

        onTypeNameChanged(formattedType.typeName, formattedType.firstChar)
        onTypeMarkColorChanged(formattedType.typeMarkColorInt, formattedType.typeMarkColorName!!)
        onTypeMarkPatternChanged(formattedType.hasTypeMarkPattern, formattedType.typeMarkPatternResId, formattedType.typeMarkPatternName)
    }

    override fun showTypeNameEditorDialog(originalEditorText: String) {
        EditorDialogFragment.newInstance(
                getString(android.R.string.ok),
                {
                    //TODO 处理返回值
                    it?.let { mTypeEditPresenter.notifyUpdatingTypeName(it) }
                    true
                },
                originalEditorText,
                getString(R.string.hint_type_name_editor_edit),
                getString(R.string.title_dialog_type_name_editor)
        ).show(supportFragmentManager)
    }

    override fun onTypeNameChanged(typeName: String, firstChar: String) {
        ic_type_mark.setInnerText(firstChar)
        text_type_name.text = typeName
        item_type_name.setDescriptionText(typeName)
    }

    override fun onTypeMarkColorChanged(colorInt: Int, colorName: String) {
        window.navigationBarColor = colorInt
        window.statusBarColor = colorInt
        toolbar.setBackgroundColor(ColorUtil.reduceSaturation(colorInt, 0.85f))
        ic_type_mark.setFillColor(colorInt)
        item_type_name.setThemeColor(colorInt)
        item_type_mark_color.setDescriptionText(colorName)
        item_type_mark_color.setThemeColor(colorInt)
        item_type_mark_pattern.setThemeColor(colorInt)
    }

    override fun onTypeMarkPatternChanged(hasPattern: Boolean, patternResId: Int, patternName: String?) {
        ic_type_mark.setInnerIcon(if (hasPattern) getDrawable(patternResId) else null)
        item_type_mark_pattern.setDescriptionText(if (hasPattern) patternName else getString(R.string.dscpt_unsettled))
    }

    override fun showTypeMarkColorPickerDialog(defaultColor: Int) {
        TypeMarkColorPickerDialogFragment.newInstance(
                defaultColor,
                //TODO 处理返回值
                {
                    mTypeEditPresenter.notifyTypeMarkColorSelected(it)
                    true
                }
        ).show(supportFragmentManager)
    }

    override fun showTypeMarkPatternPickerDialog(defaultPattern: String?) {
        TypeMarkPatternPickerDialogFragment.newInstance(
                defaultPattern,
                //TODO 处理返回值
                {
                    mTypeEditPresenter.notifyTypeMarkPatternSelected(it)
                    true
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
