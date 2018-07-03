package net.zackzhang.app.end.view.dialog

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_fragment_message.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.util.ResourceUtil
import me.imzack.lib.basedialogfragment.BaseDialogFragment

// TODO 将此类封装进 BaseDialogFragment
class MessageDialogFragment : BaseDialogFragment() {

    companion object {

        private const val ARG_MESSAGE = "message"
    }

    var okButtonClickListener: (() -> Unit)? = null

    var thirdButtonClickListener: (() -> Unit)? = null

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_message, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_message.text = arguments!!.getCharSequence(ARG_MESSAGE)

        neutralButtonClickListener = {
            thirdButtonClickListener?.invoke()
            true
        }
        // TODO 应该可以不定义这个，默认就可以关闭（其他类里相同）
        negativeButtonClickListener = { true }
        positiveButtonClickListener = {
            okButtonClickListener?.invoke()
            true
        }
    }

    class Builder {

        private val args = Bundle()

        fun setTitle(title: CharSequence): Builder {
            putTitle(args, title)
            return this
        }

        fun setTitle(@StringRes titleResId: Int) = setTitle(ResourceUtil.getString(titleResId))

        fun setMessage(message: CharSequence): Builder {
            args.putCharSequence(ARG_MESSAGE, message)
            return this
        }

        fun setMessage(@StringRes messageResId: Int) = setMessage(ResourceUtil.getString(messageResId))

        fun setOkButtonText(okButtonText: CharSequence): Builder {
            putPositiveButtonText(args, okButtonText)
            return this
        }

        fun setOkButtonText(@StringRes okButtonTextResId: Int) = setOkButtonText(ResourceUtil.getString(okButtonTextResId))

        fun setThirdButtonText(thirdButtonText: CharSequence): Builder {
            putNeutralButtonText(args, thirdButtonText)
            return this
        }

        fun setThirdButtonText(@StringRes thirdButtonTextResId: Int) = setThirdButtonText(ResourceUtil.getString(thirdButtonTextResId))

        fun showCancelButton(): Builder {
            putNegativeButtonText(args, ResourceUtil.getString(android.R.string.cancel))
            return this
        }

        fun build(): MessageDialogFragment {
            val fragment = MessageDialogFragment()
            fragment.arguments = args
            return fragment
        }

        fun show(fm: FragmentManager, tag: String? = null) {
            build().show(fm, tag)
        }
    }
}
