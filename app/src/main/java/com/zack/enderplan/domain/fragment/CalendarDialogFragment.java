package com.zack.enderplan.domain.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import com.zack.enderplan.R;

import java.util.Calendar;

public class CalendarDialogFragment extends DialogFragment {

    private static final String CLASS_NAME = "CalendarDialogFragment";
    private static final String ARG_DEFAULT_DATE_IN_MILLIS = "default_date_in_millis";

    private long defaultDateInMillis;
    private TextView title;
    private Calendar calendar;
    private String dateFormatStr;
    private OnDateChangedListener onDateChangedListener;

    public CalendarDialogFragment() {
        //Empty constructor
    }

    public static CalendarDialogFragment newInstance(long defaultDateInMillis) {
        CalendarDialogFragment fragment = new CalendarDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DEFAULT_DATE_IN_MILLIS, defaultDateInMillis);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            defaultDateInMillis = getArguments().getLong(ARG_DEFAULT_DATE_IN_MILLIS);
        }

        if (getActivity() instanceof OnDateChangedListener) {
            onDateChangedListener = (OnDateChangedListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnDateChangedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = (TextView) view.findViewById(R.id.dialog_title);
        CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendar_view);
        TextView negativeButton = (TextView) view.findViewById(R.id.dialog_button_negative);
        TextView positiveButton = (TextView) view.findViewById(R.id.dialog_button_positive);

        calendar = Calendar.getInstance();
        if (defaultDateInMillis != 0) {
            //如果传入的时间不为0，将calendar的时间设为传入的时间
            calendar.setTimeInMillis(defaultDateInMillis);
        } else {
            //如果传入的时间为0，calendar为当前时间，但时间设成今天的最后时刻
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
        }

        dateFormatStr = getResources().getString(R.string.date_format);

        title.setText(DateFormat.format(dateFormatStr, calendar));

        calendarView.setDate(calendar.getTimeInMillis());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //更新calendar中的时间
                calendar.set(year, month, dayOfMonth);
                title.setText(DateFormat.format(dateFormatStr, calendar));
                /*int colorId = calendar.getTimeInMillis() < System.currentTimeMillis() ?
                        R.color.red : R.color.colorPrimary;
                title.setTextColor(ContextCompat.getColor(getActivity(), colorId));*/
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                onDateChangedListener.onDateRemoved();
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                onDateChangedListener.onDateSelected(calendar.getTimeInMillis());
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onDateChangedListener = null;
    }

    public interface OnDateChangedListener {
        void onDateSelected(long newDateInMillis);
        void onDateRemoved();
    }
}
