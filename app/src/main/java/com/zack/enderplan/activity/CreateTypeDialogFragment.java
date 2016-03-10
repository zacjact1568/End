package com.zack.enderplan.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.bean.TypeMark;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.manager.TypeManager;
import com.zack.enderplan.util.Util;
import com.zack.enderplan.widget.TypeMarkAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateTypeDialogFragment extends DialogFragment {

    //private static final String ARG_ACTIVE_TYPE_MARKS = "active_type_marks";

    private Type type;
    private List<TypeMark> typeMarkList;
    private TypeMarkAdapter typeMarkAdapter;
    private TextInputEditText typeNameEditor;
    private TextView saveButton;
    private int selectedPosition = -1;
    private int lastSelectedPosition = -1;
    private int negativeColor, positiveColor;
    private boolean isTypeNameNotEmpty, isTypeMarkSelected;
    //private boolean isValidTypeMarkExists = true;
    private OnTypeCreatedListener onTypeCreatedListener;

    public CreateTypeDialogFragment() {
        // Required empty public constructor
    }

    /*public static CreateTypeDialogFragment newInstance(List<String> activeTypeMarks) {
        CreateTypeDialogFragment fragment = new CreateTypeDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_ACTIVE_TYPE_MARKS, (ArrayList<String>) activeTypeMarks);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            activeTypeMarks = getArguments().getStringArrayList(ARG_ACTIVE_TYPE_MARKS);
        }*/

        if (getActivity() instanceof OnTypeCreatedListener) {
            onTypeCreatedListener = (OnTypeCreatedListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnTypeCreatedListener");
        }

        type = new Type(Util.makeCode());

        TypeManager typeManager = TypeManager.getInstance();
        typeMarkList = typeManager.getTypeMarkList();
        //自动预选一个typeMark
        /*for (int i = 0; i < typeMarkList.size(); i++) {
            TypeMark typeMark = typeMarkList.get(i);
            if (typeMark.isValid()) {
                typeMark.setIsSelected(true);
                selectedPosition = i;
                break;
            }
            if (i == typeMarkList.size() - 1) {
                //能进入这里，说明没有一个typeMark是可用的了
                isValidTypeMarkExists = false;
                //TODO 处理没有可用typeMark的情况
            }
        }*/
        typeMarkAdapter = new TypeMarkAdapter(typeMarkList);

        negativeColor = ContextCompat.getColor(getActivity(), android.R.color.secondary_text_light_nodisable);
        positiveColor = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_fragment_create_type, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputLayout typeNameEditorWrapper = (TextInputLayout) view.findViewById(R.id.wrapper_type_name_editor);
        GridView gridView = (GridView) view.findViewById(R.id.grid_view);
        TextView cancelButton = (TextView) view.findViewById(R.id.button_cancel);
        saveButton = (TextView) view.findViewById(R.id.button_save);

        if (typeNameEditorWrapper.getEditText() != null) {
            typeNameEditor = (TextInputEditText) typeNameEditorWrapper.getEditText();
        } else {
            throw new RuntimeException("TextInputLayout has to warp an EditText or its descendant");
        }

        typeNameEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isTypeNameNotEmpty = !TextUtils.isEmpty(s.toString());
                updateSaveButton();
            }
        });

        gridView.setAdapter(typeMarkAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastSelectedPosition = selectedPosition;
                selectedPosition = position;
                TypeMark typeMark = typeMarkList.get(position);
                typeMark.setIsSelected(!typeMark.isSelected());
                if (lastSelectedPosition != -1 && selectedPosition != lastSelectedPosition) {
                    typeMarkList.get(lastSelectedPosition).setIsSelected(false);
                }
                typeMarkAdapter.notifyDataSetChanged();
                isTypeMarkSelected = typeMark.isSelected();
                updateSaveButton();
                /*if (selectedPosition == -1) {
                    //第一次点选
                    selectedPosition = position;
                    typeMark.setIsSelected(true);
                    typeMarkAdapter.notifyDataSetChanged();
                } else if (position != selectedPosition) {
                    //两次点击的不是同一个
                    lastSelectedPosition = selectedPosition;
                    typeMarkList.get(lastSelectedPosition).setIsSelected(false);
                    selectedPosition = position;
                    typeMark.setIsSelected(true);
                    typeMarkAdapter.notifyDataSetChanged();
                } else if (typeMark.isSelected()) {
                    //两次点击的是同一个，当前点击的是已选中的
                    typeMark.setIsSelected(false);
                    selectedPosition = lastSelectedPosition = -1;
                    typeMarkAdapter.notifyDataSetChanged();
                } else {
                    Log.d("CTDF", "特殊情况");
                }*/
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type.setTypeName(typeNameEditor.getText().toString());
                type.setTypeMark(Util.parseColor(ContextCompat.getColor(getActivity(),
                        typeMarkList.get(selectedPosition).getResId())));
                EnderPlanDB.getInstance().saveType(type);
                getDialog().dismiss();
                onTypeCreatedListener.onTypeCreated(type);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onTypeCreatedListener = null;
    }

    private void updateSaveButton() {
        boolean isSaveButtonEnable = isTypeNameNotEmpty && isTypeMarkSelected;
        saveButton.setClickable(isSaveButtonEnable);
        saveButton.setTextColor(isSaveButtonEnable ? positiveColor : negativeColor);
    }

    public interface OnTypeCreatedListener {
        void onTypeCreated(Type type);
    }
}
