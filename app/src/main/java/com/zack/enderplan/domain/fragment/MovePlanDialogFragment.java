package com.zack.enderplan.domain.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.zack.enderplan.interactor.adapter.SimpleTypeAdapter;
import com.zack.enderplan.model.DataManager;

public class MovePlanDialogFragment extends DialogFragment {

    private static final String ARG_PLAN_COUNT = "plan_count";
    private static final String ARG_FROM_TYPE_CODE = "from_type_code";

    private int mPlanCount;
    private String mFromTypeCode;
    //private OnListItemClickListener mOnListItemClickListener;

    public MovePlanDialogFragment() {
        // Required empty public constructor
    }

    public static MovePlanDialogFragment newInstance(int planCount, String fromTypeCode) {
        MovePlanDialogFragment fragment = new MovePlanDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PLAN_COUNT, planCount);
        args.putString(ARG_FROM_TYPE_CODE, fromTypeCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mPlanCount = args.getInt(ARG_PLAN_COUNT, -1);
            mFromTypeCode = args.getString(ARG_FROM_TYPE_CODE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return null;
    }

    public interface OnTargetTypeSelectedListener {
        void onListItemClick(View view, int position);
    }

//    public void setOnListItemClickListener(OnListItemClickListener listener) {
//        mOnListItemClickListener = listener;
//    }
}
