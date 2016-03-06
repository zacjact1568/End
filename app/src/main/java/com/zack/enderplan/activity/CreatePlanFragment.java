package com.zack.enderplan.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.widget.DateTimePicker;
import com.zack.enderplan.widget.TypeSpinnerAdapter;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.util.Util;

import java.util.List;

public class CreatePlanFragment extends DialogFragment {

    private EnderPlanDB enderplanDB;
    private View view;
    private EditText content;
    private TextView contentError;
    private RelativeLayout relativeLayout;
    private List<Type> typeList;
    private String typeCodeStr;
    private boolean enabledMoreSettings = false;
    private long deadlineInMillis = 0;
    private boolean enabledReminder = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enderplanDB = EnderPlanDB.getInstance(getActivity());

        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_create_plan, null);

        content = (EditText) view.findViewById(R.id.content);
        contentError = (TextView) view.findViewById(R.id.content_error);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_box);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout);
        DateTimePicker dateTimePicker = (DateTimePicker) view.findViewById(R.id.date_time_picker);
        Switch reminderSwitch = (Switch) view.findViewById(R.id.reminder_switch);

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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
        TypeSpinnerAdapter typeSpinnerAdapter = new TypeSpinnerAdapter(getActivity());

        spinner.setAdapter(typeSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeCodeStr = typeList.get(position).getTypeCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                relativeLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                int colorId = isChecked ? R.color.colorAccent : R.color.grey;
                buttonView.setTextColor(ContextCompat.getColor(getActivity(), colorId));
                enabledMoreSettings = isChecked;
            }
        });

        relativeLayout.setVisibility(View.GONE);

        dateTimePicker.setOnDateTimeChangeListener(new DateTimePicker.OnDateTimeChangeListener() {
            @Override
            public void onDateTimeChange(DateTimePicker picker, long milliseconds) {
                deadlineInMillis = milliseconds;
            }
        });

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int colorId = isChecked ? R.color.colorAccent : R.color.grey;
                buttonView.setTextColor(ContextCompat.getColor(getActivity(), colorId));
                enabledReminder = isChecked;
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.create_plan).setView(view).setPositiveButton(R.string.create, null)
                .setNegativeButton(R.string.cancel, null);
        final AlertDialog dialog = builder.show();//TODO show()在HomeActivity中已经有了
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentStr = content.getText().toString();
                long currentTimeMillis = System.currentTimeMillis();
                if (TextUtils.isEmpty(contentStr)) {
                    contentError.setText(getResources().getString(R.string.content_error_empty));
                    contentError.setVisibility(View.VISIBLE);
                    return;
                }
                if (enabledMoreSettings && deadlineInMillis < currentTimeMillis) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.message_deadline_overdue_dialog)
                            .setPositiveButton(R.string.pos_button_deadline_overdue_dialog, null).show();
                    return;
                }

                Plan plan = new Plan(Util.makeCode(), contentStr, typeCodeStr, currentTimeMillis,
                        deadlineInMillis, 0, 0, 0);
                enderplanDB.savePlan(plan);

                if (enabledReminder) {
                    ReminderManager manager = new ReminderManager(getActivity());
                    manager.setAlarm(plan.getPlanCode(), deadlineInMillis);
                }

                OnPlanCreatedListener listener = (OnPlanCreatedListener) getActivity();
                listener.onPlanCreated(plan);

                dialog.cancel();
            }
        });
        return dialog;
    }

    public interface OnPlanCreatedListener {
        void onPlanCreated(Plan plan);
    }
}
