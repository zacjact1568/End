package me.imzack.app.end.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.imzack.app.end.R;
import me.imzack.app.end.common.Constant;
import me.imzack.app.end.util.ResourceUtil;
import me.imzack.app.end.util.StringUtil;
import me.imzack.app.end.util.SystemUtil;
import me.imzack.app.end.view.dialog.BaseDialogFragment;
import me.imzack.app.end.view.dialog.MessageDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutFragment extends BaseFragment {

    @BindView(R.id.text_dscpt_developer)
    TextView mDeveloperDscptText;
    @BindView(R.id.text_dscpt_rate)
    TextView mRateDscptText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mDeveloperDscptText.setText(StringUtil.addSpan(
                getString(R.string.text_developer),
                new String[]{getString(R.string.text_span_seg_contact)},
                new int[]{StringUtil.SPAN_CLICKABLE},
                new Object[]{new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constant.DEVELOPER_EMAIL});
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_email));
                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            new MessageDialogFragment.Builder()
                                    .setMessage(R.string.msg_dialog_no_email_app_found)
                                    .setTitle(R.string.title_dialog_no_email_app_found)
                                    .setNeutralButton(R.string.btn_dialog_copy_to_clipboard, new BaseDialogFragment.OnButtonClickListener() {
                                        @Override
                                        public boolean onClick() {
                                            SystemUtil.putTextToClipboard(ResourceUtil.getString(R.string.label_developer_email), Constant.DEVELOPER_EMAIL);
                                            showToast(R.string.toast_copy_to_clipboard_successfully);
                                            return true;
                                        }
                                    })
                                    .setPositiveButton(R.string.button_ok, null)
                                    .show(getFragmentManager());
                        }
                    }
                }}
        ));
        mDeveloperDscptText.setMovementMethod(LinkMovementMethod.getInstance());

        mRateDscptText.setText(StringUtil.addSpan(
                getString(R.string.text_rate),
                new String[]{getString(R.string.text_span_seg_rate)},
                new int[]{StringUtil.SPAN_CLICKABLE},
                new Object[]{new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        SystemUtil.openLink(
                                "market://details?id=" + getContext().getPackageName(),
                                getActivity(),
                                getString(R.string.toast_no_store_found)
                        );
                    }
                }}
        ));
        mRateDscptText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
