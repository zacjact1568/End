package me.imzack.app.end.view.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import butterknife.OnClick
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

    @BindView(R.id.picker_date)
    lateinit var mDatePicker: DatePicker
    @BindView(R.id.picker_time)
    lateinit var mTimePicker: TimePicker
    @BindView(R.id.switcher_date_time_picker)
    lateinit var mDateTimePickerSwitcher: ViewAnimator
    @BindView(R.id.layout_switcher_date)
    lateinit var mDateSwitcherLayout: LinearLayout
    @BindView(R.id.layout_switcher_time)
    lateinit var mTimeSwitcherLayout: LinearLayout
    @BindView(R.id.text_date)
    lateinit var mDateText: TextView
    @BindView(R.id.text_time)
    lateinit var mTimeText: TextView

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

        mDatePicker.init(
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
        ) { _, year, monthOfYear, dayOfMonth ->
            mCalendar.set(year, monthOfYear, dayOfMonth)
            updateDateText()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mTimePicker.currentHour = mCalendar.get(Calendar.HOUR_OF_DAY)
            mTimePicker.currentMinute = mCalendar.get(Calendar.MINUTE)
        } else {
            mTimePicker.hour = mCalendar.get(Calendar.HOUR_OF_DAY)
            mTimePicker.minute = mCalendar.get(Calendar.MINUTE)
        }
        mTimePicker.setIs24HourView(TimeUtil.is24HourFormat)
        mTimePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
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
        mDateTimePickerSwitcher.showNext()
        updateSwitcherLayout()
    }

    private fun updateSwitcherLayout() {
        val isDatePicker = mDateTimePickerSwitcher.currentView.id == R.id.picker_date
        mDateSwitcherLayout.isClickable = !isDatePicker
        mDateSwitcherLayout.alpha = if (isDatePicker) 1f else 0.8f
        mDateSwitcherLayout.backgroundTintList = ColorStateList.valueOf(if (isDatePicker) Color.TRANSPARENT else mDarkPrimaryColor)
        mTimeSwitcherLayout.isClickable = isDatePicker
        mTimeSwitcherLayout.alpha = if (isDatePicker) 0.8f else 1f
        mTimeSwitcherLayout.backgroundTintList = ColorStateList.valueOf(if (isDatePicker) mDarkPrimaryColor else Color.TRANSPARENT)
    }

    private fun updateDateText() {
        mDateText.text = TimeUtil.formatDate(mCalendar.timeInMillis)
    }

    private fun updateTimeText() {
        mTimeText.text = TimeUtil.formatTime(mCalendar.timeInMillis)
    }

    interface OnDateTimePickedListener : Serializable {
        fun onDateTimePicked(timeInMillis: Long)
    }
}
