package com.zack.enderplan.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.widget.TypeSpinnerAdapter;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.bean.Type;

import java.util.List;

public class PlanDetailFragment extends DialogFragment {

    private static final String ARG_PLAN_DETAIL = "plan_detail";

    private Plan plan;
    private EnderPlanDB enderplanDB;
    private View view;
    private EditText content;
    private TextView contentError;
    private List<Type> typeList;
    private String typeCodeStr;

    public static PlanDetailFragment newInstance(Plan plan) {
        PlanDetailFragment fragment = new PlanDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PLAN_DETAIL, plan);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            plan = getArguments().getParcelable(ARG_PLAN_DETAIL);
        }

        enderplanDB = EnderPlanDB.getInstance(getActivity());

        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_plan_detail, null);

        content = (EditText) view.findViewById(R.id.content);
        contentError = (TextView) view.findViewById(R.id.content_error);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

        content.setText(plan.getContent());
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //TODO Validate the input text here
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (contentError.getVisibility() == View.VISIBLE) {
                    contentError.setVisibility(View.GONE);
                }
            }
        });

        contentError.setVisibility(View.GONE);

        typeList = enderplanDB.loadType();
        TypeSpinnerAdapter typeSpinnerAdapter = new TypeSpinnerAdapter(getActivity(), typeList);

        spinner.setAdapter(typeSpinnerAdapter);

        for (int i = 0; i < typeList.size(); i++) {
            if (typeList.get(i).getTypeCode().equals(plan.getTypeCode())) {
                spinner.setSelection(i);
                break;
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //事实上在初始化spinner时，就算不打开spinner，这个方法体内的语句也会执行一遍
                //而且position就是默认选中的那个item的position
                //所以不需要将typeCodeStr预先初始化一遍
                typeCodeStr = typeList.get(position).getTypeCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.edit_plan).setView(view).setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null);
        final AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentStr = content.getText().toString();
                if (TextUtils.isEmpty(contentStr)) {
                    contentError.setText(getResources().getString(R.string.content_error_empty));
                    contentError.setVisibility(View.VISIBLE);
                    return;
                }

                ContentValues contentValues = new ContentValues();
                contentValues.put(EnderPlanDB.DB_STR_CONTENT, contentStr);
                contentValues.put(EnderPlanDB.DB_STR_TYPE_CODE, typeCodeStr);
                enderplanDB.editPlan(plan.getPlanCode(), contentValues);

                //更新plan (HomeActivity的planList中的plan也会更新)
                plan.setContent(contentStr);
                plan.setTypeCode(typeCodeStr);

                /*if (plan.getReminderStatus() == Plan.PLAN_REMINDER_ENABLED) {
                    ReminderManager manager = new ReminderManager(getActivity());
                    manager.setAlarm(plan);
                }*/

                OnPlanEditedListener listener = (OnPlanEditedListener) getActivity();
                listener.onPlanEdited();

                dialog.cancel();
            }
        });
        return dialog;
    }

    public interface OnPlanEditedListener {
        void onPlanEdited();
    }
}
