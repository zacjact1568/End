package net.zackzhang.app.end.view.activity

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MenuItem
import android.view.View
import android.view.Window
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_type_edit.*
import kotlinx.android.synthetic.main.content_type_edit.*
import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import net.zackzhang.app.end.common.Constant
import net.zackzhang.app.end.injector.component.DaggerTypeEditComponent
import net.zackzhang.app.end.injector.module.TypeEditPresenterModule
import net.zackzhang.app.end.model.bean.FormattedType
import net.zackzhang.app.end.presenter.TypeEditPresenter
import net.zackzhang.app.end.util.ColorUtil
import net.zackzhang.app.end.view.contract.TypeEditViewContract
import net.zackzhang.app.end.view.dialog.EditorDialogFragment
import net.zackzhang.app.end.view.dialog.TypeMarkColorPickerDialogFragment
import net.zackzhang.app.end.view.dialog.TypeMarkPatternPickerDialogFragment
import javax.inject.Inject

class TypeEditActivity : BaseActivity(), TypeEditViewContract {

    companion object {

        private const val TAG_TYPE_NAME_EDITOR = "type_name_editor"
        private const val TAG_TYPE_MARK_COLOR_PICKER = "type_mark_color_picker"
        private const val TAG_TYPE_MARK_PATTERN_PICKER = "type_mark_pattern_picker"

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
    lateinit var typeEditPresenter: TypeEditPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        typeEditPresenter.attach()
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
        typeEditPresenter.detach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        when (fragment.tag) {
            TAG_TYPE_NAME_EDITOR -> (fragment as EditorDialogFragment).editedListener = {
                // TODO 该返回值是默认的，分情况处理返回值，下同
                typeEditPresenter.notifyUpdatingTypeName(it)
                true
            }
            TAG_TYPE_MARK_COLOR_PICKER -> (fragment as TypeMarkColorPickerDialogFragment).typeMarkColorPickedListener = {
                typeEditPresenter.notifyTypeMarkColorSelected(it)
                true
            }
            TAG_TYPE_MARK_PATTERN_PICKER -> (fragment as TypeMarkPatternPickerDialogFragment).typeMarkPatternPickedListener = {
                typeEditPresenter.notifyTypeMarkPatternSelected(it)
                true
            }
        }
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
        EditorDialogFragment.Builder()
                .setTitle(R.string.title_dialog_type_name_editor)
                .setDefaultEditorText(originalEditorText)
                .setEditorHint(R.string.hint_type_name_editor_edit)
                .setEditButtonText(android.R.string.ok)
                .show(supportFragmentManager, TAG_TYPE_NAME_EDITOR)
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
        TypeMarkColorPickerDialogFragment.newInstance(defaultColor).show(supportFragmentManager, TAG_TYPE_MARK_COLOR_PICKER)
    }

    override fun showTypeMarkPatternPickerDialog(defaultPattern: String?) {
        TypeMarkPatternPickerDialogFragment.newInstance(defaultPattern).show(supportFragmentManager, TAG_TYPE_MARK_PATTERN_PICKER)
    }

    @OnClick(R.id.item_type_name, R.id.item_type_mark_color, R.id.item_type_mark_pattern)
    fun onClick(view: View) {
        when (view.id) {
            R.id.item_type_name -> typeEditPresenter.notifySettingTypeName()
            R.id.item_type_mark_color -> typeEditPresenter.notifySettingTypeMarkColor()
            R.id.item_type_mark_pattern -> typeEditPresenter.notifySettingTypeMarkPattern()
        }
    }
}
