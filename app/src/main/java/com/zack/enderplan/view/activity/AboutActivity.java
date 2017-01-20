package com.zack.enderplan.view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.common.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.text_version_name)
    TextView versionNameText;

    public static void start(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setupActionBar();

        versionNameText.setText(getVersionName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getVersionName() {
        String versionName;
        PackageManager manager = getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "null";
        }
        return versionName;
    }

    @OnClick({R.id.fab_email})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_email:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constant.DEVELOPER_EMAIL});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_email));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.title_dialog_no_email_app_found)
                            .setMessage(R.string.msg_dialog_no_email_app_found)
                            .setPositiveButton(R.string.button_ok, null)
                            .setNegativeButton(R.string.btn_dialog_copy_to_clipboard, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Util.putTextToClipboard(Util.getString(R.string.label_developer_email), Constant.DEVELOPER_EMAIL);
                                    showToast(R.string.toast_copy_to_clipboard_successfully);
                                }
                            })
                            .show();
                }
                break;
        }
    }
}
