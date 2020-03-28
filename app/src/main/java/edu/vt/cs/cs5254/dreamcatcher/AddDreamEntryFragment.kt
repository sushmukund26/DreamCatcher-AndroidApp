package edu.vt.cs.cs5254.dreamcatcher

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import edu.vt.cs.cs5254.dreamcatcher.util.KeyboardUtil.hideSoftKeyboard

class AddDreamEntryFragment: DialogFragment() {

    interface Callbacks {
        fun onCommentProvided(comment: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewGroup = requireActivity().findViewById(android.R.id.content) as ViewGroup
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_add_comment, viewGroup, false)
        val textView = view.findViewById(R.id.comment_text) as EditText
        val okListener = DialogInterface.OnClickListener { _, _ ->
            targetFragment?.let { fragment ->
                (fragment as Callbacks).onCommentProvided(textView.text.toString())
            }
            hideSoftKeyboard(requireContext(), view)
        }
        val cancelListener = DialogInterface.OnClickListener { _, _ ->
            hideSoftKeyboard(requireContext(), view)
        }
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Add Comment")
            .setPositiveButton(android.R.string.ok, okListener)
            .setNegativeButton(android.R.string.cancel, cancelListener)
            .create()
    }
}