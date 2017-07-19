package me.imzack.app.end.view.dialog;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.imzack.app.end.R;
import me.imzack.app.end.model.DataManager;
import me.imzack.app.end.util.ResourceUtil;
import me.imzack.app.end.view.activity.TypeCreationActivity;
import me.imzack.app.end.view.adapter.TypePickerGridAdapter;

import java.io.Serializable;

import butterknife.BindView;

public class TypePickerDialogFragment extends BaseDialogFragment {

    @BindView(R.id.grid_type_picker)
    RecyclerView mTypePickerGrid;

    private static final String ARG_DEFAULT_POSITION = "default_position";
    private static final String ARG_TYPE_PICKED_LSNR = "type_picked_lsnr";

    private OnTypePickedListener mOnTypePickedListener;
    private int mPosition;

    public TypePickerDialogFragment() {

    }

    public static TypePickerDialogFragment newInstance(int defaultPosition, OnTypePickedListener listener) {
        TypePickerDialogFragment fragment = new TypePickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE_STR, ResourceUtil.getString(R.string.title_dialog_fragment_type_picker));
        args.putString(ARG_NEU_BTN_STR, ResourceUtil.getString(R.string.btn_new_type));
        args.putString(ARG_NEG_BTN_STR, ResourceUtil.getString(R.string.button_cancel));
        args.putString(ARG_POS_BTN_STR, ResourceUtil.getString(R.string.button_select));
        args.putInt(ARG_DEFAULT_POSITION, defaultPosition);
        args.putSerializable(ARG_TYPE_PICKED_LSNR, listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mPosition = args.getInt(ARG_DEFAULT_POSITION, -1);
            mOnTypePickedListener = (OnTypePickedListener) args.getSerializable(ARG_TYPE_PICKED_LSNR);
        }

        setNeutralButtonClickListener(new OnButtonClickListener() {
            @Override
            public boolean onClick() {
                TypeCreationActivity.start(getContext());
                return true;
            }
        });
        setPositiveButtonClickListener(new OnButtonClickListener() {
            @Override
            public boolean onClick() {
                if (mOnTypePickedListener != null) {
                    mOnTypePickedListener.onTypePicked(mPosition);
                }
                return true;
            }
        });
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup root) {
        return inflater.inflate(R.layout.dialog_fragment_type_picker, root, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TypePickerGridAdapter typePickerGridAdapter = new TypePickerGridAdapter(DataManager.getInstance(), mPosition);
        typePickerGridAdapter.setOnItemClickListener(new TypePickerGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mPosition = position;
            }
        });

        mTypePickerGrid.setAdapter(typePickerGridAdapter);
        mTypePickerGrid.setHasFixedSize(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnTypePickedListener = null;
    }

    public interface OnTypePickedListener extends Serializable {
        void onTypePicked(int position);
    }
}
