package me.imzack.app.end.view.fragment

import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_simple_guide_page.*
import me.imzack.app.end.R
import java.io.Serializable

class SimpleGuidePageFragment : BaseFragment() {

    companion object {

        private val ARG_IMAGE_RES_ID = "0"
        private val ARG_TITLE_RES_ID = "1"
        private val ARG_DSCPT_RES_ID = "2"
        private val ARG_BTN_TEXT_RES_ID = "3"
        private val ARG_BTN_CLK_LSNR = "4"

        fun newInstance(@DrawableRes imageResId: Int, @StringRes titleResId: Int,
                        @StringRes descriptionResId: Int, @StringRes buttonTextResId: Int,
                        onButtonClickListener: OnButtonClickListener?): SimpleGuidePageFragment {
            val fragment = SimpleGuidePageFragment()
            val args = Bundle()
            args.putInt(ARG_IMAGE_RES_ID, imageResId)
            args.putInt(ARG_TITLE_RES_ID, titleResId)
            args.putInt(ARG_DSCPT_RES_ID, descriptionResId)
            args.putInt(ARG_BTN_TEXT_RES_ID, buttonTextResId)
            args.putSerializable(ARG_BTN_CLK_LSNR, onButtonClickListener)
            fragment.arguments = args
            return fragment
        }
    }

    private var mImageResId = 0
    private var mTitleResId = 0
    private var mDscptResId = 0
    private var mBtnTextResId = 0
    private var mBtnClkLsnr: OnButtonClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        if (args != null) {
            mImageResId = args.getInt(ARG_IMAGE_RES_ID)
            mTitleResId = args.getInt(ARG_TITLE_RES_ID)
            mDscptResId = args.getInt(ARG_DSCPT_RES_ID)
            mBtnTextResId = args.getInt(ARG_BTN_TEXT_RES_ID)
            mBtnClkLsnr = args.getSerializable(ARG_BTN_CLK_LSNR) as OnButtonClickListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_simple_guide_page, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mImageResId == 0) {
            image_subject.visibility = View.INVISIBLE
        } else {
            image_subject.setImageResource(mImageResId)
        }
        if (mTitleResId == 0) {
            text_title.visibility = View.INVISIBLE
        } else {
            text_title.setText(mTitleResId)
        }
        if (mDscptResId == 0) {
            text_dscpt.visibility = View.INVISIBLE
        } else {
            text_dscpt.setText(mDscptResId)
        }
        if (mBtnTextResId == 0) {
            btn_action.visibility = View.INVISIBLE
        } else {
            btn_action.setText(mBtnTextResId)
        }
        btn_action.setOnClickListener(mBtnClkLsnr)
    }

    override fun onDetach() {
        super.onDetach()
    }

    class Builder {

        private var mImageResId = 0
        private var mTitleResId = 0
        private var mDscptResId = 0
        private var mBtnTextResId = 0
        private var mBtnClkLsnr: OnButtonClickListener? = null

        fun setImage(@DrawableRes resId: Int): Builder {
            mImageResId = resId
            return this
        }

        fun setTitle(@StringRes resId: Int): Builder {
            mTitleResId = resId
            return this
        }

        fun setDescription(@StringRes resId: Int): Builder {
            mDscptResId = resId
            return this
        }

        fun setButton(@StringRes resId: Int, listener: OnButtonClickListener?): Builder {
            mBtnTextResId = resId
            mBtnClkLsnr = listener
            return this
        }

        fun create() = newInstance(mImageResId, mTitleResId, mDscptResId, mBtnTextResId, mBtnClkLsnr)
    }

    interface OnButtonClickListener : View.OnClickListener, Serializable
}
