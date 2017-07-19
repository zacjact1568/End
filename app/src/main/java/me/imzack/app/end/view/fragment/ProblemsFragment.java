package me.imzack.app.end.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.imzack.app.end.R;
import me.imzack.app.end.view.adapter.ProblemListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProblemsFragment extends BaseFragment {

    @BindView(R.id.list_problem)
    RecyclerView mProblemList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_problems, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mProblemList.setAdapter(new ProblemListAdapter());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
