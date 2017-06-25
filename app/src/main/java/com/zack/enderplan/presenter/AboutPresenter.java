package com.zack.enderplan.presenter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.zack.enderplan.R;
import com.zack.enderplan.util.CommonUtil;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.SystemUtil;
import com.zack.enderplan.view.adapter.AboutPagerAdapter;
import com.zack.enderplan.view.contract.AboutViewContract;
import com.zack.enderplan.view.fragment.AboutFragment;
import com.zack.enderplan.view.fragment.ThanksFragment;
import com.zack.enderplan.view.fragment.ProblemsFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AboutPresenter extends BasePresenter implements SensorEventListener {

    private static final int APP_BAR_STATE_EXPANDED = 1;
    private static final int APP_BAR_STATE_INTERMEDIATE = 0;
    private static final int APP_BAR_STATE_COLLAPSED = -1;

    private AboutViewContract mAboutViewContract;
    private AboutPagerAdapter mAboutPagerAdapter;
    private int mAppBarMaxRange;
    private float mLastHeaderAlpha = 1f;
    private int mAppBarState = APP_BAR_STATE_EXPANDED;
    private SensorManager mSensorManager;
    private float[] mGravityValues;
    private float[] mGeomagneticValues;
    private float[] mRotationValues;
    private float[] mOrientationValues;
    private float mMaxTranslation;

    @Inject
    AboutPresenter(AboutViewContract aboutViewContract, FragmentManager fragmentManager, SensorManager sensorManager) {
        mAboutViewContract = aboutViewContract;

        mAboutPagerAdapter = new AboutPagerAdapter(fragmentManager, getAboutPages());

        mSensorManager = sensorManager;
        mGravityValues = new float[3];
        mGeomagneticValues = new float[3];
        mRotationValues = new float[9];
        mOrientationValues = new float[3];
        mMaxTranslation = CommonUtil.convertDpToPx(12);
    }

    @Override
    public void attach() {
        mAboutViewContract.showInitialView(SystemUtil.getVersionName(), mAboutPagerAdapter);
    }

    @Override
    public void detach() {
        mAboutViewContract = null;
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

        float translationX = -mOrientationValues[2] * 30;
        float translationY = mOrientationValues[1] * 30;

        mAboutViewContract.translateViewWhenIncline(
                Math.abs(translationX) <= mMaxTranslation,
                translationX,
                Math.abs(translationY) <= mMaxTranslation,
                translationY
        );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void notifyPreDrawingAppBar(int appBarMaxRange) {
        mAppBarMaxRange = appBarMaxRange;
    }

    public void notifyAppBarScrolled(int offset) {

        if (mAppBarMaxRange == 0) return;

        int absOffset = Math.abs(offset);
        float headerAlpha = 1f - absOffset * 1.3f / mAppBarMaxRange;
        if (headerAlpha < 0) headerAlpha = 0;

        if ((headerAlpha == 0 || mLastHeaderAlpha == 0) && headerAlpha != mLastHeaderAlpha) {
            mAboutViewContract.onAppBarScrolledToCriticalPoint(headerAlpha == 0 ? ResourceUtil.getString(R.string.title_activity_about) : " ");
            mLastHeaderAlpha = headerAlpha;
        }

        mAboutViewContract.onAppBarScrolled(headerAlpha);

        if (absOffset == 0) {
            mAppBarState = APP_BAR_STATE_EXPANDED;
        } else if (absOffset == mAppBarMaxRange) {
            mAppBarState = APP_BAR_STATE_COLLAPSED;
        } else {
            mAppBarState = APP_BAR_STATE_INTERMEDIATE;
        }
    }

    public void notifyRegisteringSensorListener() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
    }

    public void notifyUnregisteringSensorListener() {
        mSensorManager.unregisterListener(this);
    }

    public void notifyResetingViewTranslation() {
        mAboutViewContract.resetViewTranslation();
    }

    public void notifyBackPressed() {
        if (mAppBarState == APP_BAR_STATE_EXPANDED) {
            mAboutViewContract.pressBack();
        } else {
            mAboutViewContract.backToTop();
        }
    }

    private List<Fragment> getAboutPages() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new AboutFragment());
        fragmentList.add(new ThanksFragment());
        fragmentList.add(new ProblemsFragment());
        return fragmentList;
    }
}
