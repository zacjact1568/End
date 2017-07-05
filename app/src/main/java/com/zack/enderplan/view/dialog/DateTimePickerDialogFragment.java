package com.zack.enderplan.view.dialog;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.ViewAnimator;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.TimeUtil;

import java.io.Serializable;
import java.util.Calendar;

import butterknife.BindView;

public class DateTimePickerDialogFragment extends BaseDialogFragment {

    @BindView(R.id.picker_date)
    DatePicker mDatePicker;
    @BindView(R.id.picker_time)
    TimePicker mTimePicker;
    @BindView(R.id.switcher_date_time_picker)
    ViewAnimator mDateTimePickerSwitcher;

    private static final String ARG_DEFAULT_TIME = "default_time";
    private static final String ARG_DATE_TIME_PICKED_LSNR = "date_time_picked_lsnr";

    private Calendar mCalendar;
    private OnDateTimePickedListener mOnDateTimePickedListener;

    public DateTimePickerDialogFragment() {

    }

    public static DateTimePickerDialogFragment newInstance(long defaultTime, OnDateTimePickedListener listener) {
        DateTimePickerDialogFragment fragment = new DateTimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NEU_BTN_STR, ResourceUtil.getString(R.string.text_time_picker_switcher));
        args.putString(ARG_NEG_BTN_STR, ResourceUtil.getString(R.string.button_remove));
        args.putString(ARG_POS_BTN_STR, ResourceUtil.getString(R.string.button_select));
        args.putLong(ARG_DEFAULT_TIME, defaultTime);
        args.putSerializable(ARG_DATE_TIME_PICKED_LSNR, listener);
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
            mOnDateTimePickedListener = (OnDateTimePickedListener) args.getSerializable(ARG_DATE_TIME_PICKED_LSNR);
        }

        //设置点击三个按钮的事件
        setNeutralButtonClickListener(new OnButtonClickListener() {
            @Override
            public boolean onClick() {
                mDateTimePickerSwitcher.showNext();
                setNeutralButtonString(getString(mDateTimePickerSwitcher.getCurrentView().getId() == R.id.picker_time ? R.string.text_date_picker_switcher : R.string.text_time_picker_switcher));
                return false;
            }
        });
        setNegativeButtonClickListener(new OnButtonClickListener() {
            @Override
            public boolean onClick() {
                if (mOnDateTimePickedListener != null) {
                    mOnDateTimePickedListener.onDateTimePicked(Constant.UNDEFINED_TIME);
                }
                return true;
            }
        });
        setPositiveButtonClickListener(new OnButtonClickListener() {
            @Override
            public boolean onClick() {
                if (mOnDateTimePickedListener != null) {
                    mOnDateTimePickedListener.onDateTimePicked(mCalendar.getTimeInMillis());
                }
                return true;
            }
        });
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup root) {
        return inflater.inflate(R.layout.dialog_fragment_date_time_picker, root, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        mTimePicker.setIs24HourView(TimeUtil.is24HourFormat());
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

    public interface OnDateTimePickedListener extends Serializable {
        void onDateTimePicked(long timeInMillis);
    }
}
