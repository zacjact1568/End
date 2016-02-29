package com.zack.enderplan.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.widget.PlanAdapter;

import java.util.ArrayList;
import java.util.List;

public class AllPlansFragment extends Fragment {

    private static final String CLASS_NAME = "AllPlansFragment";

    private PlanAdapter planAdapter;

    private static final String ARG_PLAN_LIST = "plan_list";

    private OnPlanItemClickListener onPlanItemClickListener;
    private OnPlanItemLongClickListener onPlanItemLongClickListener;
    private OnPlanItemSwipedListener onPlanItemSwipedListener;

    public AllPlansFragment() {
        // Required empty public constructor
    }

    public static AllPlansFragment newInstance(List<Plan> planList) {
        AllPlansFragment fragment = new AllPlansFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PLAN_LIST, (ArrayList<Plan>) planList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(CLASS_NAME, "onAttach执行");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            return;
        }

        Context context = getActivity();

        if (context instanceof OnPlanItemClickListener) {
            onPlanItemClickListener = (OnPlanItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlanItemClickListener");
        }
        if (context instanceof OnPlanItemLongClickListener) {
            onPlanItemLongClickListener = (OnPlanItemLongClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlanItemLongClickListener");
        }
        if (context instanceof OnPlanItemSwipedListener) {
            onPlanItemSwipedListener = (OnPlanItemSwipedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlanItemSwipedListener");
        }

        List<Plan> planList = getArguments().getParcelableArrayList(ARG_PLAN_LIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_plans, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onPlanItemClickListener = null;
        onPlanItemLongClickListener = null;
        onPlanItemSwipedListener = null;
    }

    public PlanAdapter getPlanAdapter() {
        return planAdapter;
    }

    public interface OnPlanItemClickListener {
        void onPlanItemClick(int position);
    }

    public void onPlanItemClick(int position) {
        if (onPlanItemClickListener != null) {
            onPlanItemClickListener.onPlanItemClick(position);
        }
    }

    public interface OnPlanItemLongClickListener {
        void onPlanItemLongClick(int position);
    }

    public void onPlanItemLongClick(int position) {
        if (onPlanItemLongClickListener != null) {
            onPlanItemLongClickListener.onPlanItemLongClick(position);
        }
    }

    public interface OnPlanItemSwipedListener {
        void onPlanItemSwiped(RecyclerView.ViewHolder viewHolder, int direction);
    }

    public void onPlanItemSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (onPlanItemSwipedListener != null) {
            onPlanItemSwipedListener.onPlanItemSwiped(viewHolder, direction);
        }
    }
}
