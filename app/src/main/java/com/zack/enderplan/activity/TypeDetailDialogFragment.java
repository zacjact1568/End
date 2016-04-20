package com.zack.enderplan.activity;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.presenter.TypeDetailPresenter;
import com.zack.enderplan.util.LogUtil;
import com.zack.enderplan.view.TypeDetailView;
import com.zack.enderplan.widget.PlanSingleTypeAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class TypeDetailDialogFragment extends BottomSheetDialogFragment implements TypeDetailView {

    @Bind(R.id.ic_type_mark)
    CircleImageView typeMarkIcon;
    @Bind(R.id.text_first_char)
    TextView firstCharText;
    @Bind(R.id.text_type_name)
    TextView typeNameText;
    @Bind(R.id.text_plan_count)
    TextView planCountText;
    @Bind(R.id.editor_content)
    EditText contentEditor;
    @Bind(R.id.ic_clear_text)
    ImageView clearTextIcon;
    @Bind(R.id.list_uc_plan)
    RecyclerView ucPlanList;

    private static final String LOG_TAG = "TypeDetailDFragment";
    private static final String ARG_POSITION = "position";

    private int position;
    private TypeDetailPresenter typeDetailPresenter;

    public TypeDetailDialogFragment() {
        // Required empty public constructor
    }

    public static TypeDetailDialogFragment newInstance(int position) {
        TypeDetailDialogFragment fragment = new TypeDetailDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }

        typeDetailPresenter = new TypeDetailPresenter(this, position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_type_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        typeDetailPresenter.setInitialView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        typeDetailPresenter.detachView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.ic_edit_type, R.id.ic_clear_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_edit_type:
                getDialog().dismiss();
                EditTypeDialogFragment dialog = EditTypeDialogFragment.newInstance(position);
                dialog.show(getFragmentManager(), "edit_type");
                break;
            case R.id.ic_clear_text:
                contentEditor.setText("");
                break;
        }
    }

    @Override
    public void showInitialView(int typeMarkColorRes, String firstChar, String typeName,
                                String planCountStr, PlanSingleTypeAdapter planSingleTypeAdapter) {
        typeMarkIcon.setImageResource(typeMarkColorRes);
        firstCharText.setText(firstChar);
        typeNameText.setText(typeName);
        planCountText.setText(planCountStr);

        contentEditor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    typeDetailPresenter.notifyPlanCreation(contentEditor.getText().toString());
                }
                return false;
            }
        });

        planSingleTypeAdapter.setOnPlanItemClickListener(new PlanSingleTypeAdapter.OnPlanItemClickListener() {
            @Override
            public void onPlanItemClick(int position, String planCode) {
                typeDetailPresenter.notifyPlanItemClicked(position, planCode);
            }
        });

        ucPlanList.setLayoutManager(new LinearLayoutManager(getActivity()));
        ucPlanList.setHasFixedSize(true);
        ucPlanList.setAdapter(planSingleTypeAdapter);
    }

    @Override
    public void onPlanCreationSuccess(String planCountStr) {
        Toast.makeText(getActivity(), R.string.toast_create_plan_success, Toast.LENGTH_SHORT).show();
        ucPlanList.scrollToPosition(0);
        planCountText.setText(planCountStr);
        contentEditor.setText("");
    }

    @Override
    public void onPlanCreationFailed() {
        Toast.makeText(getActivity(), R.string.toast_create_plan_failed, Toast.LENGTH_SHORT).show();
    }
}
