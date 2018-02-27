package me.imzack.app.end.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_fragment_type_picker_for_plan_migration.*
import me.imzack.app.end.R
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.view.adapter.TypePickerForPlanMigrationGridAdapter
import me.imzack.lib.basedialogfragment.BaseDialogFragment
import java.io.Serializable

class TypePickerForPlanMigrationDialogFragment : BaseDialogFragment() {

    companion object {

        private val ARG_EXCLUDED_TYPE_CODE = "excluded_type_code"
        private val ARG_TYPE_PICKED_LISTENER = "type_picked_listener"

        fun newInstance(excludedTypeCode: String, typePickedListener: OnTypePickedListener): TypePickerForPlanMigrationDialogFragment {
            val fragment = TypePickerForPlanMigrationDialogFragment()
            val args = Bundle()
            putBaseArguments(
                    args,
                    ResourceUtil.getQuantityString(R.string.title_dialog_type_picker_for_plan_migration, R.plurals.text_plan_count_upper_case, DataManager.getUcPlanCountOfOneType(excludedTypeCode)),
                    null,
                    null,
                    ResourceUtil.getString(android.R.string.cancel),
                    object : OnButtonClickListener {
                        override fun onClick() = true
                    },
                    addHorizontalMargins = false
            )
            args.putString(ARG_EXCLUDED_TYPE_CODE, excludedTypeCode)
            args.putSerializable(ARG_TYPE_PICKED_LISTENER, typePickedListener)
            fragment.arguments = args
            return fragment
        }
    }

    private val typePickedListener by lazy { arguments!!.getSerializable(ARG_TYPE_PICKED_LISTENER) as OnTypePickedListener }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_type_picker_for_plan_migration, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TypePickerForPlanMigrationGridAdapter(arguments!!.getString(ARG_EXCLUDED_TYPE_CODE))
        adapter.mOnItemClickListener = { typeCode, typeName ->
            typePickedListener.onTypePicked(typeCode, typeName)
            dialog.dismiss()
        }

        grid_type_picker_for_plan_migration.adapter = adapter
        grid_type_picker_for_plan_migration.setHasFixedSize(true)
    }

    interface OnTypePickedListener : Serializable {
        fun onTypePicked(typeCode: String, typeName: String)
    }
}
