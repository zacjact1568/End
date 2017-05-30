package com.zack.enderplan.view.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zack.enderplan.R;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.activity.TypeCreationActivity;
import com.zack.enderplan.view.adapter.TypePickerListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TypePickerDialogFragment extends DialogFragment {

    @BindView(R.id.list_type_picker)
    RecyclerView mTypePickerList;

    private static final String ARG_DEFAULT_TYPE_LIST_POSITION = "default_type_list_position";

    private OnTypePickedListener mOnTypePickedListener;
    private int mTypeListPosition;

    public TypePickerDialogFragment() {

    }

    public static TypePickerDialogFragment newInstance(int defaultTypeListPosition) {
        TypePickerDialogFragment fragment = new TypePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DEFAULT_TYPE_LIST_POSITION, defaultTypeListPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mTypeListPosition = args.getInt(ARG_DEFAULT_TYPE_LIST_POSITION, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_type_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        TypePickerListAdapter typePickerListAdapter = new TypePickerListAdapter(DataManager.getInstance(), mTypeListPosition);
        typePickerListAdapter.setOnItemClickListener(new TypePickerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mTypeListPosition = position;
            }
        });

        mTypePickerList.setAdapter(typePickerListAdapter);
        mTypePickerList.setHasFixedSize(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnTypePickedListener = null;
    }

    @OnClick({R.id.btn_new_type, R.id.btn_select, R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_new_type:
                TypeCreationActivity.start(getContext());
                break;
            case R.id.btn_select:
                if (mOnTypePickedListener != null) {
                    mOnTypePickedListener.onTypePicked(mTypeListPosition);
                }
                break;
            case R.id.btn_cancel:
                break;
        }
        getDialog().dismiss();
    }

    public interface OnTypePickedListener {
        void onTypePicked(int position);
    }

    public void setOnTypePickedListener(OnTypePickedListener listener) {
        mOnTypePickedListener = listener;
    }
}
