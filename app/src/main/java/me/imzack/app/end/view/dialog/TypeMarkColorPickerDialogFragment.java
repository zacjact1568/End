package me.imzack.app.end.view.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import me.imzack.app.end.App;
import me.imzack.app.end.R;
import me.imzack.app.end.model.bean.TypeMarkColor;
import me.imzack.app.end.util.ColorUtil;
import me.imzack.app.end.util.ResourceUtil;
import me.imzack.app.end.view.adapter.TypeMarkColorGridAdapter;
import me.imzack.app.end.view.widget.CircleColorView;
import me.imzack.app.end.view.widget.ColorPicker;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

public class TypeMarkColorPickerDialogFragment extends BaseDialogFragment {

    @BindView(R.id.switcher_color_picker)
    ViewAnimator mColorPickerSwitcher;
    @BindView(R.id.grid_type_mark_color)
    GridView mTypeMarkColorGrid;
    @BindView(R.id.picker_type_mark_color)
    ColorPicker mTypeMarkColorPicker;
    @BindView(R.id.text_type_mark_color)
    TextView mTypeMarkColorText;
    @BindView(R.id.ic_type_mark_color)
    CircleColorView mTypeMarkColorIcon;

    private static final String ARG_DEFAULT_COLOR = "default_color";
    private static final String ARG_TYPE_MARK_COLOR_PICKED_LSNR = "type_mark_color_picked_lsnr";

    private String mDefaultColor;
    private TypeMarkColor mTypeMarkColor;
    private int mPosition = -1;
    private List<TypeMarkColor> mTypeMarkColorList;
    private OnTypeMarkColorPickedListener mOnTypeMarkColorPickedListener;

    public TypeMarkColorPickerDialogFragment() {

    }

    public static TypeMarkColorPickerDialogFragment newInstance(String defaultColor, OnTypeMarkColorPickedListener listener) {
        TypeMarkColorPickerDialogFragment fragment = new TypeMarkColorPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE_STR, ResourceUtil.getString(R.string.title_dialog_fragment_type_mark_color_picker));
        args.putString(ARG_NEU_BTN_STR, ResourceUtil.getString(R.string.btn_custom));
        args.putString(ARG_NEG_BTN_STR, ResourceUtil.getString(R.string.button_cancel));
        args.putString(ARG_POS_BTN_STR, ResourceUtil.getString(R.string.button_select));
        args.putString(ARG_DEFAULT_COLOR, defaultColor);
        args.putSerializable(ARG_TYPE_MARK_COLOR_PICKED_LSNR, listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mDefaultColor = args.getString(ARG_DEFAULT_COLOR);
            mOnTypeMarkColorPickedListener = (OnTypeMarkColorPickedListener) args.getSerializable(ARG_TYPE_MARK_COLOR_PICKED_LSNR);
        }

        mTypeMarkColorList = App.getDataManager().getTypeMarkColorList();

        mPosition = getPositionInTypeMarkColorList(mDefaultColor);

        mTypeMarkColor = new TypeMarkColor(
                mDefaultColor,
                mPosition == -1 ? mDefaultColor : mTypeMarkColorList.get(mPosition).getColorName()
        );

        setNeutralButtonClickListener(new OnButtonClickListener() {
            @Override
            public boolean onClick() {
                mColorPickerSwitcher.showNext();
                if (mColorPickerSwitcher.getCurrentView().getId() == R.id.grid_type_mark_color) {
                    //切换到了grid界面，此时mPosition一定为-1
                    mPosition = getPositionInTypeMarkColorList(mTypeMarkColor.getColorHex());
                    if (mPosition != -1) {
                        //若picker界面选中的颜色在grid界面也有，选中它
                        mTypeMarkColorGrid.setItemChecked(mPosition, true);
                    }
                    setNeutralButtonString(getString(R.string.btn_custom));
                } else {
                    //切换到了picker界面
                    if (mPosition != -1) {
                        //若之前在grid界面有选择，取消选择
                        mTypeMarkColorGrid.setItemChecked(mPosition, false);
                        mPosition = -1;
                    }
                    //此时mPosition一定为-1
                    mTypeMarkColorText.setText(mTypeMarkColor.getColorHex());
                    mTypeMarkColorIcon.setFillColor(Color.parseColor(mTypeMarkColor.getColorHex()));
                    mTypeMarkColorPicker.setColor(Color.parseColor(mTypeMarkColor.getColorHex()));
                    setNeutralButtonString(getString(R.string.btn_preset));
                }
                return false;
            }
        });
        setPositiveButtonClickListener(new OnButtonClickListener() {
            @Override
            public boolean onClick() {
                mPosition = getPositionInTypeMarkColorList(mTypeMarkColor.getColorHex());
                //若position为-1，说明颜色在grid中不存在，是在picker中设置的，无名称，用hex代替；反之说明颜色在grid中存在，有名称
                mTypeMarkColor.setColorName(mPosition == -1 ? mTypeMarkColor.getColorHex() : mTypeMarkColorList.get(mPosition).getColorName());
                if (mOnTypeMarkColorPickedListener != null) {
                    mOnTypeMarkColorPickedListener.onTypeMarkColorPicked(mTypeMarkColor);
                }
                return true;
            }
        });
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup root) {
        return inflater.inflate(R.layout.dialog_fragment_type_mark_color_picker, root, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTypeMarkColorGrid.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mTypeMarkColorGrid.getViewTreeObserver().removeOnPreDrawListener(this);
                //运行时设置宽度（在xml文件中宽度设置成match_parent不行）
                mTypeMarkColorPicker.getLayoutParams().width = mTypeMarkColorGrid.getWidth();
                return false;
            }
        });

        mTypeMarkColorGrid.setAdapter(new TypeMarkColorGridAdapter(mTypeMarkColorList));

        if (mPosition != -1) {
            mTypeMarkColorGrid.setItemChecked(mPosition, true);
        }

        mTypeMarkColorGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
                mTypeMarkColor.setColorHex(mTypeMarkColorList.get(mPosition).getColorHex());
            }
        });

        mTypeMarkColorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                String colorHex = ColorUtil.parseColor(color);
                mTypeMarkColorIcon.setFillColor(color);
                mTypeMarkColorText.setText(colorHex);
                mTypeMarkColor.setColorHex(colorHex);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnTypeMarkColorPickedListener = null;
    }

    private int getPositionInTypeMarkColorList(String color) {
        for (int i = 0; i < mTypeMarkColorList.size(); i++) {
            if (mTypeMarkColorList.get(i).getColorHex().equals(color)) {
                return i;
            }
        }
        //未在TypeMarkColorList中找到
        return -1;
    }

    public interface OnTypeMarkColorPickedListener extends Serializable {
        void onTypeMarkColorPicked(TypeMarkColor typeMarkColor);
    }
}
