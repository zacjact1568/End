package me.imzack.app.end.view.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.NumberPicker
import kotlinx.android.synthetic.main.date_time_picker.view.*
import me.imzack.app.end.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Zack
 */
class DateTimePicker(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val MAX_DATE_LENGTH = 10

    private val calendar = Calendar.getInstance()
    private val variableCal = Calendar.getInstance()
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd EEE", Locale.getDefault())
    private val dateValues = Array(MAX_DATE_LENGTH, { "" })
    //private int selectedHour, selectedMinute;
    //private boolean isLimited = true;

    var onDateTimeChangeListener: ((timeInMillis: Long) -> Unit)? = null

    init {

        inflate(context, R.layout.date_time_picker, this)

        val valueFormatter = NumberPicker.Formatter { value ->
            var str = value.toString()
            if (value < 10) {
                str = "0" + str
            }
            str
        }

        calendar.set(Calendar.SECOND, 0)
        //selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        //selectedMinute = calendar.get(Calendar.MINUTE);

        //final String today = simpleDateFormat.format(variableCal.getTime());
        //mCalendar初始设置成前一天
        variableCal.add(Calendar.DAY_OF_YEAR, -1)
        //TODO hour默认加5
        //final String yesterday = simpleDateFormat.format(variableCal.getTime());

        date_picker.minValue = 0
        date_picker.maxValue = MAX_DATE_LENGTH - 1
        updateDateValues()
        date_picker.value = 1
        date_picker.wrapSelectorWheel = false
        //date_picker.setFormatter(); TODO 添加昨天、今天、明天
        date_picker.setOnValueChangedListener { _, oldVal, newVal ->
            /*if (dateValues[newVal].equals(yesterday)) {
                    date_picker.setValue(1);
                    return;
                }
                isLimited = dateValues[newVal].equals(today);
                if (isLimited) {
                    if (selectedHour < currentHour) {
                        hour_picker.setValue(currentHour);
                        calendar.set(Calendar.HOUR_OF_DAY, currentHour);
                    }
                    if (selectedMinute < currentMinute) {
                        minute_picker.setValue(currentMinute);
                        calendar.set(Calendar.MINUTE, currentMinute);
                    }
                }*/
            when (newVal) {
                0 -> {
                    variableCal.add(Calendar.DAY_OF_YEAR, 1 - 2 * (MAX_DATE_LENGTH - 1))
                    updateDateValues()
                    date_picker.value = MAX_DATE_LENGTH - 2
                }
                MAX_DATE_LENGTH - 1 -> {
                    variableCal.add(Calendar.DAY_OF_YEAR, -1)
                    updateDateValues()
                    date_picker.value = 1
                }
                else -> { }
            }
            calendar.add(Calendar.DAY_OF_YEAR, newVal - oldVal)
            onDateTimeChangeListener?.invoke(calendar.timeInMillis)
        }

        hour_picker.minValue = 0
        hour_picker.maxValue = 23
        hour_picker.value = variableCal.get(Calendar.HOUR_OF_DAY)
        hour_picker.wrapSelectorWheel = true
        hour_picker.setFormatter(valueFormatter)
        hour_picker.setOnValueChangedListener { _, _, _ ->
            /*if (newVal < currentHour && isLimited) {
                    hour_picker.setValue(currentHour);
                    return;
                }*/
            calendar.set(Calendar.HOUR_OF_DAY, hour_picker.value)
            onDateTimeChangeListener?.invoke(calendar.timeInMillis)
        }

        minute_picker.minValue = 0
        minute_picker.maxValue = 59
        minute_picker.value = variableCal.get(Calendar.MINUTE)
        minute_picker.wrapSelectorWheel = true
        minute_picker.setFormatter(valueFormatter)
        minute_picker.setOnValueChangedListener { _, _, _ ->
            /*if (newVal < currentMinute && isLimited) {
                    minute_picker.setValue(currentMinute);
                    return;
                }*/
            calendar.set(Calendar.MINUTE, minute_picker.value)
            onDateTimeChangeListener?.invoke(calendar.timeInMillis)
        }

        //修改分隔线颜色
        val fields = NumberPicker::class.java.declaredFields
        val colorDrawable = ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary))
        for (field in fields) {
            if (field.name == "mSelectionDivider") {
                field.isAccessible = true
                try {
                    field.set(date_picker, colorDrawable)
                    field.set(hour_picker, colorDrawable)
                    field.set(minute_picker, colorDrawable)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
                break
            }
        }
    }

    private fun updateDateValues() {
        for (i in 0 until MAX_DATE_LENGTH) {
            dateValues[i] = simpleDateFormat.format(variableCal.time)
            variableCal.add(Calendar.DAY_OF_YEAR, 1)
        }
        variableCal.add(Calendar.DAY_OF_YEAR, -1)
        date_picker.displayedValues = dateValues
    }
}
