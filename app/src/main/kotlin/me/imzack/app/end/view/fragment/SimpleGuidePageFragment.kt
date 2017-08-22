package me.imzack.app.end.view.fragment

import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
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

    @BindView(R.id.image_subject)
    lateinit var mSubjectImage: ImageView
    @BindView(R.id.text_title)
    lateinit var mTitleText: TextView
    @BindView(R.id.text_dscpt)
    lateinit var mDscptText: TextView
    @BindView(R.id.btn_action)
    lateinit var mActionButton: Button

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
        ButterKnife.bind(this, view)

        if (mImageResId == 0) {
            mSubjectImage.visibility = View.INVISIBLE
        } else {
            mSubjectImage.setImageResource(mImageResId)
        }
        if (mTitleResId == 0) {
            mTitleText.visibility = View.INVISIBLE
        } else {
            mTitleText.setText(mTitleResId)
        }
        if (mDscptResId == 0) {
            mDscptText.visibility = View.INVISIBLE
        } else {
            mDscptText.setText(mDscptResId)
        }
        if (mBtnTextResId == 0) {
            mActionButton.visibility = View.INVISIBLE
        } else {
            mActionButton.setText(mBtnTextResId)
        }
        mActionButton.setOnClickListener(mBtnClkLsnr)
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
