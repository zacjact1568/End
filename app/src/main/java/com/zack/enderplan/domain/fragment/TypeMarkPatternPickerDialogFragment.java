package com.zack.enderplan.domain.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.zack.enderplan.R;
import com.zack.enderplan.interactor.adapter.TypeMarkPatternAdapter;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.TypeMarkPattern;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TypeMarkPatternPickerDialogFragment extends DialogFragment {

    @BindView(R.id.grid_type_mark_pattern)
    GridView mTypeMarkPatternGrid;

    private static final String ARG_DEFAULT_PATTERN = "default_pattern";

    private TypeMarkPattern mTypeMarkPattern;
    private int mPosition = -1;
    private List<TypeMarkPattern> mTypeMarkPatternList;
    private OnTypeMarkPatternPickedListener mOnTypeMarkPatternPickedListener;

    public TypeMarkPatternPickerDialogFragment() {
        // Required empty public constructor
    }

    public static TypeMarkPatternPickerDialogFragment newInstance(String defaultPattern) {
        TypeMarkPatternPickerDialogFragment fragment = new TypeMarkPatternPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DEFAULT_PATTERN, defaultPattern);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String defaultPattern = null;
        Bundle args = getArguments();
        if (args != null) {
            defaultPattern = args.getString(ARG_DEFAULT_PATTERN);
        }

        mTypeMarkPatternList = DataManager.getInstance().getTypeMarkPatternList();

        mTypeMarkPattern = new TypeMarkPattern();

        if (defaultPattern != null) {
            for (int i = 0; i < mTypeMarkPatternList.size(); i++) {
                TypeMarkPattern typeMarkPattern = mTypeMarkPatternList.get(i);
                if (typeMarkPattern.getPatternFn().equals(defaultPattern)) {
                    mPosition = i;
                    mTypeMarkPattern.setPattern(typeMarkPattern.getPatternFn(), typeMarkPattern.getPatternName());
                    break;//TODO color也要加
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_type_mark_pattern_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mTypeMarkPatternGrid.setAdapter(new TypeMarkPatternAdapter(mTypeMarkPatternList));

        if (mPosition != -1) {
            mTypeMarkPatternGrid.setItemChecked(mPosition, true);
        }

        mTypeMarkPatternGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
                TypeMarkPattern typeMarkPattern = mTypeMarkPatternList.get(mPosition);
                mTypeMarkPattern.setPattern(typeMarkPattern.getPatternFn(), typeMarkPattern.getPatternName());
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnTypeMarkPatternPickedListener = null;
    }

    @OnClick({R.id.btn_remove, R.id.btn_select})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_remove:
                if (mOnTypeMarkPatternPickedListener != null) {
                    mOnTypeMarkPatternPickedListener.onTypeMarkPatternPicked(null);
                }
                getDialog().dismiss();
                break;
            case R.id.btn_select:
                if (mOnTypeMarkPatternPickedListener != null) {
                    mOnTypeMarkPatternPickedListener.onTypeMarkPatternPicked(mTypeMarkPattern.getPatternFn() == null ? null : mTypeMarkPattern);
                }
                getDialog().dismiss();
                break;
        }
    }

    public interface OnTypeMarkPatternPickedListener {
        void onTypeMarkPatternPicked(TypeMarkPattern typeMarkPattern);
    }

    public void setOnTypeMarkPatternPickedListener(OnTypeMarkPatternPickedListener listener) {
        mOnTypeMarkPatternPickedListener = listener;
    }
}
