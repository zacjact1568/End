package me.imzack.app.end.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_fragment_editor.*
import me.imzack.app.end.R
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.SystemUtil
import me.imzack.lib.basedialogfragment.BaseDialogFragment

class EditorDialogFragment : BaseDialogFragment() {

    companion object {

        private val ARG_DEFAULT_EDITOR_TEXT = "default_editor_text"
        private val ARG_EDITOR_HINT = "editor_hint"

        fun newInstance(
                editButtonText: String,
                editedListener: (text: String?) -> Boolean,
                defaultEditorText: String? = null,
                editorHint: String? = null,
                titleText: String? = null,
                thirdButtonText: String? = null,
                thirdButtonClickListener: (() -> Unit)? = null
        ): EditorDialogFragment {
            val fragment = EditorDialogFragment()
            val args = Bundle()
            putBaseArguments(
                    args,
                    titleText,
                    thirdButtonText,
                    object : OnButtonClickListener {
                        override fun onClick(): Boolean {
                            thirdButtonClickListener?.invoke()
                            return true
                        }
                    },
                    ResourceUtil.getString(android.R.string.cancel),
                    object : OnButtonClickListener {
                        override fun onClick() = true
                    },
                    editButtonText,
                    object : OnButtonClickListener {
                        override fun onClick() = editedListener(fragment.editor.text.toString())
                    }
            )
            args.putString(ARG_DEFAULT_EDITOR_TEXT, defaultEditorText)
            args.putString(ARG_EDITOR_HINT, editorHint)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_editor, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editor.setText(arguments.getString(ARG_DEFAULT_EDITOR_TEXT))
        editor.hint = arguments.getString(ARG_EDITOR_HINT)
        editor.setSelection(editor.length())
        SystemUtil.showSoftInput(editor, 100)
    }
}
