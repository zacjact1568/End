package com.zack.enderplan.view.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.StringUtil;
import com.zack.enderplan.util.SystemUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutFragment extends BaseFragment {

    @BindView(R.id.text_dscpt_ender_series)
    TextView mEnderSeriesDscptText;
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

        mEnderSeriesDscptText.setText(StringUtil.addSpan(
                getString(R.string.text_ender_series),
                new String[]{getString(R.string.text_span_seg_enderman), getString(R.string.text_span_seg_minecraft)},
                new int[]{StringUtil.SPAN_URL, StringUtil.SPAN_URL},
                new Object[]{getString(R.string.text_span_seg_enderman_url), "https://minecraft.net/"}
        ));
        mEnderSeriesDscptText.setMovementMethod(LinkMovementMethod.getInstance());

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
                            new AlertDialog.Builder(getContext())
                                    .setTitle(R.string.title_dialog_no_email_app_found)
                                    .setMessage(R.string.msg_dialog_no_email_app_found)
                                    .setPositiveButton(R.string.button_ok, null)
                                    .setNegativeButton(R.string.btn_dialog_copy_to_clipboard, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SystemUtil.putTextToClipboard(ResourceUtil.getString(R.string.label_developer_email), Constant.DEVELOPER_EMAIL);
                                            showToast(R.string.toast_copy_to_clipboard_successfully);
                                        }
                                    })
                                    .show();
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
