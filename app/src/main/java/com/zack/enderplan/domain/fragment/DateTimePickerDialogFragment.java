package com.zack.enderplan.domain.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewSwitcher;

import com.zack.enderplan.R;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DateTimePickerDialogFragment extends DialogFragment {

    @BindView(R.id.picker_date)
    DatePicker mDatePicker;
    @BindView(R.id.picker_time)
    TimePicker mTimePicker;
    @BindView(R.id.switcher_date_time_picker)
    ViewSwitcher mDateTimePickerSwitcher;
    @BindView(R.id.btn_picker_switcher)
    TextView mPickerSwitcherButton;

    private static final String ARG_DEFAULT_TIME_IN_MILLIS = "default_time_in_millis";

    private long defaultTimeInMillis;
    private Calendar calendar;
    private OnDateTimePickedListener onDateTimePickedListener;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_date_time_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        calendar = Calendar.getInstance();
        if (defaultTimeInMillis != 0) {
            calendar.setTimeInMillis(defaultTimeInMillis);
        }

        mDatePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                    }
                }
        );

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        } else {
            mTimePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setMinute(calendar.get(Calendar.MINUTE));
        }
        mTimePicker.setIs24HourView(true);
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onDateTimePickedListener = null;
    }

    @OnClick({R.id.btn_picker_switcher, R.id.btn_ok, R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_picker_switcher:
                mDateTimePickerSwitcher.showNext();
                mPickerSwitcherButton.setText(mDateTimePickerSwitcher.getCurrentView().getId() == R.id.picker_time ? R.string.text_date_picker_switcher : R.string.text_time_picker_switcher);
                break;
            case R.id.btn_ok:
                getDialog().dismiss();
                if (onDateTimePickedListener != null) {
                    onDateTimePickedListener.onDateTimePicked(calendar.getTimeInMillis());
                }
                break;
            case R.id.btn_cancel:
                getDialog().dismiss();
                if (onDateTimePickedListener != null) {
                    onDateTimePickedListener.onDateTimePicked(0);
                }
                break;
        }
    }

    public interface OnDateTimePickedListener {
        void onDateTimePicked(long timeInMillis);
    }

    public void setOnDateTimePickedListener(OnDateTimePickedListener listener) {
        onDateTimePickedListener = listener;
    }
}
