package com.zack.enderplan.domain.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.activity.PlanDetailActivity;
import com.zack.enderplan.interactor.presenter.TypeDetailPresenter;
import com.zack.enderplan.domain.view.TypeDetailView;
import com.zack.enderplan.interactor.adapter.PlanSingleTypeAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class TypeDetailDialogFragment extends BottomSheetDialogFragment implements TypeDetailView {

    private static final String LOG_TAG = "TypeDetailDFragment";

    @BindView(R.id.ic_type_mark)
    CircleImageView typeMarkIcon;
    @BindView(R.id.text_first_char)
    TextView firstCharText;
    @BindView(R.id.text_type_name)
    TextView typeNameText;
    @BindView(R.id.text_uc_plan_count)
    TextView ucPlanCountText;
    @BindView(R.id.editor_content)
    EditText contentEditor;
    @BindView(R.id.list_uc_plan)
    RecyclerView ucPlanList;

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
    public void showInitialView(int typeMarkColorRes, String firstChar, String typeName,
                                String ucPlanCountStr, PlanSingleTypeAdapter planSingleTypeAdapter) {
        typeMarkIcon.setImageResource(typeMarkColorRes);
        firstCharText.setText(firstChar);
        typeNameText.setText(typeName);
        ucPlanCountText.setText(ucPlanCountStr);

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
            public void onPlanItemClick(int position) {
                typeDetailPresenter.notifyPlanItemClicked(position);
            }
        });

        planSingleTypeAdapter.setOnStarMarkIconClickListener(new PlanSingleTypeAdapter.OnStarMarkIconClickListener() {
            @Override
            public void onStarMarkIconClick(int position) {
                typeDetailPresenter.notifyPlanStarStatusChanged(position);
            }
        });

        ucPlanList.setLayoutManager(new LinearLayoutManager(getActivity()));
        ucPlanList.setHasFixedSize(true);
        ucPlanList.setAdapter(planSingleTypeAdapter);
        new ItemTouchHelper(new SingleTypeUcPlanListItemTouchCallback()).attachToRecyclerView(ucPlanList);
    }

    @Override
    public void onPlanCreationSuccess(String ucPlanCountStr) {
        Toast.makeText(getActivity(), R.string.toast_create_plan_success, Toast.LENGTH_SHORT).show();
        ucPlanList.scrollToPosition(0);
        ucPlanCountText.setText(ucPlanCountStr);
        contentEditor.setText("");
    }

    @Override
    public void onPlanCreationFailed() {
        Toast.makeText(getActivity(), R.string.toast_create_plan_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUcPlanCountChanged(String ucPlanCountStr) {
        ucPlanCountText.setText(ucPlanCountStr);
    }

    @Override
    public void onPlanItemClicked(int posInPlanList) {
        Intent intent = new Intent(getActivity(), PlanDetailActivity.class);
        intent.putExtra("position", posInPlanList);
        getActivity().startActivity(intent);
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

    private class SingleTypeUcPlanListItemTouchCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int swipeFlags = ItemTouchHelper.END;
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getLayoutPosition();
            switch (direction) {
                case ItemTouchHelper.END:
                    typeDetailPresenter.notifyPlanCompleted(position);
                    break;
                default:
                    break;
            }
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return .6f;
        }
    }
}
