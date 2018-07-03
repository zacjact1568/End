package net.zackzhang.app.end.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_fragment_type_picker_for_plan_migration.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.model.DataManager
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.view.adapter.TypePickerForPlanMigrationGridAdapter
import me.imzack.lib.basedialogfragment.BaseDialogFragment
import java.io.Serializable

class TypePickerForPlanMigrationDialogFragment : BaseDialogFragment() {

    companion object {

        private const val ARG_EXCLUDED_TYPE_CODE = "excluded_type_code"

        fun newInstance(excludedTypeCode: String): TypePickerForPlanMigrationDialogFragment {
            val fragment = TypePickerForPlanMigrationDialogFragment()
            val args = Bundle()
            putAddHorizontalMargins(args, false)
            args.putString(ARG_EXCLUDED_TYPE_CODE, excludedTypeCode)
            fragment.arguments = args
            return fragment
        }
    }

    var typePickedListener: ((typeCode: String, typeName: String) -> Unit)? = null

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_type_picker_for_plan_migration, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val excludedTypeCode = arguments!!.getString(ARG_EXCLUDED_TYPE_CODE)

        titleText = ResourceUtil.getQuantityString(R.string.title_dialog_type_picker_for_plan_migration, R.plurals.text_plan_count_upper_case, DataManager.getUcPlanCountOfOneType(excludedTypeCode))

        val adapter = TypePickerForPlanMigrationGridAdapter(excludedTypeCode)
        adapter.mOnItemClickListener = { typeCode, typeName ->
            typePickedListener?.invoke(typeCode, typeName)
            dialog.dismiss()
        }

        grid_type_picker_for_plan_migration.adapter = adapter
        grid_type_picker_for_plan_migration.setHasFixedSize(true)

        negativeButtonText = ResourceUtil.getString(android.R.string.cancel)
        negativeButtonClickListener = { true }
    }
}
