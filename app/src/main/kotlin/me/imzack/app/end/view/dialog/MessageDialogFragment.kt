package me.imzack.app.end.view.dialog

import android.os.Bundle
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_fragment_message.*
import me.imzack.app.end.R
import me.imzack.app.end.util.ResourceUtil

class MessageDialogFragment : BaseDialogFragment() {

    companion object {

        private val ARG_MESSAGE = "message"
    }

    private var mMessage: CharSequence? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        if (args != null) {
            mMessage = args.getCharSequence(ARG_MESSAGE)
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_message, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_message.text = mMessage
    }

    class Builder : BaseDialogFragment.Builder<MessageDialogFragment>() {

        private var mMessage: CharSequence? = null

        fun setMessage(message: CharSequence): Builder {
            mMessage = message
            return this
        }

        fun setMessage(@StringRes resId: Int) = setMessage(ResourceUtil.getString(resId))

        override fun onBuildContent(): MessageDialogFragment {
            val fragment = MessageDialogFragment()
            val args = Bundle()
            args.putCharSequence(ARG_MESSAGE, mMessage)
            fragment.arguments = args
            return fragment
        }
    }
}
