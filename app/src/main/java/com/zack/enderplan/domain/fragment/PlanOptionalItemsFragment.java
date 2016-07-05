package com.zack.enderplan.domain.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zack.enderplan.R;

public class PlanOptionalItemsFragment extends Fragment {

    private static final String ARG_PRIORITY_LEVEL = "priority_level";
    private static final String ARG_DEADLINE = "deadline";
    private static final String ARG_REMINDER_TIME = "reminder_time";

    private int priorityLevel;
    private long deadline, reminderTime;
    private RelativeLayout priorityLevelItemView, deadlineItemView, reminderItemView;
    private OnItemViewClickListener onItemViewClickListener;

    private static final String CLASS_NAME = "PlanOptionalItemsFrag.";

    public PlanOptionalItemsFragment() {
        // Required empty public constructor
    }

    public static PlanOptionalItemsFragment newInstance(int priorityLevel, long deadline, long reminderTime) {
        PlanOptionalItemsFragment fragment = new PlanOptionalItemsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PRIORITY_LEVEL, priorityLevel);
        args.putLong(ARG_DEADLINE, deadline);
        args.putLong(ARG_REMINDER_TIME, reminderTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            priorityLevel = getArguments().getInt(ARG_PRIORITY_LEVEL);
            deadline = getArguments().getLong(ARG_DEADLINE);
            reminderTime = getArguments().getLong(ARG_REMINDER_TIME);
        }

        if (getActivity() instanceof OnItemViewClickListener) {
            onItemViewClickListener = (OnItemViewClickListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnItemViewClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan_optional_items, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        priorityLevelItemView = (RelativeLayout) view.findViewById(R.id.item_view_priority_level);
        deadlineItemView = (RelativeLayout) view.findViewById(R.id.item_view_deadline);
        reminderItemView = (RelativeLayout) view.findViewById(R.id.item_view_reminder);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClickListener.onItemViewClick(v);
            }
        };
        priorityLevelItemView.setOnClickListener(listener);
        deadlineItemView.setOnClickListener(listener);
        reminderItemView.setOnClickListener(listener);

        //RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        TextView priorityLevelDescription = (TextView) view.findViewById(R.id.text_priority_level_description);
        TextView deadlineDescription = (TextView) view.findViewById(R.id.text_deadline_description);
        TextView reminderDescription = (TextView) view.findViewById(R.id.text_reminder_description);

        //ratingBar.setRating(plan.getPriorityLevel());
        //String[] priorityLevelDescriptions = getResources().getStringArray(R.array.descriptions_priority_level);
        //priorityLevelDescription.setText(priorityLevelDescriptions[priorityLevel]);

        if (deadline != 0) {
            String dateFormatStr = getResources().getString(R.string.date_format);
            deadlineDescription.setText(DateFormat.format(dateFormatStr, deadline));
        }

        if (reminderTime != 0) {
            String dateTimeFormatStr = getResources().getString(R.string.date_time_format);
            reminderDescription.setText(DateFormat.format(dateTimeFormatStr, reminderTime));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onItemViewClickListener = null;
    }

    public RelativeLayout getPriorityLevelItemView() {
        return priorityLevelItemView;
    }

    public RelativeLayout getDeadlineItemView() {
        return deadlineItemView;
    }

    public RelativeLayout getReminderItemView() {
        return reminderItemView;
    }

    public interface OnItemViewClickListener {
        void onItemViewClick(View itemView);
    }
}
