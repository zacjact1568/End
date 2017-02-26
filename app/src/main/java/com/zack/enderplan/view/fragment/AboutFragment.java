package com.zack.enderplan.view.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import butterknife.OnClick;

public class AboutFragment extends BaseFragment {

    @BindView(R.id.text_email)
    TextView mEmailText;
    @BindView(R.id.text_store)
    TextView mStoreText;

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

        mEmailText.setText(StringUtil.addSpan(getString(R.string.text_email), StringUtil.SPAN_UNDERLINE));
        mStoreText.setText(StringUtil.addSpan(getString(R.string.text_store), StringUtil.SPAN_UNDERLINE));
    }

    @OnClick({R.id.text_email, R.id.text_store, R.id.btn_reminder_problem})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_email:
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
                break;
            case R.id.text_store:
                SystemUtil.openLink(
                        "market://details?id=" + getContext().getPackageName(),
                        getActivity(),
                        getString(R.string.toast_no_store_found)
                );
                break;
            case R.id.btn_reminder_problem:
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_dialog_reminder_problem)
                        .setMessage(R.string.msg_dialog_reminder_problem)
                        .setPositiveButton(R.string.button_ok, null)
                        .show();
                break;
        }
    }
}
