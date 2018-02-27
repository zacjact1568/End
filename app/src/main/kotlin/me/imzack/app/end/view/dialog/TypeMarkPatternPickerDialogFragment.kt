package me.imzack.app.end.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import kotlinx.android.synthetic.main.dialog_fragment_type_mark_pattern_picker.*
import me.imzack.app.end.R
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.model.bean.Type
import me.imzack.app.end.model.bean.TypeMarkPattern
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.view.adapter.TypeMarkPatternGridAdapter
import me.imzack.lib.basedialogfragment.BaseDialogFragment

class TypeMarkPatternPickerDialogFragment : BaseDialogFragment() {

    companion object {

        private val ARG_DEFAULT_PATTERN = "default_pattern"

        fun newInstance(defaultPattern: String?, typeMarkPatternPickedListener: (typeMarkPattern: TypeMarkPattern?) -> Boolean): TypeMarkPatternPickerDialogFragment {
            val fragment = TypeMarkPatternPickerDialogFragment()
            val args = Bundle()
            putBaseArguments(
                    args,
                    ResourceUtil.getString(R.string.title_dialog_fragment_type_mark_pattern_picker),
                    ResourceUtil.getString(R.string.button_remove),
                    object : OnButtonClickListener {
                        override fun onClick() = typeMarkPatternPickedListener(null)
                    },
                    ResourceUtil.getString(android.R.string.cancel),
                    object : OnButtonClickListener {
                        override fun onClick() = true
                    },
                    ResourceUtil.getString(R.string.button_select),
                    object : OnButtonClickListener {
                        // takeUnless 判断调用它的变量是否符合要求，如果符合，返回此变量，否则返回 null
                        override fun onClick() = typeMarkPatternPickedListener(fragment.typeMarkPattern.takeUnless { it.patternFn == null })
                    },
                    false
            )
            args.putString(ARG_DEFAULT_PATTERN, defaultPattern)
            fragment.arguments = args
            return fragment
        }
    }

    private val defaultPattern by lazy { arguments!!.getString(ARG_DEFAULT_PATTERN) }
    private val typeMarkPatternList = DataManager.typeMarkPatternList
    private var position = -1
    private val typeMarkPattern by lazy { if (position == -1) TypeMarkPattern() else typeMarkPatternList[position].copy() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 为 position 设置正确的初始值
        if (Type.hasMarkPattern(defaultPattern)) {
            // indexOfFirst 找到第一个出现符合要求的元素下标，如果没找到，返回 -1（实际上不会出现）
            position = typeMarkPatternList.asSequence().indexOfFirst { it.patternFn == defaultPattern }
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_type_mark_pattern_picker, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        grid_type_mark_pattern.adapter = TypeMarkPatternGridAdapter(typeMarkPatternList)

        if (position != -1) {
            grid_type_mark_pattern.setItemChecked(position, true)
        }

        grid_type_mark_pattern.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            position = pos
            val (patternFn, patternName) = typeMarkPatternList[position]
            typeMarkPattern.patternFn = patternFn
            typeMarkPattern.patternName = patternName
        }
    }
}
