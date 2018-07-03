package net.zackzhang.app.end.view.dialog

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_fragment_editor.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.util.SystemUtil
import me.imzack.lib.basedialogfragment.BaseDialogFragment

// TODO 将此类封装进 BaseDialogFragment
class EditorDialogFragment : BaseDialogFragment() {

    companion object {

        private const val ARG_DEFAULT_EDITOR_TEXT = "default_editor_text"
        private const val ARG_EDITOR_HINT = "editor_hint"
    }

    // TODO text 可能是 null 吗
    var editedListener: ((text: String) -> Boolean)? = null

    var thirdButtonClickListener: (() -> Unit)? = null

    override fun onCreateContentView(inflater: LayoutInflater, root: ViewGroup) =
            inflater.inflate(R.layout.dialog_fragment_editor, root, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editor.setText(arguments!!.getString(ARG_DEFAULT_EDITOR_TEXT))
        editor.hint = arguments!!.getString(ARG_EDITOR_HINT)
        editor.setSelection(editor.length())
        SystemUtil.showSoftInput(editor, 100)

        neutralButtonClickListener = {
            thirdButtonClickListener?.invoke()
            true
        }
        // 编辑对话框一定有取消按钮
        negativeButtonText = ResourceUtil.getString(android.R.string.cancel)
        negativeButtonClickListener = { true }
        positiveButtonClickListener = { editedListener?.invoke(editor.text.toString()) != false }
    }

    class Builder {

        private val args = Bundle()

        fun setTitle(title: CharSequence): Builder {
            putTitle(args, title)
            return this
        }

        fun setTitle(@StringRes titleResId: Int) = setTitle(ResourceUtil.getString(titleResId))

        fun setDefaultEditorText(defaultEditorText: CharSequence): Builder {
            args.putCharSequence(ARG_DEFAULT_EDITOR_TEXT, defaultEditorText)
            return this
        }

        fun setDefaultEditorText(@StringRes defaultEditorTextResId: Int) = setDefaultEditorText(ResourceUtil.getString(defaultEditorTextResId))

        fun setEditorHint(editorHint: CharSequence): Builder {
            args.putCharSequence(ARG_EDITOR_HINT, editorHint)
            return this
        }

        fun setEditorHint(@StringRes editorHintResId: Int) = setEditorHint(ResourceUtil.getString(editorHintResId))

        fun setEditButtonText(editButtonText: CharSequence): Builder {
            putPositiveButtonText(args, editButtonText)
            return this
        }

        fun setEditButtonText(@StringRes editButtonTextResId: Int) = setEditButtonText(ResourceUtil.getString(editButtonTextResId))

        fun setThirdButtonText(thirdButtonText: CharSequence): Builder {
            putNeutralButtonText(args, thirdButtonText)
            return this
        }

        fun setThirdButtonText(@StringRes thirdButtonTextResId: Int) = setThirdButtonText(ResourceUtil.getString(thirdButtonTextResId))

        fun build(): EditorDialogFragment {
            val fragment = EditorDialogFragment()
            fragment.arguments = args
            return fragment
        }

        fun show(fm: FragmentManager, tag: String? = null) {
            build().show(fm, tag)
        }
    }
}
