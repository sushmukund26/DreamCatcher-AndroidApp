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
import androidx.lifecycle.ViewModelProviders
import edu.vt.cs.cs5254.dreamcatcher.database.Dream
import androidx.lifecycle.Observer
import edu.vt.cs.cs5254.dreamcatcher.database.DreamWithEntries
import java.util.*

private const val ARG_DREAM_ID = "dream_id"

class DreamFragment : Fragment() {

    private lateinit var dream: Dream
    private lateinit var dreamWithEntries: DreamWithEntries
    private lateinit var titleField: EditText
    private lateinit var dreamRevealedButton: Button
    private lateinit var buttonsContainer: ViewGroup

    private lateinit var isRealizedCheckBox: CheckBox
    private lateinit var isDeferredCheckBox: CheckBox

    private val dreamDetailViewModel: DreamDetailViewModel by lazy {
        ViewModelProviders.of(this).get(DreamDetailViewModel::class.java)
    }

    //initialize model fields
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dream = Dream()
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
        dreamRevealedButton = view.findViewById(R.id.dream_entry_0_button)
        isRealizedCheckBox = view.findViewById(R.id.dream_realized)
        isDeferredCheckBox = view.findViewById(R.id.dream_deferred)
        buttonsContainer = view.findViewById(R.id.buttonsContainer)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dreamDetailViewModel.dreamLiveData.observe(
            viewLifecycleOwner,
            Observer { dream ->
                dream?.let {
                    this.dream = dream
                    updateUI()
                }
            })

        dreamDetailViewModel.dreamWithEntriesLiveData.observe(
            viewLifecycleOwner,
            Observer { dreamWithEntries ->
                dreamWithEntries?.let {
                    this.dreamWithEntries = dreamWithEntries
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
            if(isChecked) {
                val button = Button(activity)
                button.text = getString(R.string.dream_realized_button_text)
                buttonsContainer.addView(button)
            } else {
                buttonsContainer.removeViewAt(buttonsContainer.childCount-1)
            }
        }

        isDeferredCheckBox.setOnCheckedChangeListener { _, isChecked ->
            dream.isDeferred = isChecked
            isRealizedCheckBox.isEnabled = !isChecked
            if(isChecked) {
                val button = Button(activity)
                button.text = getString(R.string.dream_deferred_button_text)
                buttonsContainer.addView(button)
            } else {
                buttonsContainer.removeViewAt(buttonsContainer.childCount-1)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        dreamDetailViewModel.saveDream(dream)
    }

    private fun updateUI() {
        titleField.setText(dream.description)
//        titleField.setText(dreamWithEntries.dreamEntries[0].comment)

        isRealizedCheckBox.apply {
            isChecked = dream.isRealized
            jumpDrawablesToCurrentState()
        }
        isDeferredCheckBox.apply {
            isChecked = dream.isDeferred
            jumpDrawablesToCurrentState()
        }
//        for (entry in dream.dreamEntry)
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
