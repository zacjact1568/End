package me.imzack.app.end.view.dialog

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import me.imzack.app.end.R
import me.imzack.app.end.util.LogUtil
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.SystemUtil
import java.io.Serializable

abstract class BaseDialogFragment : DialogFragment() {

    companion object {

        val ARG_TITLE_STR = "0"
        val ARG_NEU_BTN_STR = "1"
        val ARG_NEU_BTN_CLK_LSNR = "2"
        val ARG_NEG_BTN_STR = "3"
        val ARG_NEG_BTN_CLK_LSNR = "4"
        val ARG_POS_BTN_STR = "5"
        val ARG_POS_BTN_CLK_LSNR = "6"
    }

    @BindView(R.id.text_title)
    lateinit var mTitleText: TextView
    @BindView(R.id.btn_neutral)
    lateinit var mNeutralButton: Button
    @BindView(R.id.btn_negative)
    lateinit var mNegativeButton: Button
    @BindView(R.id.btn_positive)
    lateinit var mPositiveButton: Button

    // 传入null表示不显示标题，下同
    //TODO 这里应该将传入的字段放进Arguments，fragment重建后才能恢复新的？
    var mTitleString: String? = null
    var mNeutralButtonString: String? = null
    var mNegativeButtonString: String? = null
    var mPositiveButtonString: String? = null
    var mNeutralButtonClickListener: OnButtonClickListener? = null
    var mNegativeButtonClickListener: OnButtonClickListener? = null
    var mPositiveButtonClickListener: OnButtonClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        if (args != null) {
            mTitleString = args.getString(ARG_TITLE_STR)
            mNeutralButtonString = args.getString(ARG_NEU_BTN_STR)
            // OnButtonClickListener不能为非空类型，因为可空类型不能转换成非空类型
            mNeutralButtonClickListener = args.getSerializable(ARG_NEU_BTN_CLK_LSNR) as OnButtonClickListener?
            mNegativeButtonString = args.getString(ARG_NEG_BTN_STR)
            mNegativeButtonClickListener = args.getSerializable(ARG_NEG_BTN_CLK_LSNR) as OnButtonClickListener?
            mPositiveButtonString = args.getString(ARG_POS_BTN_STR)
            mPositiveButtonClickListener = args.getSerializable(ARG_POS_BTN_CLK_LSNR) as OnButtonClickListener?
        }
    }

    /** 重写这个方法提供内容区域的view  */
    abstract fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup): View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.dialog_fragment_base, container, false) as ViewGroup
        root.addView(onCreateContentView(inflater, root), 1)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        //通过动态设置内容区域的view宽度来设置dialog宽度（不能直接设置根view的宽度，因为它的LayoutParams为null）
        (view as ViewGroup).getChildAt(1).layoutParams.width = (SystemUtil.displayWidth * 0.8f).toInt()

        updateTitle()
        updateNeutralButtonString()
        updateNegativeButtonString()
        updatePositiveButtonString()
    }

    override fun onDetach() {
        super.onDetach()
        mNeutralButtonClickListener = null
        mNegativeButtonClickListener = null
        mPositiveButtonClickListener = null
    }

    @OnClick(R.id.btn_neutral, R.id.btn_negative, R.id.btn_positive)
    fun onClickBase(view: View) {
        if (when (view.id) {
            R.id.btn_neutral -> mNeutralButtonClickListener?.onClick() != false
            R.id.btn_negative -> mNegativeButtonClickListener?.onClick() != false
            R.id.btn_positive -> mPositiveButtonClickListener?.onClick() != false
            else -> true
        }) {
            dialog.dismiss()
        }
    }

    fun updateTitle() {
        mTitleText.visibility = if (mTitleString == null) View.GONE else View.VISIBLE
        mTitleText.text = mTitleString
    }

    fun updateNeutralButtonString() {
        mNeutralButton.visibility = if (mNeutralButtonString == null) View.GONE else View.VISIBLE
        mNeutralButton.text = mNeutralButtonString
    }

    fun updateNegativeButtonString() {
        mNegativeButton.visibility = if (mNegativeButtonString == null) View.GONE else View.VISIBLE
        mNegativeButton.text = mNegativeButtonString
    }

    fun updatePositiveButtonString() {
        mPositiveButton.visibility = if (mPositiveButtonString == null) View.GONE else View.VISIBLE
        mPositiveButton.text = mPositiveButtonString
    }

    fun show(manager: FragmentManager) {
        super.show(manager, null)
    }

    abstract class Builder<out DF : BaseDialogFragment> {

        private var mTitleStr: String? = null
        private var mNeuBtnStr: String? = null
        private var mNegBtnStr: String? = null
        private var mPosBtnStr: String? = null
        private var mNeuBtnClkLsnr: OnButtonClickListener? = null
        private var mNegBtnClkLsnr: OnButtonClickListener? = null
        private var mPosBtnClkLsnr: OnButtonClickListener? = null

        fun setTitle(title: String): Builder<*> {
            mTitleStr = title
            return this
        }

        fun setTitle(@StringRes resId: Int) = setTitle(ResourceUtil.getString(resId))

        // 若listener为null，点击此按钮直接关闭dialog
        fun setNeutralButton(text: String, listener: OnButtonClickListener?): Builder<*> {
            mNeuBtnStr = text
            mNeuBtnClkLsnr = listener
            return this
        }

        fun setNeutralButton(@StringRes resId: Int, listener: OnButtonClickListener?) =
                setNeutralButton(ResourceUtil.getString(resId), listener)

        fun setNegativeButton(text: String, listener: OnButtonClickListener?): Builder<*> {
            mNegBtnStr = text
            mNegBtnClkLsnr = listener
            return this
        }

        fun setNegativeButton(@StringRes resId: Int, listener: OnButtonClickListener?) =
                setNegativeButton(ResourceUtil.getString(resId), listener)

        fun setPositiveButton(text: String, listener: OnButtonClickListener?): Builder<*> {
            mPosBtnStr = text
            mPosBtnClkLsnr = listener
            return this
        }

        fun setPositiveButton(@StringRes resId: Int, listener: OnButtonClickListener?) =
                setPositiveButton(ResourceUtil.getString(resId), listener)

        /** 重写此方法提供子类DialogFragment  */
        protected abstract fun onBuildContent(): DF

        /** 仅创建DialogFragment，不附到activity上  */
        fun build(): DF {
            val dialogFragment = onBuildContent()
            val args = dialogFragment.arguments
            args.putString(ARG_TITLE_STR, mTitleStr)
            args.putString(ARG_NEU_BTN_STR, mNeuBtnStr)
            args.putSerializable(ARG_NEU_BTN_CLK_LSNR, mNeuBtnClkLsnr)
            args.putString(ARG_NEG_BTN_STR, mNegBtnStr)
            args.putSerializable(ARG_NEG_BTN_CLK_LSNR, mNegBtnClkLsnr)
            args.putString(ARG_POS_BTN_STR, mPosBtnStr)
            args.putSerializable(ARG_POS_BTN_CLK_LSNR, mPosBtnClkLsnr)
            return dialogFragment
        }

        /** 创建DialogFragment并将其附到activity上  */
        fun show(manager: FragmentManager) {
            build().show(manager)
        }
    }

    // 继承了Serializable，没法写成函数类型
    interface OnButtonClickListener : Serializable {
        /** 按钮按下时调用，返回值表示是否关闭dialog  */
        fun onClick(): Boolean
    }
}
