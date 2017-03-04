package com.zack.enderplan.view.fragment;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.util.CommonUtil;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.StringUtil;
import com.zack.enderplan.util.SystemUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutFragment extends BaseFragment implements SensorEventListener {

    @BindView(R.id.image_logo)
    ImageView mLogoImage;
    @BindView(R.id.text_email)
    TextView mEmailText;
    @BindView(R.id.text_store)
    TextView mStoreText;

    private SensorManager mSensorManager;
    private float[] mGravityValues;
    private float[] mGeomagneticValues;
    private float[] mRotationValues;
    private float[] mOrientationValues;
    private float mMaxTranslation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mGravityValues = new float[3];
        mGeomagneticValues = new float[3];
        mRotationValues = new float[9];
        mOrientationValues = new float[3];
        mMaxTranslation = CommonUtil.convertDpToPx(12);
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

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //将位置恢复放在这，就看不到移动的过程了
        mLogoImage.setTranslationX(0f);
        mLogoImage.setTranslationY(0f);
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravityValues = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagneticValues = event.values;
        }
        SensorManager.getRotationMatrix(mRotationValues, null, mGravityValues, mGeomagneticValues);
        SensorManager.getOrientation(mRotationValues, mOrientationValues);

        playTranslationOnLogoImage(mOrientationValues[1], mOrientationValues[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void playTranslationOnLogoImage(float pitch, float roll) {

        float translationX = -roll * 60;
        float translationY = pitch * 60;
        
        boolean isTranslationXProper = Math.abs(translationX) <= mMaxTranslation;
        boolean isTranslationYProper = Math.abs(translationY) <= mMaxTranslation;

        if (!isTranslationXProper && !isTranslationYProper) return;

        Path path = new Path();
        //start point
        path.moveTo(mLogoImage.getTranslationX(), mLogoImage.getTranslationY());
        //end point
        if (isTranslationXProper && isTranslationYProper) {
            //x和y均合适
            path.lineTo(translationX, translationY);
        } else if (isTranslationXProper) {
            //x合适，y不合适
            path.lineTo(translationX, mLogoImage.getTranslationY());
        } else {
            //x不合适，y合适
            path.lineTo(mLogoImage.getTranslationX(), translationY);
        }
        ObjectAnimator.ofFloat(mLogoImage, "translationX", "translationY", path).setDuration(80).start();
    }
}
