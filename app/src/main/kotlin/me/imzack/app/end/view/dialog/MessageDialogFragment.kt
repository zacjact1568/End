package me.imzack.app.end.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_fragment_message.*
import me.imzack.app.end.R
import me.imzack.app.end.util.ResourceUtil
import me.imzack.lib.basedialogfragment.BaseDialogFragment

class MessageDialogFragment : BaseDialogFragment() {

    companion object {

        private val ARG_DEFAULT_MESSAGE = "default_message"

        fun newInstance(
                defaultMessageText: CharSequence,
                titleText: String? = null,
                okButtonText: String? = null,
                okButtonClickListener: (() -> Unit)? = null,
                thirdButtonText: String? = null,
                thirdButtonClickListener: (() -> Unit)? = null
        ): MessageDialogFragment {
            val fragment = MessageDialogFragment()
            val args = Bundle()
            putBaseArguments(
                    args,
                    titleText,
                    thirdButtonText,
                    object : OnButtonClickListener {
                        override fun onClick(): Boolean {
                            thirdButtonClickListener?.invoke()
                            return true
                        }
                    },
                    ResourceUtil.getString(android.R.string.cancel),
                    object : OnButtonClickListener {
                        override fun onClick() = true
                    },
                    okButtonText,
                    object : OnButtonClickListener {
                        override fun onClick(): Boolean {
                            okButtonClickListener?.invoke()
                            return true
                        }
                    }
            )
            args.putCharSequence(ARG_DEFAULT_MESSAGE, defaultMessageText)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_message, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_message.text = arguments.getCharSequence(ARG_DEFAULT_MESSAGE)
    }
}
