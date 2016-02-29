package com.zack.enderplan.activity;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;

import com.zack.enderplan.R;

public class RatingBarDialogFragment extends DialogFragment {

    private static final String CLASS_NAME = "RatingBarDialogFragment";
    private static final String ARG_DEFAULT_RATING = "default_rating";
    private static final String ARG_DIALOG_TITLE = "dialog_title";

    private int defaultRating, newRating;
    private String dialogTitle;
    private OnRatingChangeListener onRatingChangeListener;

    public RatingBarDialogFragment() {
        //Empty constructor
    }

    public static RatingBarDialogFragment newInstance(int defaultRating, String dialogTitle) {
        RatingBarDialogFragment fragment = new RatingBarDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DEFAULT_RATING, defaultRating);
        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            defaultRating = getArguments().getInt(ARG_DEFAULT_RATING);
            dialogTitle = getArguments().getString(ARG_DIALOG_TITLE);
        }

        //Better put them in onAttach(...)
        if (getActivity() instanceof OnRatingChangeListener) {
            onRatingChangeListener = (OnRatingChangeListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnRatingChangeListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_fragment_rating_bar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = (TextView) view.findViewById(R.id.dialog_title);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        TextView negativeButton = (TextView) view.findViewById(R.id.dialog_button_negative);
        TextView positiveButton = (TextView) view.findViewById(R.id.dialog_button_positive);

        title.setText(dialogTitle);

        ratingBar.setRating(defaultRating);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                newRating = (int) rating;
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                onRatingChangeListener.onRatingChanged(newRating);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onRatingChangeListener = null;
    }

    public interface OnRatingChangeListener {
        void onRatingChanged(int newRating);
    }
}
