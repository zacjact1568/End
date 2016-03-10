package com.zack.enderplan.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.manager.TypeManager;
import com.zack.enderplan.widget.TypeAdapter;

import java.util.ArrayList;
import java.util.List;

public class AllTypesFragment extends Fragment {

    private List<Type> typeList;
    private TypeAdapter typeAdapter;
    //private List<Type> typeList;
    //private EnderPlanDB enderplanDB;

    private static final String ARG_PLAN_COUNT_OF_EACH_TYPE = "plan_count_of_each_type";

    private Bundle planCountOfEachType;

    //private OnFragmentInteractionListener mListener;

    public AllTypesFragment() {
        // Required empty public constructor
    }

    public static AllTypesFragment newInstance(Bundle planCountOfEachType) {
        AllTypesFragment fragment = new AllTypesFragment();
        Bundle args = new Bundle();
        args.putBundle(ARG_PLAN_COUNT_OF_EACH_TYPE, planCountOfEachType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            planCountOfEachType = getArguments().getBundle(ARG_PLAN_COUNT_OF_EACH_TYPE);
        }
        TypeManager typeManager = TypeManager.getInstance();
        typeList = typeManager.getTypeList();
        typeAdapter = new TypeAdapter(getActivity(), typeList, planCountOfEachType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_types, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(typeAdapter);
    }

    public List<Type> getTypeList() {
        return typeList;
    }

    public TypeAdapter getTypeAdapter() {
        return typeAdapter;
    }

    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }*/
}
