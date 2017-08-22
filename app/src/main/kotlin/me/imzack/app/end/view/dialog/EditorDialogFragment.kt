package me.imzack.app.end.view.dialog

import android.os.Bundle
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindView
import me.imzack.app.end.R
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.SystemUtil
import java.io.Serializable

class EditorDialogFragment : BaseDialogFragment() {

    companion object {

        private val ARG_EDITOR_TEXT = "editor_text"
        private val ARG_EDITOR_HINT = "editor_hint"
        private val ARG_TEXT_EDITED_LSNR = "text_edited_lsnr"
    }

    @BindView(R.id.editor)
    lateinit var mEditor: EditText

    private var mEditorTextStr: String? = null
    private var mEditorHintStr: String? = null
    private var mOnTextEditedListener: OnTextEditedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        if (args != null) {
            mEditorTextStr = args.getString(ARG_EDITOR_TEXT)
            mEditorHintStr = args.getString(ARG_EDITOR_HINT)
            mOnTextEditedListener = args.getSerializable(ARG_TEXT_EDITED_LSNR) as OnTextEditedListener
        }

        mPositiveButtonClickListener = object : BaseDialogFragment.OnButtonClickListener {
            override fun onClick(): Boolean {
                mOnTextEditedListener?.onTextEdited(mEditor.text.toString())
                return true
            }
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_editor, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mEditor.setText(mEditorTextStr)
        mEditor.hint = mEditorHintStr
        mEditor.setSelection(mEditor.length())
        SystemUtil.showSoftInput(mEditor, 100)
    }

    override fun onDetach() {
        super.onDetach()
        mOnTextEditedListener = null
    }

    class Builder : BaseDialogFragment.Builder<EditorDialogFragment>() {

        private var mEditorText: String? = null
        private var mEditorHint: String? = null
        private var mOnTextEditedListener: OnTextEditedListener? = null

        fun setEditorText(editorText: String): Builder {
            mEditorText = editorText
            return this
        }

        fun setEditorHint(editorHint: String): Builder {
            mEditorHint = editorHint
            return this
        }

        fun setEditorHint(@StringRes resId: Int) = setEditorHint(ResourceUtil.getString(resId))

        fun setPositiveButton(text: String, listener: OnTextEditedListener): Builder {
            setPositiveButton(text, null)
            mOnTextEditedListener = listener
            return this
        }

        fun setPositiveButton(@StringRes resId: Int, listener: OnTextEditedListener) =
                setPositiveButton(ResourceUtil.getString(resId), listener)

        override fun onBuildContent(): EditorDialogFragment {
            val fragment = EditorDialogFragment()
            val args = Bundle()
            args.putString(ARG_EDITOR_TEXT, mEditorText)
            args.putString(ARG_EDITOR_HINT, mEditorHint)
            args.putSerializable(ARG_TEXT_EDITED_LSNR, mOnTextEditedListener)
            fragment.arguments = args
            return fragment
        }
    }

    interface OnTextEditedListener : Serializable {
        fun onTextEdited(text: String)
    }
}
