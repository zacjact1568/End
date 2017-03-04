package com.zack.enderplan.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zack.enderplan.R;
import com.zack.enderplan.view.adapter.LibraryListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LibrariesFragment extends BaseFragment {

    @BindView(R.id.list_library)
    RecyclerView mLibraryList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_libraries, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mLibraryList.setAdapter(new LibraryListAdapter(getActivity()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
