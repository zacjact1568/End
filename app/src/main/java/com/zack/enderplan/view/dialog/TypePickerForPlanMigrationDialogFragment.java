package com.zack.enderplan.view.dialog;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zack.enderplan.R;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.view.adapter.TypePickerForPlanMigrationGridAdapter;

import butterknife.BindView;

public class TypePickerForPlanMigrationDialogFragment extends BaseDialogFragment {

    @BindView(R.id.grid_type_picker_for_plan_migration)
    RecyclerView mTypePickerForPlanMigrationGrid;

    private static final String ARG_EXCLUDED_TYPE_CODE = "excluded_type_code";

    private OnTypePickedListener mOnTypePickedListener;
    private String mExcludedTypeCode;

    public TypePickerForPlanMigrationDialogFragment() {

    }

    public static TypePickerForPlanMigrationDialogFragment newInstance(String excludedTypeCode) {
        TypePickerForPlanMigrationDialogFragment fragment = new TypePickerForPlanMigrationDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, ResourceUtil.getQuantityString(R.string.title_dialog_type_picker_for_plan_migration, R.plurals.text_plan_count_upper_case, DataManager.getInstance().getUcPlanCountOfOneType(excludedTypeCode)));
        args.putString(ARG_NEG_BTN, ResourceUtil.getString(R.string.button_cancel));
        args.putString(ARG_EXCLUDED_TYPE_CODE, excludedTypeCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mExcludedTypeCode = args.getString(ARG_EXCLUDED_TYPE_CODE);
        }
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup root) {
        return inflater.inflate(R.layout.dialog_fragment_type_picker_for_plan_migration, root, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TypePickerForPlanMigrationGridAdapter adapter = new TypePickerForPlanMigrationGridAdapter(DataManager.getInstance(), mExcludedTypeCode);
        adapter.setOnItemClickListener(new TypePickerForPlanMigrationGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String typeCode, String typeName) {
                if (mOnTypePickedListener != null) {
                    mOnTypePickedListener.onTypePicked(typeCode, typeName);
                }
                getDialog().dismiss();
            }
        });

        mTypePickerForPlanMigrationGrid.setAdapter(adapter);
        mTypePickerForPlanMigrationGrid.setHasFixedSize(true);
    }

    @Override
    public boolean onButtonClicked(int which) {
        switch (which) {
            case BTN_NEG:
                break;
        }
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnTypePickedListener = null;
    }

    public interface OnTypePickedListener {
        void onTypePicked(String typeCode, String typeName);
    }

    public void setOnTypePickedListener(OnTypePickedListener listener) {
        mOnTypePickedListener = listener;
    }
}
