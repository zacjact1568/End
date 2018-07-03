package net.zackzhang.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_type_creation.*
import kotlinx.android.synthetic.main.content_type_creation.*
import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import net.zackzhang.app.end.injector.component.DaggerTypeCreationComponent
import net.zackzhang.app.end.injector.module.TypeCreationPresenterModule
import net.zackzhang.app.end.model.bean.FormattedType
import net.zackzhang.app.end.presenter.TypeCreationPresenter
import net.zackzhang.app.end.util.ColorUtil
import net.zackzhang.app.end.view.contract.TypeCreationViewContract
import net.zackzhang.app.end.view.dialog.EditorDialogFragment
import net.zackzhang.app.end.view.dialog.TypeMarkColorPickerDialogFragment
import net.zackzhang.app.end.view.dialog.TypeMarkPatternPickerDialogFragment
import javax.inject.Inject

class TypeCreationActivity : BaseActivity(), TypeCreationViewContract {

    companion object {

        private const val TAG_TYPE_NAME_EDITOR = "type_name_editor"
        private const val TAG_TYPE_MARK_COLOR_PICKER = "type_mark_color_picker"
        private const val TAG_TYPE_MARK_PATTERN_PICKER = "type_mark_pattern_picker"

        fun start(context: Context) {
            context.startActivity(Intent(context, TypeCreationActivity::class.java))
        }
    }

    @Inject
    lateinit var mTypeCreationPresenter: TypeCreationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTypeCreationPresenter.attach()
    }

    override fun onInjectPresenter() {
        DaggerTypeCreationComponent.builder()
                .typeCreationPresenterModule(TypeCreationPresenterModule(this))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mTypeCreationPresenter.detach()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_type_creation, menu)
        //在这里改变图标的tint，因为没法在xml文件中改
        menu.findItem(R.id.action_create).icon.setTint(Color.WHITE)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> mTypeCreationPresenter.notifyCancelButtonClicked()
            R.id.action_create -> mTypeCreationPresenter.notifyCreateButtonClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        //super.onBackPressed();
        mTypeCreationPresenter.notifyCancelButtonClicked()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        when (fragment.tag) {
            TAG_TYPE_NAME_EDITOR -> (fragment as EditorDialogFragment).editedListener = {
                // TODO 该返回值是默认的，分情况处理返回值，下同
                mTypeCreationPresenter.notifyTypeNameEdited(it)
                true
            }
            TAG_TYPE_MARK_COLOR_PICKER -> (fragment as TypeMarkColorPickerDialogFragment).typeMarkColorPickedListener = {
                mTypeCreationPresenter.notifyTypeMarkColorSelected(it)
                true
            }
            TAG_TYPE_MARK_PATTERN_PICKER -> (fragment as TypeMarkPatternPickerDialogFragment).typeMarkPatternPickedListener = {
                mTypeCreationPresenter.notifyTypeMarkPatternSelected(it)
                true
            }
        }
    }

    override fun showInitialView(formattedType: FormattedType) {
        //        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_type_creation)
        ButterKnife.bind(this)

        //        //TODO if (savedInstanceState == null)
        //        mCircularRevealLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        //            @Override
        //            public boolean onPreDraw() {
        //                mCircularRevealLayout.getViewTreeObserver().removeOnPreDrawListener(this);
        //                playCircularRevealAnimation();
        //                return false;
        //            }
        //        });

        setSupportActionBar(toolbar)
        setupActionBar()

        onTypeNameChanged(formattedType.typeName, formattedType.firstChar)
        onTypeMarkColorChanged(formattedType.typeMarkColorInt, formattedType.typeMarkColorName!!)
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
        item_type_mark_pattern.setDescriptionText(if (hasPattern) patternName else getString(R.string.dscpt_touch_to_set))
    }

    override fun showTypeNameEditorDialog(defaultName: String) {
        EditorDialogFragment.Builder()
                .setTitle(R.string.title_dialog_type_name_editor)
                .setDefaultEditorText(defaultName)
                .setEditorHint(R.string.hint_type_name_editor_creation)
                .setEditButtonText(android.R.string.ok)
                .show(supportFragmentManager, TAG_TYPE_NAME_EDITOR)
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
            R.id.item_type_name -> mTypeCreationPresenter.notifySettingTypeName()
            R.id.item_type_mark_color -> mTypeCreationPresenter.notifySettingTypeMarkColor()
            R.id.item_type_mark_pattern -> mTypeCreationPresenter.notifySettingTypeMarkPattern()
        }
    }

    //    private void playCircularRevealAnimation() {
    //        int fabCoordinateInPx = (int) (44 * getResources().getDisplayMetrics().density + 0.5f);
    //        int centerX = mCircularRevealLayout.getWidth() - fabCoordinateInPx;
    //        int centerY = mCircularRevealLayout.getHeight() - fabCoordinateInPx;
    //
    //        Animator anim = ViewAnimationUtils.createCircularReveal(mCircularRevealLayout, centerX, centerY, 0, (float) Math.hypot(centerX, centerY));
    //        anim.setDuration(400);
    //        anim.addListener(new Animator.AnimatorListener() {
    //            @Override
    //            public void onAnimationStart(Animator animation) {
    //
    //            }
    //
    //            @Override
    //            public void onAnimationEnd(Animator animation) {
    //                CommonUtil.showSoftInput(mTypeNameEditor);
    //            }
    //
    //            @Override
    //            public void onAnimationCancel(Animator animation) {
    //
    //            }
    //
    //            @Override
    //            public void onAnimationRepeat(Animator animation) {
    //
    //            }
    //        });
    //        anim.start();
    //    }
}
