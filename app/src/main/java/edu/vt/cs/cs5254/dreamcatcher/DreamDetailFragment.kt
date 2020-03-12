package edu.vt.cs.cs5254.dreamcatcher

import android.graphics.Color.*
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import edu.vt.cs.cs5254.dreamcatcher.database.Dream
import edu.vt.cs.cs5254.dreamcatcher.database.DreamEntry
import edu.vt.cs.cs5254.dreamcatcher.database.DreamEntryKind
import edu.vt.cs.cs5254.dreamcatcher.database.DreamWithEntries
import java.util.*

private const val ARG_DREAM_ID = "dream_id"

class DreamFragment : Fragment() {

    private lateinit var dream: Dream
    private lateinit var dreamEntries: List<DreamEntry>
    private lateinit var titleField: EditText

    private lateinit var buttons: List<Button>

    private lateinit var dreamEntry0Button: Button
    private lateinit var dreamEntry1Button: Button
    private lateinit var dreamEntry2Button: Button
    private lateinit var dreamEntry3Button: Button
    private lateinit var dreamEntry4Button: Button

    private lateinit var isRealizedCheckBox: CheckBox
    private lateinit var isDeferredCheckBox: CheckBox

    private val dreamDetailViewModel: DreamDetailViewModel by lazy {
        ViewModelProvider(this).get(DreamDetailViewModel::class.java)
    }

    //initialize model fields
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dream = Dream()
        dreamEntries = listOf()
        val dreamId: UUID = arguments?.getSerializable(ARG_DREAM_ID) as UUID
        dreamDetailViewModel.loadDream(dreamId)
    }

    //initialize view fields
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dream, container, false)

        titleField = view.findViewById(R.id.dream_title)
        dreamEntry0Button = view.findViewById(R.id.dream_entry_0_button)
        dreamEntry1Button = view.findViewById(R.id.dream_entry_1_button)
        dreamEntry2Button = view.findViewById(R.id.dream_entry_2_button)
        dreamEntry3Button = view.findViewById(R.id.dream_entry_3_button)
        dreamEntry4Button = view.findViewById(R.id.dream_entry_4_button)

        buttons = listOf(dreamEntry0Button, dreamEntry1Button, dreamEntry2Button, dreamEntry3Button, dreamEntry4Button)
        isRealizedCheckBox = view.findViewById(R.id.dream_realized)
        isDeferredCheckBox = view.findViewById(R.id.dream_deferred)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dreamDetailViewModel.dreamWithEntriesLiveData.observe(
            viewLifecycleOwner,
            Observer { dream ->
                dream?.let {
                    this.dream = dream.dream
                    this.dreamEntries = dream.dreamEntries
                    updateUI()
                }
            })
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

        isRealizedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            dream.isRealized = isChecked
            isDeferredCheckBox.isEnabled = !isChecked
            //add or remove dreamEntry
            dreamEntries = if(isChecked) {
                if(dreamEntries.filter { dE -> dE.kind == DreamEntryKind.REALIZED }.isEmpty()) {
                    var dreamEntry = DreamEntry(
                        dreamId = dream.id,
                        kind = DreamEntryKind.REALIZED,
                        comment = "Dream Realized"
                    )
                    dreamEntries + dreamEntry
                } else {
                    dreamEntries
                }
            } else {
                dreamEntries.dropLast(1)
            }
            updateButtons()
        }

        isDeferredCheckBox.setOnCheckedChangeListener { _, isChecked ->
            dream.isDeferred = isChecked
            isRealizedCheckBox.isEnabled = !isChecked
            dreamEntries = if(isChecked) {
                if(dreamEntries.filter { dE -> dE.kind == DreamEntryKind.DEFERRED }.isEmpty()) {
                    var dreamEntry = DreamEntry(
                        dreamId = dream.id,
                        kind = DreamEntryKind.DEFERRED,
                        comment = "Dream Deferred"
                    )
                    dreamEntries + dreamEntry
                } else {
                    dreamEntries
                }
            } else {
                dreamEntries.dropLast(1)
            }
            updateButtons()
        }
    }

    private fun updateButtons() {
        for(button in buttons) {
            button.visibility = INVISIBLE
        }

        for((dreamEntry, button) in dreamEntries.zip(buttons)) {
            button.visibility = VISIBLE
            button.text = if(dreamEntry.kind == DreamEntryKind.COMMENT) {
                button.background.setTint(resources.getColor(R.color.colorAccent))
                val df = DateFormat.getMediumDateFormat(activity)
                val commentDate = df.format(dreamEntry.dateCreated)
                dreamEntry.comment + " (" + commentDate + ")"
            } else if(dreamEntry.kind == DreamEntryKind.DEFERRED) {
                button.background.setTint(resources.getColor(R.color.red))
                dreamEntry.comment
            } else if(dreamEntry.kind == DreamEntryKind.REALIZED) {
                button.background.setTint(resources.getColor(R.color.green))
                dreamEntry.comment
            } else {
                button.background.setTint(resources.getColor(R.color.colorPrimary))
                dreamEntry.comment
            }

        }
    }

    override fun onStop() {
        super.onStop()
        var dreamWithEntries = DreamWithEntries(dream, dreamEntries)
        dreamDetailViewModel.saveDreamWithEntries(dreamWithEntries)
    }

    private fun updateUI() {
        titleField.setText(dream.description)

        isRealizedCheckBox.apply {
            isChecked = dream.isRealized
            jumpDrawablesToCurrentState()
        }
        isDeferredCheckBox.apply {
            isChecked = dream.isDeferred
            jumpDrawablesToCurrentState()
        }

        updateButtons()
    }

    companion object {

        fun newInstance(dreamID: UUID): DreamFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DREAM_ID, dreamID)
            }
            return DreamFragment().apply {
                arguments = args
            }
        }
    }
}
