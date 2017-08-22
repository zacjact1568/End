package me.imzack.app.end.view.dialog

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import me.imzack.app.end.R
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.view.adapter.TypePickerForPlanMigrationGridAdapter
import java.io.Serializable

class TypePickerForPlanMigrationDialogFragment : BaseDialogFragment() {

    companion object {

        private val ARG_EXCLUDED_TYPE_CODE = "excluded_type_code"
        private val ARG_TYPE_PICKED_LSNR = "type_picked_lsnr"

        fun newInstance(excludedTypeCode: String, listener: OnTypePickedListener): TypePickerForPlanMigrationDialogFragment {
            val fragment = TypePickerForPlanMigrationDialogFragment()
            val args = Bundle()
            args.putString(ARG_TITLE_STR, ResourceUtil.getQuantityString(R.string.title_dialog_type_picker_for_plan_migration, R.plurals.text_plan_count_upper_case, DataManager.getUcPlanCountOfOneType(excludedTypeCode)))
            args.putString(ARG_NEG_BTN_STR, ResourceUtil.getString(R.string.button_cancel))
            args.putString(ARG_EXCLUDED_TYPE_CODE, excludedTypeCode)
            args.putSerializable(ARG_TYPE_PICKED_LSNR, listener)
            fragment.arguments = args
            return fragment
        }
    }
    
    @BindView(R.id.grid_type_picker_for_plan_migration)
    lateinit var mTypePickerForPlanMigrationGrid: RecyclerView

    private lateinit var mExcludedTypeCode: String
    private var mOnTypePickedListener: OnTypePickedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        if (args != null) {
            mExcludedTypeCode = args.getString(ARG_EXCLUDED_TYPE_CODE)
            mOnTypePickedListener = args.getSerializable(ARG_TYPE_PICKED_LSNR) as OnTypePickedListener
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_type_picker_for_plan_migration, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TypePickerForPlanMigrationGridAdapter(mExcludedTypeCode)
        adapter.mOnItemClickListener = { typeCode, typeName ->
            mOnTypePickedListener?.onTypePicked(typeCode, typeName)
            dialog.dismiss()
        }

        mTypePickerForPlanMigrationGrid.adapter = adapter
        mTypePickerForPlanMigrationGrid.setHasFixedSize(true)
    }

    override fun onDetach() {
        super.onDetach()
        mOnTypePickedListener = null
    }

    interface OnTypePickedListener : Serializable {
        fun onTypePicked(typeCode: String, typeName: String)
    }
}
