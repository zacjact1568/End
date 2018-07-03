package net.zackzhang.app.end.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_about.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.common.Constant
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.util.StringUtil
import net.zackzhang.app.end.util.SystemUtil
import net.zackzhang.app.end.view.dialog.MessageDialogFragment

class AboutFragment : BaseFragment() {

    companion object {

        private const val TAG_NO_EMAIL_APP_FOUND = "no_email_app_found"
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
                        if (intent.resolveActivity(context!!.packageManager) != null) {
                            startActivity(intent)
                        } else {
                            MessageDialogFragment.Builder()
                                    .setTitle(R.string.title_dialog_no_email_app_found)
                                    .setMessage(R.string.msg_dialog_no_email_app_found)
                                    .setOkButtonText(android.R.string.ok)
                                    .setThirdButtonText(R.string.btn_dialog_copy_to_clipboard)
                                    .showCancelButton()
                                    .show(childFragmentManager, TAG_NO_EMAIL_APP_FOUND)
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
                                "market://details?id=" + context!!.packageName,
                                activity!!,
                                getString(R.string.toast_no_store_found)
                        )
                    }
                })
        )
        text_dscpt_rate.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)

        if (childFragment.tag == TAG_NO_EMAIL_APP_FOUND) {
            (childFragment as MessageDialogFragment).okButtonClickListener = {
                SystemUtil.putTextToClipboard(ResourceUtil.getString(R.string.label_developer_email), Constant.DEVELOPER_EMAIL)
                showToast(R.string.toast_copy_to_clipboard_successfully)
            }
        }
    }
}
