package me.imzack.app.end.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_fragment_type_picker.*
import me.imzack.app.end.R
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.view.activity.TypeCreationActivity
import me.imzack.app.end.view.adapter.TypePickerGridAdapter
import me.imzack.lib.basedialogfragment.BaseDialogFragment

class TypePickerDialogFragment : BaseDialogFragment() {

    companion object {

        private val ARG_DEFAULT_POSITION = "default_position"

        fun newInstance(defaultPosition: Int, typePickedListener: (position: Int) -> Unit): TypePickerDialogFragment {
            val fragment = TypePickerDialogFragment()
            val args = Bundle()
            putBaseArguments(
                    args,
                    ResourceUtil.getString(R.string.title_dialog_fragment_type_picker),
                    ResourceUtil.getString(R.string.btn_new_type),
                    object : OnButtonClickListener {
                        override fun onClick(): Boolean {
                            TypeCreationActivity.start(fragment.context)
                            return true
                        }
                    },
                    ResourceUtil.getString(android.R.string.cancel),
                    object : OnButtonClickListener {
                        override fun onClick() = true
                    },
                    ResourceUtil.getString(R.string.button_select),
                    object : OnButtonClickListener {
                        override fun onClick(): Boolean {
                            typePickedListener(fragment.position)
                            return true
                        }
                    },
                    false
            )
            args.putInt(ARG_DEFAULT_POSITION, defaultPosition)
            fragment.arguments = args
            return fragment
        }
    }

    private var position = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 为 position 设置正确的初始值
        position = arguments.getInt(ARG_DEFAULT_POSITION)
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_type_picker, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val typePickerGridAdapter = TypePickerGridAdapter(position)
        typePickerGridAdapter.mOnItemClickListener = { position = it }

        grid_type_picker.adapter = typePickerGridAdapter
        grid_type_picker.setHasFixedSize(true)
    }
}
