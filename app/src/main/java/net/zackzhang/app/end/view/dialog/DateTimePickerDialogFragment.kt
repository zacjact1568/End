package net.zackzhang.app.end.view.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_fragment_date_time_picker.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.util.TimeUtil
import me.imzack.lib.basedialogfragment.BaseDialogFragment
import java.util.*

class DateTimePickerDialogFragment : BaseDialogFragment() {

    companion object {

        private const val ARG_DEFAULT_TIME = "default_time"

        fun newInstance(defaultTime: Long): DateTimePickerDialogFragment {
            val fragment = DateTimePickerDialogFragment()
            val args = Bundle()
            // TODO 必须放到这里，有点不好
            // TODO BaseDialogFragment 中，titleText 使用 GONE 隐藏！！
            putAddHorizontalMargins(args, false)
            args.putLong(ARG_DEFAULT_TIME, defaultTime)
            fragment.arguments = args
            return fragment
        }
    }

    private val darkPrimaryColor = ResourceUtil.getColor(R.color.colorPrimaryDark)

    private val calendar = Calendar.getInstance()

    // 不提供编程来改变时间，因为无此需求，即只有通过用户改变时间

    var dateTimePickedListener: ((timeInMillis: Long) -> Boolean)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // defaultTime 被初始化，calendar 被更新为正确的初始时间
        calendar.timeInMillis = arguments!!.getLong(ARG_DEFAULT_TIME)
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_date_time_picker, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Switcher
        updateSwitcherLayout()
        layout_switcher_date.setOnClickListener { onClickSwitcher() }
        layout_switcher_time.setOnClickListener { onClickSwitcher() }

        // DateText
        updateDateText()

        // TimeText
        updateTimeText()

        // DatePicker
        picker_date.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ) { _, year, monthOfYear, dayOfMonth ->
            calendar.set(year, monthOfYear, dayOfMonth)
            updateDateText()
        }

        // TimePicker
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            picker_time.currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            picker_time.currentMinute = calendar.get(Calendar.MINUTE)
        } else {
            picker_time.hour = calendar.get(Calendar.HOUR_OF_DAY)
            picker_time.minute = calendar.get(Calendar.MINUTE)
        }
        picker_time.setIs24HourView(TimeUtil.is24HourFormat)
        picker_time.setOnTimeChangedListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            updateTimeText()
        }

        neutralButtonText = ResourceUtil.getString(R.string.button_remove)
        neutralButtonClickListener = { dateTimePickedListener?.invoke(0L) != false }
        negativeButtonText = ResourceUtil.getString(android.R.string.cancel)
        negativeButtonClickListener = { true }
        positiveButtonText = ResourceUtil.getString(R.string.button_select)
        positiveButtonClickListener = { dateTimePickedListener?.invoke(calendar.timeInMillis) != false }
    }

    private fun onClickSwitcher() {
        switcher_date_time_picker.showNext()
        updateSwitcherLayout()
    }

    private fun updateSwitcherLayout() {
        val isDatePicker = switcher_date_time_picker.currentView.id == R.id.picker_date
        layout_switcher_date.isClickable = !isDatePicker
        layout_switcher_date.alpha = if (isDatePicker) 1f else 0.8f
        layout_switcher_date.backgroundTintList = ColorStateList.valueOf(if (isDatePicker) Color.TRANSPARENT else darkPrimaryColor)
        layout_switcher_time.isClickable = isDatePicker
        layout_switcher_time.alpha = if (isDatePicker) 0.8f else 1f
        layout_switcher_time.backgroundTintList = ColorStateList.valueOf(if (isDatePicker) darkPrimaryColor else Color.TRANSPARENT)
    }

    private fun updateDateText() {
        text_date.text = TimeUtil.formatDate(calendar.timeInMillis)
    }

    private fun updateTimeText() {
        text_time.text = TimeUtil.formatTime(calendar.timeInMillis)
    }
}
