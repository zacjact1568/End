package com.zack.enderplan.view.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.zack.enderplan.R;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.TypeMarkPattern;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.view.adapter.TypeMarkPatternGridAdapter;

import java.util.List;

import butterknife.BindView;

public class TypeMarkPatternPickerDialogFragment extends BaseDialogFragment {

    @BindView(R.id.grid_type_mark_pattern)
    GridView mTypeMarkPatternGrid;

    private static final String ARG_DEFAULT_PATTERN = "default_pattern";

    private TypeMarkPattern mTypeMarkPattern;
    private int mPosition = -1;
    private List<TypeMarkPattern> mTypeMarkPatternList;
    private OnTypeMarkPatternPickedListener mOnTypeMarkPatternPickedListener;

    public TypeMarkPatternPickerDialogFragment() {

    }

    public static TypeMarkPatternPickerDialogFragment newInstance(String defaultPattern) {
        TypeMarkPatternPickerDialogFragment fragment = new TypeMarkPatternPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, ResourceUtil.getString(R.string.title_dialog_fragment_type_mark_pattern_picker));
        args.putString(ARG_NEG_BTN, ResourceUtil.getString(R.string.button_remove));
        args.putString(ARG_POS_BTN, ResourceUtil.getString(R.string.button_select));
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
                    break;
                }
            }
        }
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup root) {
        return inflater.inflate(R.layout.dialog_fragment_type_mark_pattern_picker, root, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTypeMarkPatternGrid.setAdapter(new TypeMarkPatternGridAdapter(mTypeMarkPatternList));

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
    public boolean onButtonClicked(int which) {
        switch (which) {
            case BTN_NEG:
                if (mOnTypeMarkPatternPickedListener != null) {
                    mOnTypeMarkPatternPickedListener.onTypeMarkPatternPicked(null);
                }
                break;
            case BTN_POS:
                if (mOnTypeMarkPatternPickedListener != null) {
                    mOnTypeMarkPatternPickedListener.onTypeMarkPatternPicked(mTypeMarkPattern.getPatternFn() == null ? null : mTypeMarkPattern);
                }
                break;
        }
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnTypeMarkPatternPickedListener = null;
    }

    public interface OnTypeMarkPatternPickedListener {
        void onTypeMarkPatternPicked(TypeMarkPattern typeMarkPattern);
    }

    public void setOnTypeMarkPatternPickedListener(OnTypeMarkPatternPickedListener listener) {
        mOnTypeMarkPatternPickedListener = listener;
    }
}
