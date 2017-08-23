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
import java.io.Serializable

class TypeMarkPatternPickerDialogFragment : BaseDialogFragment() {

    companion object {

        private val ARG_DEFAULT_PATTERN = "default_pattern"
        private val ARG_TYPE_MARK_PATTERN_PICKED_LSNR = "type_mark_pattern_picked_lsnr"

        fun newInstance(defaultPattern: String?, listener: OnTypeMarkPatternPickedListener): TypeMarkPatternPickerDialogFragment {
            val fragment = TypeMarkPatternPickerDialogFragment()
            val args = Bundle()
            args.putString(ARG_TITLE_STR, ResourceUtil.getString(R.string.title_dialog_fragment_type_mark_pattern_picker))
            args.putString(ARG_NEU_BTN_STR, ResourceUtil.getString(R.string.button_remove))
            args.putString(ARG_NEG_BTN_STR, ResourceUtil.getString(R.string.button_cancel))
            args.putString(ARG_POS_BTN_STR, ResourceUtil.getString(R.string.button_select))
            args.putString(ARG_DEFAULT_PATTERN, defaultPattern)
            args.putSerializable(ARG_TYPE_MARK_PATTERN_PICKED_LSNR, listener)
            fragment.arguments = args
            return fragment
        }
    }

    private var mDefaultPattern: String? = null
    private val mTypeMarkPattern = TypeMarkPattern()
    private var mPosition = -1
    private val mTypeMarkPatternList = DataManager.typeMarkPatternList
    private var mOnTypeMarkPatternPickedListener: OnTypeMarkPatternPickedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        if (args != null) {
            mDefaultPattern = args.getString(ARG_DEFAULT_PATTERN)
            mOnTypeMarkPatternPickedListener = args.getSerializable(ARG_TYPE_MARK_PATTERN_PICKED_LSNR) as OnTypeMarkPatternPickedListener
        }

        // 若初始有默认的Pattern，则将mTypeMarkPattern初始化为传入的默认Pattern，反之使用空的mTypeMarkPattern
        if (Type.hasMarkPattern(mDefaultPattern)) {
            for (i in mTypeMarkPatternList.indices) {
                val (patternFn, patternName) = mTypeMarkPatternList[i]
                if (patternFn == mDefaultPattern) {
                    mPosition = i
                    mTypeMarkPattern.patternFn = patternFn
                    mTypeMarkPattern.patternName = patternName
                    break
                }
            }
        }

        mNeutralButtonClickListener = object : BaseDialogFragment.OnButtonClickListener {
            override fun onClick(): Boolean {
                mOnTypeMarkPatternPickedListener?.onTypeMarkPatternPicked(null)
                return true
            }
        }
        mPositiveButtonClickListener = object : BaseDialogFragment.OnButtonClickListener {
            override fun onClick(): Boolean {
                mOnTypeMarkPatternPickedListener?.onTypeMarkPatternPicked(if (mTypeMarkPattern.patternFn == null) null else mTypeMarkPattern)
                return true
            }
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_type_mark_pattern_picker, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        grid_type_mark_pattern.adapter = TypeMarkPatternGridAdapter(mTypeMarkPatternList)

        if (mPosition != -1) {
            grid_type_mark_pattern.setItemChecked(mPosition, true)
        }

        grid_type_mark_pattern.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            mPosition = position
            val (patternFn, patternName) = mTypeMarkPatternList[mPosition]
            mTypeMarkPattern.patternFn = patternFn
            mTypeMarkPattern.patternName = patternName
        }
    }

    override fun onDetach() {
        super.onDetach()
        mOnTypeMarkPatternPickedListener = null
    }

    interface OnTypeMarkPatternPickedListener : Serializable {
        fun onTypeMarkPatternPicked(typeMarkPattern: TypeMarkPattern?)
    }
}
