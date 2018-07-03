package net.zackzhang.app.end.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_fragment_type_picker.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.view.activity.TypeCreationActivity
import net.zackzhang.app.end.view.adapter.TypePickerGridAdapter
import me.imzack.lib.basedialogfragment.BaseDialogFragment

class TypePickerDialogFragment : BaseDialogFragment() {

    companion object {

        private const val ARG_DEFAULT_POSITION = "default_position"

        fun newInstance(defaultPosition: Int): TypePickerDialogFragment {
            val fragment = TypePickerDialogFragment()
            val args = Bundle()
            putAddHorizontalMargins(args, false)
            args.putInt(ARG_DEFAULT_POSITION, defaultPosition)
            fragment.arguments = args
            return fragment
        }
    }

    private var position = -1

    var typePickedListener: ((position: Int) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 为 position 设置正确的初始值
        position = arguments!!.getInt(ARG_DEFAULT_POSITION)
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_type_picker, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleText = ResourceUtil.getString(R.string.title_dialog_fragment_type_picker)

        val typePickerGridAdapter = TypePickerGridAdapter(position)
        typePickerGridAdapter.mOnItemClickListener = { position = it }

        grid_type_picker.adapter = typePickerGridAdapter
        grid_type_picker.setHasFixedSize(true)

        neutralButtonText = ResourceUtil.getString(R.string.btn_new_type)
        neutralButtonClickListener = {
            TypeCreationActivity.start(context!!)
            true
        }
        negativeButtonText = ResourceUtil.getString(android.R.string.cancel)
        negativeButtonClickListener = { true }
        positiveButtonText = ResourceUtil.getString(R.string.button_select)
        positiveButtonClickListener = {
            typePickedListener?.invoke(position)
            true
        }
    }
}
