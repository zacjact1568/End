package com.zack.enderplan.view.dialog;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.ViewAnimator;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Constant;

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
    ViewAnimator mDateTimePickerSwitcher;
    @BindView(R.id.btn_picker_switcher)
    Button mPickerSwitcherButton;

    private static final String ARG_DEFAULT_TIME = "default_time";

    private Calendar mCalendar;
    private OnDateTimePickedListener mOnDateTimePickedListener;

    public DateTimePickerDialogFragment() {
        //Empty constructor
    }

    public static DateTimePickerDialogFragment newInstance(long defaultTime) {
        DateTimePickerDialogFragment fragment = new DateTimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DEFAULT_TIME, defaultTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCalendar = Calendar.getInstance();
        Bundle args = getArguments();
        if (args != null) {
            mCalendar.setTimeInMillis(args.getLong(ARG_DEFAULT_TIME));
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

        mDatePicker.init(
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mCalendar.set(year, monthOfYear, dayOfMonth);
                    }
                }
        );

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mTimePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        } else {
            mTimePicker.setHour(mCalendar.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setMinute(mCalendar.get(Calendar.MINUTE));
        }
        mTimePicker.setIs24HourView(true);
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnDateTimePickedListener = null;
    }

    @OnClick({R.id.btn_picker_switcher, R.id.btn_select, R.id.btn_remove})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_picker_switcher:
                mDateTimePickerSwitcher.showNext();
                mPickerSwitcherButton.setText(mDateTimePickerSwitcher.getCurrentView().getId() == R.id.picker_time ? R.string.text_date_picker_switcher : R.string.text_time_picker_switcher);
                break;
            case R.id.btn_select:
                if (mOnDateTimePickedListener != null) {
                    mOnDateTimePickedListener.onDateTimePicked(mCalendar.getTimeInMillis());
                }
                getDialog().dismiss();
                break;
            case R.id.btn_remove:
                if (mOnDateTimePickedListener != null) {
                    mOnDateTimePickedListener.onDateTimePicked(Constant.UNDEFINED_TIME);
                }
                getDialog().dismiss();
                break;
        }
    }

    public interface OnDateTimePickedListener {
        void onDateTimePicked(long timeInMillis);
    }

    public void setOnDateTimePickedListener(OnDateTimePickedListener listener) {
        mOnDateTimePickedListener = listener;
    }
}
