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
import me.imzack.lib.basedialogfragment.BaseDialogFragment

class TypeMarkColorPickerDialogFragment : BaseDialogFragment() {

    companion object {

        private val ARG_DEFAULT_COLOR = "default_color"

        fun newInstance(defaultColor: Int, typeMarkColorPickedListener: (typeMarkColor: TypeMarkColor) -> Boolean): TypeMarkColorPickerDialogFragment {
            val fragment = TypeMarkColorPickerDialogFragment()
            val args = Bundle()
            putBaseArguments(
                    args,
                    ResourceUtil.getString(R.string.title_dialog_fragment_type_mark_color_picker),
                    ResourceUtil.getString(R.string.btn_custom),
                    object : OnButtonClickListener {
                        override fun onClick(): Boolean {
                            fragment.onClickSwitcher()
                            return false
                        }
                    },
                    ResourceUtil.getString(android.R.string.cancel),
                    object : OnButtonClickListener {
                        override fun onClick() = true
                    },
                    ResourceUtil.getString(R.string.button_select),
                    object : OnButtonClickListener {
                        override fun onClick() = typeMarkColorPickedListener(fragment.typeMarkColor)
                    },
                    false
            )
            args.putInt(ARG_DEFAULT_COLOR, defaultColor)
            fragment.arguments = args
            return fragment
        }
    }

    // 不提供编程来改变颜色，因为无此需求，即只有通过用户改变颜色
    // TODO 颜色0值是否有效？
    private val defaultColor by lazy { arguments!!.getInt(ARG_DEFAULT_COLOR) }
    private val typeMarkColorList = DataManager.typeMarkColorList
    private var position = -1
    private val typeMarkColor by lazy { if (position == -1) TypeMarkColor(ColorUtil.parseColor(defaultColor)) else typeMarkColorList[position].copy() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // defaultColor 被初始化，position 被更新为正确的初始值
        updatePosition(defaultColor)
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_type_mark_color_picker, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        grid_type_mark_color.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                grid_type_mark_color.viewTreeObserver.removeOnPreDrawListener(this)
                // 运行时设置宽度（在 xml 文件中宽度设置成 match_parent 不行）
                picker_type_mark_color.layoutParams.width = grid_type_mark_color.width
                return false
            }
        })

        grid_type_mark_color.adapter = TypeMarkColorGridAdapter(typeMarkColorList)

        if (position != -1) {
            grid_type_mark_color.setItemChecked(position, true)
        }

        // 预置颜色
        grid_type_mark_color.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            // 更新 position
            position = pos
            // 更新 typeMarkColor
            val (colorHex, colorName) = typeMarkColorList[position]
            typeMarkColor.hex = colorHex
            typeMarkColor.name = colorName
        }

        // 自定义颜色
        picker_type_mark_color.mOnColorChangedListener = {
            // it 为整型颜色值（colorInt），colorHex 为十六进制颜色值
            val colorHex = ColorUtil.parseColor(it)
            // 更新颜色预览图标和颜色文本
            ic_type_mark_color.setFillColor(it)
            text_type_mark_color.text = colorHex
            // 更新 position，如果在自定义颜色中刚好调到某个预置颜色，则 position 为预置颜色中的位置，否则为 -1
            updatePosition(it)
            // 更新 typeMarkColor
            typeMarkColor.hex = colorHex
            // 若 position 为 -1，说明颜色是在自定义颜色中设置的，无名称，用 colorHex 代替；反之说明颜色在预置颜色中存在，有名称
            typeMarkColor.name = if (position == -1) colorHex else typeMarkColorList[position].name
        }
    }

    private fun onClickSwitcher() {
        switcher_color_picker.showNext()
        val colorHex = typeMarkColor.hex
        val color = Color.parseColor(colorHex)
        if (switcher_color_picker.currentView.id == R.id.grid_type_mark_color) {
            //切换到了grid界面，此时position一定为-1
            updatePosition(color)
            if (position != -1) {
                //若picker界面选中的颜色在grid界面也有，选中它
                grid_type_mark_color.setItemChecked(position, true)
            }
            neutralButtonText = getString(R.string.btn_custom)
        } else {
            //切换到了picker界面
            if (position != -1) {
                //若之前在grid界面有选择，取消选择
                grid_type_mark_color.setItemChecked(position, false)
                position = -1
            }
            //此时position一定为-1
            text_type_mark_color.text = colorHex
            ic_type_mark_color.setFillColor(color)
            picker_type_mark_color.setColor(color)
            neutralButtonText = getString(R.string.btn_preset)
        }
    }

    /** 更新 position，如果 color 在预置颜色中，position 设置为预置颜色中的位置，否则设置为 -1 */
    private fun updatePosition(color: Int) {
        position = typeMarkColorList.indices.firstOrNull { typeMarkColorList[it].hex == ColorUtil.parseColor(color) } ?: -1
    }
}
