package me.imzack.app.ender.view.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import me.imzack.app.ender.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Zack
 */
public class DateTimePicker extends FrameLayout {

    private static final int MAX_DATE_LENGTH = 10;

    private NumberPicker datePicker, hourPicker, minutePicker;
    private Calendar calendar, variableCal;
    private SimpleDateFormat simpleDateFormat;
    private String[] dateValues;
    //private int selectedHour, selectedMinute;
    //private boolean isLimited = true;
    private OnDateTimeChangeListener onDateTimeChangeListener;

    public DateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.date_time_picker, this);

        datePicker = (NumberPicker) findViewById(R.id.date_picker);
        hourPicker = (NumberPicker) findViewById(R.id.hour_picker);
        minutePicker = (NumberPicker) findViewById(R.id.minute_picker);

        NumberPicker.Formatter valueFormatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                String str = String.valueOf(value);
                if (value < 10) {
                    str = "0" + str;
                }
                return str;
            }
        };

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd EEE", Locale.getDefault());

        calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.SECOND, 0);
        //selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        //selectedMinute = calendar.get(Calendar.MINUTE);

        variableCal = Calendar.getInstance(Locale.getDefault());
        //final String today = simpleDateFormat.format(variableCal.getTime());
        //mCalendar初始设置成前一天
        variableCal.add(Calendar.DAY_OF_YEAR, -1);
        //TODO hour默认加5
        //final String yesterday = simpleDateFormat.format(variableCal.getTime());

        dateValues = new String[MAX_DATE_LENGTH];

        datePicker.setMinValue(0);
        datePicker.setMaxValue(MAX_DATE_LENGTH - 1);
        updateDateValues();
        datePicker.setValue(1);
        datePicker.setWrapSelectorWheel(false);
        //datePicker.setFormatter(); TODO 添加昨天、今天、明天
        datePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
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
                switch (newVal) {
                    case 0:
                        variableCal.add(Calendar.DAY_OF_YEAR, 1 - 2 * (MAX_DATE_LENGTH - 1));
                        updateDateValues();
                        datePicker.setValue(MAX_DATE_LENGTH - 2);
                        break;
                    case MAX_DATE_LENGTH - 1:
                        variableCal.add(Calendar.DAY_OF_YEAR, -1);
                        updateDateValues();
                        datePicker.setValue(1);
                        break;
                    default:
                        break;
                }
                calendar.add(Calendar.DAY_OF_YEAR, newVal - oldVal);
                onDateTimeChange();
            }
        });

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(variableCal.get(Calendar.HOUR_OF_DAY));
        hourPicker.setWrapSelectorWheel(true);
        hourPicker.setFormatter(valueFormatter);
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                /*if (newVal < currentHour && isLimited) {
                    hourPicker.setValue(currentHour);
                    return;
                }*/
                calendar.set(Calendar.HOUR_OF_DAY, hourPicker.getValue());
                onDateTimeChange();
            }
        });

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(variableCal.get(Calendar.MINUTE));
        minutePicker.setWrapSelectorWheel(true);
        minutePicker.setFormatter(valueFormatter);
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                /*if (newVal < currentMinute && isLimited) {
                    minutePicker.setValue(currentMinute);
                    return;
                }*/
                calendar.set(Calendar.MINUTE, minutePicker.getValue());
                onDateTimeChange();
            }
        });

        //修改分隔线颜色
        java.lang.reflect.Field[] fields = NumberPicker.class.getDeclaredFields();
        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary));
        for (java.lang.reflect.Field field : fields) {
            if (field.getName().equals("mSelectionDivider")) {
                field.setAccessible(true);
                try {
                    field.set(datePicker, colorDrawable);
                    field.set(hourPicker, colorDrawable);
                    field.set(minutePicker, colorDrawable);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void updateDateValues() {
        for (int i = 0; i < MAX_DATE_LENGTH; i++) {
            dateValues[i] = simpleDateFormat.format(variableCal.getTime());
            variableCal.add(Calendar.DAY_OF_YEAR, 1);
        }
        variableCal.add(Calendar.DAY_OF_YEAR, -1);
        datePicker.setDisplayedValues(dateValues);
    }

    private void onDateTimeChange() {
        if (onDateTimeChangeListener != null) {
            onDateTimeChangeListener.onDateTimeChange(this, calendar.getTimeInMillis());
        }
    }

    public interface OnDateTimeChangeListener {
        void onDateTimeChange(DateTimePicker picker, long milliseconds);
    }

    public void setOnDateTimeChangeListener(OnDateTimeChangeListener onDateTimeChangeListener) {
        this.onDateTimeChangeListener = onDateTimeChangeListener;
    }
}
