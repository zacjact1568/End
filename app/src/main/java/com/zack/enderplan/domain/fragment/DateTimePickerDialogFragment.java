package com.zack.enderplan.domain.fragment;

import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewSwitcher;

import com.zack.enderplan.R;

import java.util.Calendar;

public class DateTimePickerDialogFragment extends DialogFragment {

    private static final String CLASS_NAME = "DateTimePickerDia.Frag.";
    private static final String ARG_DEFAULT_TIME_IN_MILLIS = "default_time_in_millis";

    private boolean isAPIPre23;
    private long defaultTimeInMillis;
    private TextView title;
    private ViewSwitcher viewSwitcher;
    private TextView pickerSwitcher;
    private Calendar calendar;
    private String dateTimeFormatShortStr;
    private OnDateTimeChangedListener onDateTimeChangedListener;

    public DateTimePickerDialogFragment() {
        //Empty constructor
    }

    public static DateTimePickerDialogFragment newInstance(long defaultTimeInMillis) {
        DateTimePickerDialogFragment fragment = new DateTimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DEFAULT_TIME_IN_MILLIS, defaultTimeInMillis);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            defaultTimeInMillis = getArguments().getLong(ARG_DEFAULT_TIME_IN_MILLIS);
        }

        if (getActivity() instanceof OnDateTimeChangedListener) {
            onDateTimeChangedListener = (OnDateTimeChangedListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnDateTimeChangedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_fragment_date_time_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);
        pickerSwitcher = (TextView) view.findViewById(R.id.text_picker_switcher);
        DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
        TextView negativeButton = (TextView) view.findViewById(R.id.dialog_button_negative);
        TextView positiveButton = (TextView) view.findViewById(R.id.dialog_button_positive);

        pickerSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSwitcher.showNext();
                pickerSwitcher.setText(viewSwitcher.getCurrentView().getId() == R.id.time_picker ?
                        R.string.text_date_picker_switcher : R.string.text_time_picker_switcher);
            }
        });

        calendar = Calendar.getInstance();
        if (defaultTimeInMillis != 0) {
            calendar.setTimeInMillis(defaultTimeInMillis);
        }

        isAPIPre23 = Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
        if (isAPIPre23) {
            title = (TextView) view.findViewById(R.id.dialog_title);
            dateTimeFormatShortStr = getResources().getString(R.string.date_time_format_short);
            title.setText(DateFormat.format(dateTimeFormatShortStr, calendar));
        }


        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, month, dayOfMonth, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                if (isAPIPre23) {
                    title.setText(DateFormat.format(dateTimeFormatShortStr, calendar));
                }
            }
        });

        timePicker.setIs24HourView(true);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            timePicker.setCurrentHour(hourOfDay);
            timePicker.setCurrentMinute(minute);
        } else {
            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));
        }
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                if (isAPIPre23) {
                    title.setText(DateFormat.format(dateTimeFormatShortStr, calendar));
                }
            }
        });

        //TODO use ButterKnife
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                onDateTimeChangedListener.onDateTimeRemoved();
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                onDateTimeChangedListener.onDateTimeSelected(calendar.getTimeInMillis());
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onDateTimeChangedListener = null;
    }

    public interface OnDateTimeChangedListener {
        void onDateTimeSelected(long newTimeInMillis);
        void onDateTimeRemoved();
    }
}
