package me.imzack.app.end.view.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.NumberPicker
import butterknife.BindView
import butterknife.ButterKnife
import me.imzack.app.end.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Zack
 */
class DateTimePicker(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    @BindView(R.id.date_picker)
    lateinit var datePicker: NumberPicker
    @BindView(R.id.hour_picker)
    lateinit var hourPicker: NumberPicker
    @BindView(R.id.minute_picker)
    lateinit var minutePicker: NumberPicker

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

        ButterKnife.bind(this)

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

        datePicker.minValue = 0
        datePicker.maxValue = MAX_DATE_LENGTH - 1
        updateDateValues()
        datePicker.value = 1
        datePicker.wrapSelectorWheel = false
        //datePicker.setFormatter(); TODO 添加昨天、今天、明天
        datePicker.setOnValueChangedListener { _, oldVal, newVal ->
            /*if (dateValues[newVal].equals(yesterday)) {
                    datePicker.setValue(1);
                    return;
                }
                isLimited = dateValues[newVal].equals(today);
                if (isLimited) {
                    if (selectedHour < currentHour) {
                        hourPicker.setValue(currentHour);
                        calendar.set(Calendar.HOUR_OF_DAY, currentHour);
                    }
                    if (selectedMinute < currentMinute) {
                        minutePicker.setValue(currentMinute);
                        calendar.set(Calendar.MINUTE, currentMinute);
                    }
                }*/
            when (newVal) {
                0 -> {
                    variableCal.add(Calendar.DAY_OF_YEAR, 1 - 2 * (MAX_DATE_LENGTH - 1))
                    updateDateValues()
                    datePicker.value = MAX_DATE_LENGTH - 2
                }
                MAX_DATE_LENGTH - 1 -> {
                    variableCal.add(Calendar.DAY_OF_YEAR, -1)
                    updateDateValues()
                    datePicker.value = 1
                }
                else -> { }
            }
            calendar.add(Calendar.DAY_OF_YEAR, newVal - oldVal)
            onDateTimeChangeListener?.invoke(calendar.timeInMillis)
        }

        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        hourPicker.value = variableCal.get(Calendar.HOUR_OF_DAY)
        hourPicker.wrapSelectorWheel = true
        hourPicker.setFormatter(valueFormatter)
        hourPicker.setOnValueChangedListener { _, _, _ ->
            /*if (newVal < currentHour && isLimited) {
                    hourPicker.setValue(currentHour);
                    return;
                }*/
            calendar.set(Calendar.HOUR_OF_DAY, hourPicker.value)
            onDateTimeChangeListener?.invoke(calendar.timeInMillis)
        }

        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.value = variableCal.get(Calendar.MINUTE)
        minutePicker.wrapSelectorWheel = true
        minutePicker.setFormatter(valueFormatter)
        minutePicker.setOnValueChangedListener { _, _, _ ->
            /*if (newVal < currentMinute && isLimited) {
                    minutePicker.setValue(currentMinute);
                    return;
                }*/
            calendar.set(Calendar.MINUTE, minutePicker.value)
            onDateTimeChangeListener?.invoke(calendar.timeInMillis)
        }

        //修改分隔线颜色
        val fields = NumberPicker::class.java.declaredFields
        val colorDrawable = ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary))
        for (field in fields) {
            if (field.name == "mSelectionDivider") {
                field.isAccessible = true
                try {
                    field.set(datePicker, colorDrawable)
                    field.set(hourPicker, colorDrawable)
                    field.set(minutePicker, colorDrawable)
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
        datePicker.displayedValues = dateValues
    }
}
