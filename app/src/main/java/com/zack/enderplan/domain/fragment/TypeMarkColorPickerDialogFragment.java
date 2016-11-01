package com.zack.enderplan.domain.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.larswerkman.lobsterpicker.LobsterPicker;
import com.larswerkman.lobsterpicker.OnColorListener;
import com.larswerkman.lobsterpicker.sliders.LobsterShadeSlider;
import com.zack.enderplan.R;
import com.zack.enderplan.interactor.adapter.TypeMarkColorAdapter;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.utility.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A {@link DialogFragment} that includes a color picker.
 *
 * @author Zack
 */
public class TypeMarkColorPickerDialogFragment extends DialogFragment {

    @BindView(R.id.text_color_name)
    TextView mColorNameText;
    @BindView(R.id.grid_type_mark_color)
    GridView mTypeMarkColorGrid;
    @BindView(R.id.btn_picker_switcher)
    ImageView mPickerSwitcherButton;
    @BindView(R.id.picker_color)
    LobsterPicker mColorPicker;
    @BindView(R.id.slider_shade)
    LobsterShadeSlider mShadeSlider;
    @BindView(R.id.switcher_color_picker)
    ViewSwitcher mColorPickerSwitcher;

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

        mTypeMarkColor = new TypeMarkColor();

        for (int i = 0; i < mTypeMarkColorList.size(); i++) {
            TypeMarkColor typeMarkColor = mTypeMarkColorList.get(i);
            if (typeMarkColor.getColorHex().equals(mDefaultColor)) {
                mPosition = i;
                mTypeMarkColor.setColor(typeMarkColor.getColorHex(), typeMarkColor.getColorName());
            }
            if (i == mTypeMarkColorList.size() - 1) {
                //未在TypeMarkColorList中找到
                mTypeMarkColor.setColor(mDefaultColor);
            }
        }
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

        mColorNameText.setText(mTypeMarkColor.getColorName());

        mTypeMarkColorGrid.setAdapter(new TypeMarkColorAdapter(mTypeMarkColorList));

        if (mPosition != -1) {
            mTypeMarkColorGrid.setItemChecked(mPosition, true);
        }

        mTypeMarkColorGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
                TypeMarkColor typeMarkColor = mTypeMarkColorList.get(mPosition);
                mTypeMarkColor.setColor(typeMarkColor.getColorHex(), typeMarkColor.getColorName());
                mColorNameText.setText(typeMarkColor.getColorName());
            }
        });

        mColorPicker.addDecorator(mShadeSlider);
        mColorPicker.addOnColorListener(new OnColorListener() {
            @Override
            public void onColorChanged(int color) {
                String colorHex = Util.parseColor(color);
                mTypeMarkColor.setColor(colorHex);
                mColorNameText.setText(colorHex);
            }

            @Override
            public void onColorSelected(int color) {

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
                    //切换到了Grid界面
                    if (mPosition != -1) {
                        TypeMarkColor typeMarkColor = mTypeMarkColorList.get(mPosition);
                        mTypeMarkColor.setColor(typeMarkColor.getColorHex(), typeMarkColor.getColorName());
                    } else {
                        mTypeMarkColor.setColor(mDefaultColor);
                    }
                    mColorNameText.setText(mTypeMarkColor.getColorName());
                    mPickerSwitcherButton.setImageResource(R.drawable.ic_palette_black_24dp);
                } else {
                    //切换到了Picker界面
                    String colorHex = Util.parseColor(mColorPicker.getColor());
                    mTypeMarkColor.setColor(colorHex);
                    mColorNameText.setText(colorHex);
                    mPickerSwitcherButton.setImageResource(R.drawable.ic_apps_black_24dp);
                }
                break;
            case R.id.btn_cancel:
                getDialog().dismiss();
                break;
            case R.id.btn_select:
                if (mOnTypeMarkColorPickedListener != null) {
                    mOnTypeMarkColorPickedListener.onTypeMarkColorPicked(mTypeMarkColor);
                }
                getDialog().dismiss();
                break;
        }
    }

    public interface OnTypeMarkColorPickedListener {
        void onTypeMarkColorPicked(TypeMarkColor typeMarkColor);
    }

    public void setOnTypeMarkColorPickedListener(OnTypeMarkColorPickedListener listener) {
        mOnTypeMarkColorPickedListener = listener;
    }
}
