package me.imzack.app.end.view.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import kotlinx.android.synthetic.main.dialog_fragment_date_time_picker.*
import me.imzack.app.end.R
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.TimeUtil
import java.io.Serializable
import java.util.*

class DateTimePickerDialogFragment : BaseDialogFragment() {

    companion object {

        private val ARG_DEFAULT_TIME = "default_time"
        private val ARG_DATE_TIME_PICKED_LSNR = "date_time_picked_lsnr"

        fun newInstance(defaultTime: Long, listener: OnDateTimePickedListener): DateTimePickerDialogFragment {
            val fragment = DateTimePickerDialogFragment()
            val args = Bundle()
            args.putString(ARG_NEU_BTN_STR, ResourceUtil.getString(R.string.button_remove))
            args.putString(ARG_NEG_BTN_STR, ResourceUtil.getString(R.string.button_cancel))
            args.putString(ARG_POS_BTN_STR, ResourceUtil.getString(R.string.button_select))
            args.putLong(ARG_DEFAULT_TIME, defaultTime)
            args.putSerializable(ARG_DATE_TIME_PICKED_LSNR, listener)
            fragment.arguments = args
            return fragment
        }
    }

    var mOnDateTimePickedListener: OnDateTimePickedListener? = null

    private val mCalendar = Calendar.getInstance()
    private val mDarkPrimaryColor = ResourceUtil.getColor(R.color.colorPrimaryDark)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        if (args != null) {
            mCalendar.timeInMillis = args.getLong(ARG_DEFAULT_TIME)
            mOnDateTimePickedListener = args.getSerializable(ARG_DATE_TIME_PICKED_LSNR) as OnDateTimePickedListener
        }

        mNeutralButtonClickListener = object : BaseDialogFragment.OnButtonClickListener {
            override fun onClick(): Boolean {
                mOnDateTimePickedListener?.onDateTimePicked(0L)
                return true
            }
        }
        mPositiveButtonClickListener = object : BaseDialogFragment.OnButtonClickListener {
            override fun onClick(): Boolean {
                mOnDateTimePickedListener?.onDateTimePicked(mCalendar!!.timeInMillis)
                return true
            }
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_date_time_picker, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        picker_date.init(
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
        ) { _, year, monthOfYear, dayOfMonth ->
            mCalendar.set(year, monthOfYear, dayOfMonth)
            updateDateText()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            picker_time.currentHour = mCalendar.get(Calendar.HOUR_OF_DAY)
            picker_time.currentMinute = mCalendar.get(Calendar.MINUTE)
        } else {
            picker_time.hour = mCalendar.get(Calendar.HOUR_OF_DAY)
            picker_time.minute = mCalendar.get(Calendar.MINUTE)
        }
        picker_time.setIs24HourView(TimeUtil.is24HourFormat)
        picker_time.setOnTimeChangedListener { _, hourOfDay, minute ->
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            mCalendar.set(Calendar.MINUTE, minute)
            updateTimeText()
        }

        updateSwitcherLayout()
        updateDateText()
        updateTimeText()
    }

    override fun onDetach() {
        super.onDetach()
        mOnDateTimePickedListener = null
    }

    @OnClick(R.id.layout_switcher_date, R.id.layout_switcher_time)
    fun onClickSwitcher() {
        switcher_date_time_picker.showNext()
        updateSwitcherLayout()
    }

    private fun updateSwitcherLayout() {
        val isDatePicker = switcher_date_time_picker.currentView.id == R.id.picker_date
        layout_switcher_date.isClickable = !isDatePicker
        layout_switcher_date.alpha = if (isDatePicker) 1f else 0.8f
        layout_switcher_date.backgroundTintList = ColorStateList.valueOf(if (isDatePicker) Color.TRANSPARENT else mDarkPrimaryColor)
        layout_switcher_time.isClickable = isDatePicker
        layout_switcher_time.alpha = if (isDatePicker) 0.8f else 1f
        layout_switcher_time.backgroundTintList = ColorStateList.valueOf(if (isDatePicker) mDarkPrimaryColor else Color.TRANSPARENT)
    }

    private fun updateDateText() {
        text_date.text = TimeUtil.formatDate(mCalendar.timeInMillis)
    }

    private fun updateTimeText() {
        text_time.text = TimeUtil.formatTime(mCalendar.timeInMillis)
    }

    interface OnDateTimePickedListener : Serializable {
        fun onDateTimePicked(timeInMillis: Long)
    }
}
