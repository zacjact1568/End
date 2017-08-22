package me.imzack.app.end.presenter

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v4.app.FragmentManager
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.util.CommonUtil
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.SystemUtil
import me.imzack.app.end.view.adapter.AboutPagerAdapter
import me.imzack.app.end.view.contract.AboutViewContract
import me.imzack.app.end.view.fragment.AboutFragment
import me.imzack.app.end.view.fragment.ProblemsFragment
import me.imzack.app.end.view.fragment.ThanksFragment
import javax.inject.Inject

class AboutPresenter @Inject constructor(
        private var mAboutViewContract: AboutViewContract?,
        fragmentManager: FragmentManager,
        private val mSensorManager: SensorManager
) : BasePresenter(), SensorEventListener {

    // 主构造函数的参数可以在init块中使用，也可以在类中声明的属性初始化器中使用
    private val mAboutPagerAdapter = AboutPagerAdapter(fragmentManager, listOf(AboutFragment(), ThanksFragment(), ProblemsFragment()))
    private var mAppBarMaxRange = 0
    private var mLastHeaderAlpha = 1f
    private var mAppBarState = Constant.APP_BAR_STATE_EXPANDED
    private var mGravityValues = FloatArray(3)
    private var mGeomagneticValues = FloatArray(3)
    private val mRotationValues = FloatArray(9)
    private val mOrientationValues = FloatArray(3)
    private val mMaxTranslation = CommonUtil.convertDpToPx(12).toFloat()

    override fun attach() {
        mAboutViewContract!!.showInitialView(SystemUtil.versionName, mAboutPagerAdapter)
    }

    override fun detach() {
        mAboutViewContract = null
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            mGravityValues = event.values
        }
        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagneticValues = event.values
        }
        SensorManager.getRotationMatrix(mRotationValues, null, mGravityValues, mGeomagneticValues)
        SensorManager.getOrientation(mRotationValues, mOrientationValues)

        val translationX = -mOrientationValues[2] * 30
        val translationY = mOrientationValues[1] * 30

        mAboutViewContract!!.translateViewWhenIncline(
                Math.abs(translationX) <= mMaxTranslation,
                translationX,
                Math.abs(translationY) <= mMaxTranslation,
                translationY
        )
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    fun notifyPreDrawingAppBar(appBarMaxRange: Int) {
        mAppBarMaxRange = appBarMaxRange
    }

    fun notifyAppBarScrolled(offset: Int) {

        if (mAppBarMaxRange == 0) return

        val absOffset = Math.abs(offset)
        var headerAlpha = 1f - absOffset * 1.3f / mAppBarMaxRange
        if (headerAlpha < 0) headerAlpha = 0f

        if ((headerAlpha == 0f || mLastHeaderAlpha == 0f) && headerAlpha != mLastHeaderAlpha) {
            mAboutViewContract!!.onAppBarScrolledToCriticalPoint(if (headerAlpha == 0f) ResourceUtil.getString(R.string.title_activity_about) else " ")
            mLastHeaderAlpha = headerAlpha
        }

        mAboutViewContract!!.onAppBarScrolled(headerAlpha)

        mAppBarState = when (absOffset) {
            0 -> Constant.APP_BAR_STATE_EXPANDED
            mAppBarMaxRange -> Constant.APP_BAR_STATE_COLLAPSED
            else -> Constant.APP_BAR_STATE_INTERMEDIATE
        }
    }

    fun notifyRegisteringSensorListener() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI)
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI)
    }

    fun notifyUnregisteringSensorListener() {
        mSensorManager.unregisterListener(this)
    }

    fun notifyResetingViewTranslation() {
        mAboutViewContract!!.resetViewTranslation()
    }

    fun notifyBackPressed() {
        if (mAppBarState == Constant.APP_BAR_STATE_EXPANDED) {
            mAboutViewContract!!.pressBack()
        } else {
            mAboutViewContract!!.backToTop()
        }
    }
}
