package edu.vt.cs.cs5254.dreamcatcher

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import edu.vt.cs.cs5254.dreamcatcher.database.Dream

class DreamFragment : Fragment() {

    private lateinit var dream: Dream
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var isSolvedCheckBox: CheckBox

    //initialize model fields
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dream = Dream()
    }

    //initialize view fields
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dream, container, false)

        titleField = view.findViewById(R.id.dream_title)
        dateButton = view.findViewById(R.id.dream_date)
        isSolvedCheckBox = view.findViewById(R.id.dream_solved)

        dateButton.apply {
            text = dream.dateRevealed.toString()
            isEnabled = false
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This space intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                dream.description = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // This one too
            }
        }

        titleField.addTextChangedListener(titleWatcher)

        isSolvedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            dream.isDeferred = isChecked
        }
    }
}
