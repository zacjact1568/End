package com.zack.enderplan.view.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.zack.enderplan.R;
import com.zack.enderplan.view.adapter.TypeMarkColorAdapter;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.view.widget.CircleColorView;
import com.zack.enderplan.view.widget.ColorPicker;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TypeMarkColorPickerDialogFragment extends DialogFragment {

    @BindView(R.id.switcher_color_picker)
    ViewSwitcher mColorPickerSwitcher;
    @BindView(R.id.grid_type_mark_color)
    GridView mTypeMarkColorGrid;
    @BindView(R.id.picker_type_mark_color)
    ColorPicker mTypeMarkColorPicker;
    @BindView(R.id.text_type_mark_color)
    TextView mTypeMarkColorText;
    @BindView(R.id.ic_type_mark_color)
    CircleColorView mTypeMarkColorIcon;
    @BindView(R.id.btn_picker_switcher)
    Button mPickerSwitcherButton;

    private static final String ARG_DEFAULT_COLOR = "default_color";

    private String mDefaultColor;
    private TypeMarkColor mTypeMarkColor;
    private int mPosition = -1;
    private List<TypeMarkColor> mTypeMarkColorList;
    private OnTypeMarkColorPickedListener mOnTypeMarkColorPickedListener;

    public TypeMarkColorPickerDialogFragment() {
        // Required empty public constructor
    }

    public static TypeMarkColorPickerDialogFragment newInstance(String defaultColor) {
        TypeMarkColorPickerDialogFragment fragment = new TypeMarkColorPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DEFAULT_COLOR, defaultColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mDefaultColor = args.getString(ARG_DEFAULT_COLOR);
        }

        mTypeMarkColorList = DataManager.getInstance().getTypeMarkColorList();

        mPosition = getPositionInTypeMarkColorList(mDefaultColor);

        mTypeMarkColor = new TypeMarkColor(
                mDefaultColor,
                mPosition == -1 ? mDefaultColor : mTypeMarkColorList.get(mPosition).getColorName()
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_type_mark_color_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mTypeMarkColorGrid.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mTypeMarkColorGrid.getViewTreeObserver().removeOnPreDrawListener(this);
                //运行时设置宽度（在xml文件中宽度设置成match_parent不行）
                mTypeMarkColorPicker.setLayoutParams(new LinearLayout.LayoutParams(mTypeMarkColorGrid.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
                return false;
            }
        });

        mTypeMarkColorGrid.setAdapter(new TypeMarkColorAdapter(mTypeMarkColorList));

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
                String colorHex = Util.parseColor(color);
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

    @OnClick({R.id.btn_picker_switcher, R.id.btn_cancel, R.id.btn_select})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_picker_switcher:
                mColorPickerSwitcher.showNext();
                if (mColorPickerSwitcher.getCurrentView().getId() == R.id.grid_type_mark_color) {
                    //切换到了grid界面，此时mPosition一定为-1
                    mPosition = getPositionInTypeMarkColorList(mTypeMarkColor.getColorHex());
                    if (mPosition != -1) {
                        //若picker界面选中的颜色在grid界面也有，选中它
                        mTypeMarkColorGrid.setItemChecked(mPosition, true);
                    }
                    mPickerSwitcherButton.setText(R.string.btn_custom);
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
                    mPickerSwitcherButton.setText(R.string.btn_preset);
                }
                break;
            case R.id.btn_cancel:
                getDialog().dismiss();
                break;
            case R.id.btn_select:
                mPosition = getPositionInTypeMarkColorList(mTypeMarkColor.getColorHex());
                //若position为-1，说明颜色在grid中不存在，是在picker中设置的，无名称，用hex代替；反之说明颜色在grid中存在，有名称
                mTypeMarkColor.setColorName(mPosition == -1 ? mTypeMarkColor.getColorHex() : mTypeMarkColorList.get(mPosition).getColorName());
                if (mOnTypeMarkColorPickedListener != null) {
                    mOnTypeMarkColorPickedListener.onTypeMarkColorPicked(mTypeMarkColor);
                }
                getDialog().dismiss();
                break;
        }
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

    public interface OnTypeMarkColorPickedListener {
        void onTypeMarkColorPicked(TypeMarkColor typeMarkColor);
    }

    public void setOnTypeMarkColorPickedListener(OnTypeMarkColorPickedListener listener) {
        mOnTypeMarkColorPickedListener = listener;
    }
}
