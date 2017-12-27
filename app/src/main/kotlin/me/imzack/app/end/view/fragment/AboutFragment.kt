package me.imzack.app.end.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_about.*
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.StringUtil
import me.imzack.app.end.util.SystemUtil
import me.imzack.app.end.view.dialog.MessageDialogFragment

class AboutFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_about, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_dscpt_developer.text = StringUtil.addSpan(
                getString(R.string.text_developer),
                arrayOf(getString(R.string.text_span_seg_contact)),
                intArrayOf(StringUtil.SPAN_CLICKABLE),
                arrayOf<Any>(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val intent = Intent(Intent.ACTION_SENDTO)
                        intent.data = Uri.parse("mailto:")
                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(Constant.DEVELOPER_EMAIL))
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_email))
                        if (intent.resolveActivity(context.packageManager) != null) {
                            startActivity(intent)
                        } else {
                            MessageDialogFragment.newInstance(
                                    getString(R.string.msg_dialog_no_email_app_found),
                                    getString(R.string.title_dialog_no_email_app_found),
                                    getString(android.R.string.ok),
                                    null,
                                    getString(R.string.btn_dialog_copy_to_clipboard),
                                    {
                                        SystemUtil.putTextToClipboard(ResourceUtil.getString(R.string.label_developer_email), Constant.DEVELOPER_EMAIL)
                                        showToast(R.string.toast_copy_to_clipboard_successfully)
                                    }
                            ).show(fragmentManager)
                        }
                    }
                })
        )
        text_dscpt_developer.movementMethod = LinkMovementMethod.getInstance()

        text_dscpt_rate.text = StringUtil.addSpan(
                getString(R.string.text_rate),
                arrayOf(getString(R.string.text_span_seg_rate)),
                intArrayOf(StringUtil.SPAN_CLICKABLE),
                arrayOf<Any>(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        SystemUtil.openLink(
                                "market://details?id=" + context.packageName,
                                activity,
                                getString(R.string.toast_no_store_found)
                        )
                    }
                })
        )
        text_dscpt_rate.movementMethod = LinkMovementMethod.getInstance()
    }
}
