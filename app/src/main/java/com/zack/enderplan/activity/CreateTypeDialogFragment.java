package com.zack.enderplan.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.bean.TypeMark;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.presenter.CreateTypePresenter;
import com.zack.enderplan.util.Util;
import com.zack.enderplan.view.CreateTypeView;
import com.zack.enderplan.widget.TypeMarkAdapter;

import java.util.List;

import butterknife.BindColor;

public class CreateTypeDialogFragment extends DialogFragment implements CreateTypeView {

    //private static final String ARG_ACTIVE_TYPE_MARKS = "active_type_marks";

    private CreateTypePresenter createTypePresenter;
    /*private Type type;
    private List<TypeMark> typeMarkList;
    private TypeMarkAdapter typeMarkAdapter;*/
    private TextView saveButton;
    private int clickedPosition = -1;
    private int lastClickedPosition = -1;
    private int selectedTypeMarkResId;
    private boolean isTypeNameNotEmpty, isTypeMarkSelected;
    private int negativeColor, positiveColor;
    //private boolean isValidTypeMarkExists = true;
    //private OnTypeCreatedListener onTypeCreatedListener;

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

        /*if (getActivity() instanceof OnTypeCreatedListener) {
            onTypeCreatedListener = (OnTypeCreatedListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnTypeCreatedListener");
        }*/

        negativeColor = ContextCompat.getColor(getActivity(), android.R.color.secondary_text_light_nodisable);
        positiveColor = ContextCompat.getColor(getActivity(), R.color.colorPrimary);

        createTypePresenter = new CreateTypePresenter(this);

        //自动预选一个typeMark
        /*for (int i = 0; i < typeMarkList.size(); i++) {
            TypeMark typeMark = typeMarkList.get(i);
            if (typeMark.isValid()) {
                typeMark.setIsSelected(true);
                clickedPosition = i;
                break;
            }
            if (i == typeMarkList.size() - 1) {
                //能进入这里，说明没有一个typeMark是可用的了
                isValidTypeMarkExists = false;
                //TODO 处理没有可用typeMark的情况
            }
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_fragment_create_type, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputLayout typeNameEditorWrapper = (TextInputLayout) view.findViewById(R.id.wrapper_type_name_editor);
        GridView gridView = (GridView) view.findViewById(R.id.grid_view);
        TextView cancelButton = (TextView) view.findViewById(R.id.button_cancel);
        saveButton = (TextView) view.findViewById(R.id.button_save);

        TextInputEditText typeNameEditor;

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
                createTypePresenter.notifyTypeNameChanged(s.toString());
                isTypeNameNotEmpty = !TextUtils.isEmpty(s.toString());
                updateSaveButton();
            }
        });

        gridView.setAdapter(createTypePresenter.createTypeMarkAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastClickedPosition = clickedPosition;
                clickedPosition = position;
                createTypePresenter.notifyTypeMarkClicked(lastClickedPosition, clickedPosition);
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
                createTypePresenter.createNewType(Util.parseColor(ContextCompat.getColor(getActivity(), selectedTypeMarkResId)));
                //EnderPlanDB.getInstance().saveType(type);
                getDialog().dismiss();
                //onTypeCreatedListener.onTypeCreated(type);
            }
        });

        //必须放在这里，如果放在布局文件中的话，上一句setOnClickListener会将clickable设成true，就没用了
        saveButton.setClickable(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //onTypeCreatedListener = null;
        createTypePresenter.detachView();
    }

    private void updateSaveButton() {
        boolean isSaveButtonEnable = isTypeNameNotEmpty && isTypeMarkSelected;
        saveButton.setClickable(isSaveButtonEnable);
        saveButton.setTextColor(isSaveButtonEnable ? positiveColor : negativeColor);
    }

    @Override
    public void onTypeMarkClicked(boolean isTypeMarkSelected, int resId) {
        this.isTypeMarkSelected = isTypeMarkSelected;
        //传过来的resId可能为0，但不影响保存按钮可用与否的判断
        selectedTypeMarkResId = resId;
        updateSaveButton();
    }

    /*public interface OnTypeCreatedListener {
        void onTypeCreated(Type type);
    }*/
}
