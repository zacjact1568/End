package me.imzack.app.ender.view.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import me.imzack.app.ender.R;
import me.imzack.app.ender.model.DataManager;
import me.imzack.app.ender.model.bean.TypeMarkPattern;
import me.imzack.app.ender.util.ResourceUtil;
import me.imzack.app.ender.view.adapter.TypeMarkPatternGridAdapter;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

public class TypeMarkPatternPickerDialogFragment extends BaseDialogFragment {

    @BindView(R.id.grid_type_mark_pattern)
    GridView mTypeMarkPatternGrid;

    private static final String ARG_DEFAULT_PATTERN = "default_pattern";
    private static final String ARG_TYPE_MARK_PATTERN_PICKED_LSNR = "type_mark_pattern_picked_lsnr";

    private TypeMarkPattern mTypeMarkPattern;
    private int mPosition = -1;
    private List<TypeMarkPattern> mTypeMarkPatternList;
    private OnTypeMarkPatternPickedListener mOnTypeMarkPatternPickedListener;

    public TypeMarkPatternPickerDialogFragment() {

    }

    public static TypeMarkPatternPickerDialogFragment newInstance(String defaultPattern, OnTypeMarkPatternPickedListener listener) {
        TypeMarkPatternPickerDialogFragment fragment = new TypeMarkPatternPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE_STR, ResourceUtil.getString(R.string.title_dialog_fragment_type_mark_pattern_picker));
        args.putString(ARG_NEU_BTN_STR, ResourceUtil.getString(R.string.button_remove));
        args.putString(ARG_NEG_BTN_STR, ResourceUtil.getString(R.string.button_cancel));
        args.putString(ARG_POS_BTN_STR, ResourceUtil.getString(R.string.button_select));
        args.putString(ARG_DEFAULT_PATTERN, defaultPattern);
        args.putSerializable(ARG_TYPE_MARK_PATTERN_PICKED_LSNR, listener);
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
            mOnTypeMarkPatternPickedListener = (OnTypeMarkPatternPickedListener) args.getSerializable(ARG_TYPE_MARK_PATTERN_PICKED_LSNR);
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

        setNeutralButtonClickListener(new OnButtonClickListener() {
            @Override
            public boolean onClick() {
                if (mOnTypeMarkPatternPickedListener != null) {
                    mOnTypeMarkPatternPickedListener.onTypeMarkPatternPicked(null);
                }
                return true;
            }
        });
        setPositiveButtonClickListener(new OnButtonClickListener() {
            @Override
            public boolean onClick() {
                if (mOnTypeMarkPatternPickedListener != null) {
                    mOnTypeMarkPatternPickedListener.onTypeMarkPatternPicked(mTypeMarkPattern.getPatternFn() == null ? null : mTypeMarkPattern);
                }
                return true;
            }
        });
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
    public void onDetach() {
        super.onDetach();
        mOnTypeMarkPatternPickedListener = null;
    }

    public interface OnTypeMarkPatternPickedListener extends Serializable {
        void onTypeMarkPatternPicked(TypeMarkPattern typeMarkPattern);
    }
}
