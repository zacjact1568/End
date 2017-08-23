package me.imzack.app.end.view.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import kotlinx.android.synthetic.main.dialog_fragment_type_mark_color_picker.*
import me.imzack.app.end.R
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.model.bean.TypeMarkColor
import me.imzack.app.end.util.ColorUtil
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.view.adapter.TypeMarkColorGridAdapter
import java.io.Serializable

class TypeMarkColorPickerDialogFragment : BaseDialogFragment() {

    companion object {

        private val ARG_DEFAULT_COLOR = "default_color"
        private val ARG_TYPE_MARK_COLOR_PICKED_LSNR = "type_mark_color_picked_lsnr"

        fun newInstance(defaultColor: String, listener: OnTypeMarkColorPickedListener): TypeMarkColorPickerDialogFragment {
            val fragment = TypeMarkColorPickerDialogFragment()
            val args = Bundle()
            args.putString(ARG_TITLE_STR, ResourceUtil.getString(R.string.title_dialog_fragment_type_mark_color_picker))
            args.putString(ARG_NEU_BTN_STR, ResourceUtil.getString(R.string.btn_custom))
            args.putString(ARG_NEG_BTN_STR, ResourceUtil.getString(R.string.button_cancel))
            args.putString(ARG_POS_BTN_STR, ResourceUtil.getString(R.string.button_select))
            args.putString(ARG_DEFAULT_COLOR, defaultColor)
            args.putSerializable(ARG_TYPE_MARK_COLOR_PICKED_LSNR, listener)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mDefaultColor: String
    private lateinit var mTypeMarkColor: TypeMarkColor
    private var mPosition = -1
    private val mTypeMarkColorList = DataManager.typeMarkColorList
    private var mOnTypeMarkColorPickedListener: OnTypeMarkColorPickedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        if (args != null) {
            mDefaultColor = args.getString(ARG_DEFAULT_COLOR)
            mOnTypeMarkColorPickedListener = args.getSerializable(ARG_TYPE_MARK_COLOR_PICKED_LSNR) as OnTypeMarkColorPickedListener
        }

        mPosition = getPositionInTypeMarkColorList(mDefaultColor)

        mTypeMarkColor = TypeMarkColor(
                mDefaultColor,
                if (mPosition == -1) mDefaultColor else mTypeMarkColorList[mPosition].colorName
        )

        mNeutralButtonClickListener = object : BaseDialogFragment.OnButtonClickListener {
            override fun onClick(): Boolean {
                switcher_color_picker.showNext()
                if (switcher_color_picker.currentView.id == R.id.grid_type_mark_color) {
                    //切换到了grid界面，此时mPosition一定为-1
                    mPosition = getPositionInTypeMarkColorList(mTypeMarkColor.colorHex)
                    if (mPosition != -1) {
                        //若picker界面选中的颜色在grid界面也有，选中它
                        grid_type_mark_color.setItemChecked(mPosition, true)
                    }
                    mNeutralButtonString = getString(R.string.btn_custom)
                    updateNeutralButtonString()
                } else {
                    //切换到了picker界面
                    if (mPosition != -1) {
                        //若之前在grid界面有选择，取消选择
                        grid_type_mark_color.setItemChecked(mPosition, false)
                        mPosition = -1
                    }
                    //此时mPosition一定为-1
                    text_type_mark_color.text = mTypeMarkColor.colorHex
                    ic_type_mark_color.setFillColor(Color.parseColor(mTypeMarkColor.colorHex))
                    picker_type_mark_color.setColor(Color.parseColor(mTypeMarkColor.colorHex))
                    mNeutralButtonString = getString(R.string.btn_preset)
                    updateNeutralButtonString()
                }
                return false
            }
        }
        mPositiveButtonClickListener = object : BaseDialogFragment.OnButtonClickListener {
            override fun onClick(): Boolean {
                mPosition = getPositionInTypeMarkColorList(mTypeMarkColor.colorHex)
                //若position为-1，说明颜色在grid中不存在，是在picker中设置的，无名称，用hex代替；反之说明颜色在grid中存在，有名称
                mTypeMarkColor.colorName = if (mPosition == -1) mTypeMarkColor.colorHex else mTypeMarkColorList[mPosition].colorName
                mOnTypeMarkColorPickedListener?.onTypeMarkColorPicked(mTypeMarkColor)
                return true
            }
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_type_mark_color_picker, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        grid_type_mark_color.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                grid_type_mark_color.viewTreeObserver.removeOnPreDrawListener(this)
                //运行时设置宽度（在xml文件中宽度设置成match_parent不行）
                picker_type_mark_color.layoutParams.width = grid_type_mark_color.width
                return false
            }
        })

        grid_type_mark_color.adapter = TypeMarkColorGridAdapter(mTypeMarkColorList)

        if (mPosition != -1) {
            grid_type_mark_color.setItemChecked(mPosition, true)
        }

        grid_type_mark_color.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            mPosition = position
            mTypeMarkColor.colorHex = mTypeMarkColorList[mPosition].colorHex
        }

        picker_type_mark_color.mOnColorChangedListener = {
            val colorHex = ColorUtil.parseColor(it)
            ic_type_mark_color.setFillColor(it)
            text_type_mark_color.text = colorHex
            mTypeMarkColor.colorHex = colorHex
        }
    }

    override fun onDetach() {
        super.onDetach()
        mOnTypeMarkColorPickedListener = null
    }

    // 若返回-1，则mDefaultColor不是预置的颜色
    private fun getPositionInTypeMarkColorList(color: String) =
            mTypeMarkColorList.indices.firstOrNull { mTypeMarkColorList[it].colorHex == color } ?: -1

    interface OnTypeMarkColorPickedListener : Serializable {
        fun onTypeMarkColorPicked(typeMarkColor: TypeMarkColor)
    }
}
