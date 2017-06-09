package com.zack.enderplan.view.dialog;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zack.enderplan.R;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.view.activity.TypeCreationActivity;
import com.zack.enderplan.view.adapter.TypePickerListAdapter;

import butterknife.BindView;

public class TypePickerDialogFragment extends BaseDialogFragment {

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
        args.putString(ARG_TITLE, ResourceUtil.getString(R.string.title_dialog_fragment_type_picker));
        args.putString(ARG_NEU_BTN, ResourceUtil.getString(R.string.btn_new_type));
        args.putString(ARG_NEG_BTN, ResourceUtil.getString(R.string.button_cancel));
        args.putString(ARG_POS_BTN, ResourceUtil.getString(R.string.button_select));
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
    public View onCreateContentView(LayoutInflater inflater, ViewGroup root) {
        return inflater.inflate(R.layout.dialog_fragment_type_picker, root, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    public boolean onButtonClicked(int which) {
        switch (which) {
            case BTN_NEU:
                TypeCreationActivity.start(getContext());
                break;
            case BTN_NEG:
                break;
            case BTN_POS:
                if (mOnTypePickedListener != null) {
                    mOnTypePickedListener.onTypePicked(mTypeListPosition);
                }
                break;
        }
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnTypePickedListener = null;
    }

    public interface OnTypePickedListener {
        void onTypePicked(int position);
    }

    public void setOnTypePickedListener(OnTypePickedListener listener) {
        mOnTypePickedListener = listener;
    }
}
