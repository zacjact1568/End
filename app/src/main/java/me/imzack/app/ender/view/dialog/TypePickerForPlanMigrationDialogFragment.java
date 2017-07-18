package me.imzack.app.ender.view.dialog;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.imzack.app.ender.R;
import me.imzack.app.ender.model.DataManager;
import me.imzack.app.ender.util.ResourceUtil;
import me.imzack.app.ender.view.adapter.TypePickerForPlanMigrationGridAdapter;

import java.io.Serializable;

import butterknife.BindView;

public class TypePickerForPlanMigrationDialogFragment extends BaseDialogFragment {

    @BindView(R.id.grid_type_picker_for_plan_migration)
    RecyclerView mTypePickerForPlanMigrationGrid;

    private static final String ARG_EXCLUDED_TYPE_CODE = "excluded_type_code";
    private static final String ARG_TYPE_PICKED_LSNR = "type_picked_lsnr";

    private OnTypePickedListener mOnTypePickedListener;
    private String mExcludedTypeCode;

    public TypePickerForPlanMigrationDialogFragment() {

    }

    public static TypePickerForPlanMigrationDialogFragment newInstance(String excludedTypeCode, OnTypePickedListener listener) {
        TypePickerForPlanMigrationDialogFragment fragment = new TypePickerForPlanMigrationDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE_STR, ResourceUtil.getQuantityString(R.string.title_dialog_type_picker_for_plan_migration, R.plurals.text_plan_count_upper_case, DataManager.getInstance().getUcPlanCountOfOneType(excludedTypeCode)));
        args.putString(ARG_NEG_BTN_STR, ResourceUtil.getString(R.string.button_cancel));
        args.putString(ARG_EXCLUDED_TYPE_CODE, excludedTypeCode);
        args.putSerializable(ARG_TYPE_PICKED_LSNR, listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mExcludedTypeCode = args.getString(ARG_EXCLUDED_TYPE_CODE);
            mOnTypePickedListener = (OnTypePickedListener) args.getSerializable(ARG_TYPE_PICKED_LSNR);
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
    public void onDetach() {
        super.onDetach();
        mOnTypePickedListener = null;
    }

    public interface OnTypePickedListener extends Serializable {
        void onTypePicked(String typeCode, String typeName);
    }
}
