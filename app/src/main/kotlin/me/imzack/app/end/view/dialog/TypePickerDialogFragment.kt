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
import java.io.Serializable

class TypePickerDialogFragment : BaseDialogFragment() {

    companion object {

        private val ARG_DEFAULT_POSITION = "default_position"
        private val ARG_TYPE_PICKED_LSNR = "type_picked_lsnr"

        fun newInstance(defaultPosition: Int, listener: OnTypePickedListener): TypePickerDialogFragment {
            val fragment = TypePickerDialogFragment()
            val args = Bundle()
            args.putString(ARG_TITLE_STR, ResourceUtil.getString(R.string.title_dialog_fragment_type_picker))
            args.putString(ARG_NEU_BTN_STR, ResourceUtil.getString(R.string.btn_new_type))
            args.putString(ARG_NEG_BTN_STR, ResourceUtil.getString(R.string.button_cancel))
            args.putString(ARG_POS_BTN_STR, ResourceUtil.getString(R.string.button_select))
            args.putInt(ARG_DEFAULT_POSITION, defaultPosition)
            args.putSerializable(ARG_TYPE_PICKED_LSNR, listener)
            fragment.arguments = args
            return fragment
        }
    }

    private var mPosition = -1
    private var mOnTypePickedListener: OnTypePickedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        if (args != null) {
            mPosition = args.getInt(ARG_DEFAULT_POSITION, -1)
            mOnTypePickedListener = args.getSerializable(ARG_TYPE_PICKED_LSNR) as OnTypePickedListener
        }

        mNeutralButtonClickListener = object : BaseDialogFragment.OnButtonClickListener {
            override fun onClick(): Boolean {
                TypeCreationActivity.start(context)
                return true
            }
        }
        mPositiveButtonClickListener = object : BaseDialogFragment.OnButtonClickListener {
            override fun onClick(): Boolean {
                mOnTypePickedListener?.onTypePicked(mPosition)
                return true
            }
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_type_picker, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val typePickerGridAdapter = TypePickerGridAdapter(mPosition)
        typePickerGridAdapter.mOnItemClickListener = { mPosition = it }

        grid_type_picker.adapter = typePickerGridAdapter
        grid_type_picker.setHasFixedSize(true)
    }

    override fun onDetach() {
        super.onDetach()
        mOnTypePickedListener = null
    }

    interface OnTypePickedListener : Serializable {
        fun onTypePicked(position: Int)
    }
}
