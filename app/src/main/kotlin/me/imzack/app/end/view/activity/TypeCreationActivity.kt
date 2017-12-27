package me.imzack.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_type_creation.*
import kotlinx.android.synthetic.main.content_type_creation.*
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.injector.component.DaggerTypeCreationComponent
import me.imzack.app.end.injector.module.TypeCreationPresenterModule
import me.imzack.app.end.model.bean.FormattedType
import me.imzack.app.end.model.bean.TypeMarkColor
import me.imzack.app.end.model.bean.TypeMarkPattern
import me.imzack.app.end.presenter.TypeCreationPresenter
import me.imzack.app.end.util.ColorUtil
import me.imzack.app.end.view.contract.TypeCreationViewContract
import me.imzack.app.end.view.dialog.EditorDialogFragment
import me.imzack.app.end.view.dialog.TypeMarkColorPickerDialogFragment
import me.imzack.app.end.view.dialog.TypeMarkPatternPickerDialogFragment
import javax.inject.Inject

class TypeCreationActivity : BaseActivity(), TypeCreationViewContract {

    companion object {

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
        EditorDialogFragment.newInstance(
                getString(android.R.string.ok),
                {
                    //TODO 处理返回值
                    it?.let { mTypeCreationPresenter.notifyTypeNameEdited(it) }
                    true
                },
                defaultName,
                getString(R.string.hint_type_name_editor_creation),
                getString(R.string.title_dialog_type_name_editor)
        ).show(supportFragmentManager)
    }

    override fun showTypeMarkColorPickerDialog(defaultColor: Int) {
        TypeMarkColorPickerDialogFragment.newInstance(
                defaultColor,
                {
                    //TODO 处理返回值
                    mTypeCreationPresenter.notifyTypeMarkColorSelected(it)
                    true
                }
        ).show(supportFragmentManager)
    }

    override fun showTypeMarkPatternPickerDialog(defaultPattern: String?) {
        TypeMarkPatternPickerDialogFragment.newInstance(
                defaultPattern,
                //TODO 处理返回值
                {
                    mTypeCreationPresenter.notifyTypeMarkPatternSelected(it)
                    true
                }
        ).show(supportFragmentManager)
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
