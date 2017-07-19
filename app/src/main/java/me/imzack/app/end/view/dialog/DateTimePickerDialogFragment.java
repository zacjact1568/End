package me.imzack.app.end.view.dialog;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewAnimator;

import me.imzack.app.end.R;
import me.imzack.app.end.common.Constant;
import me.imzack.app.end.util.ResourceUtil;
import me.imzack.app.end.util.TimeUtil;

import java.io.Serializable;
import java.util.Calendar;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;

public class DateTimePickerDialogFragment extends BaseDialogFragment {

    @BindView(R.id.picker_date)
    DatePicker mDatePicker;
    @BindView(R.id.picker_time)
    TimePicker mTimePicker;
    @BindView(R.id.switcher_date_time_picker)
    ViewAnimator mDateTimePickerSwitcher;
    @BindView(R.id.layout_switcher_date)
    LinearLayout mDateSwitcherLayout;
    @BindView(R.id.layout_switcher_time)
    LinearLayout mTimeSwitcherLayout;
    @BindView(R.id.text_date)
    TextView mDateText;
    @BindView(R.id.text_time)
    TextView mTimeText;

    @BindColor(R.color.colorPrimaryDark)
    int mDarkPrimaryColor;

    private static final String ARG_DEFAULT_TIME = "default_time";
    private static final String ARG_DATE_TIME_PICKED_LSNR = "date_time_picked_lsnr";

    private Calendar mCalendar;
    private OnDateTimePickedListener mOnDateTimePickedListener;

    public DateTimePickerDialogFragment() {

    }

    public static DateTimePickerDialogFragment newInstance(long defaultTime, OnDateTimePickedListener listener) {
        DateTimePickerDialogFragment fragment = new DateTimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NEU_BTN_STR, ResourceUtil.getString(R.string.button_remove));
        args.putString(ARG_NEG_BTN_STR, ResourceUtil.getString(R.string.button_cancel));
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

        setNeutralButtonClickListener(new OnButtonClickListener() {
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
                        updateDateText();
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
                updateTimeText();
            }
        });

        updateSwitcherLayout();
        updateDateText();
        updateTimeText();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnDateTimePickedListener = null;
    }

    @OnClick({R.id.layout_switcher_date, R.id.layout_switcher_time})
    public void onClickSwitcher() {
        mDateTimePickerSwitcher.showNext();
        updateSwitcherLayout();
    }

    private void updateSwitcherLayout() {
        boolean isDatePicker = mDateTimePickerSwitcher.getCurrentView().getId() == R.id.picker_date;
        mDateSwitcherLayout.setClickable(!isDatePicker);
        mDateSwitcherLayout.setAlpha(isDatePicker ? 1f : 0.8f);
        mDateSwitcherLayout.setBackgroundTintList(ColorStateList.valueOf(isDatePicker ? Color.TRANSPARENT : mDarkPrimaryColor));
        mTimeSwitcherLayout.setClickable(isDatePicker);
        mTimeSwitcherLayout.setAlpha(isDatePicker ? 0.8f : 1f);
        mTimeSwitcherLayout.setBackgroundTintList(ColorStateList.valueOf(isDatePicker ? mDarkPrimaryColor : Color.TRANSPARENT));
    }

    private void updateDateText() {
        mDateText.setText(TimeUtil.formatDate(mCalendar.getTimeInMillis()));
    }

    private void updateTimeText() {
        mTimeText.setText(TimeUtil.formatTime(mCalendar.getTimeInMillis()));
    }

    public interface OnDateTimePickedListener extends Serializable {
        void onDateTimePicked(long timeInMillis);
    }
}
