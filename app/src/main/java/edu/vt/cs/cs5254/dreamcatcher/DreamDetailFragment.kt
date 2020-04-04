package edu.vt.cs.cs5254.dreamcatcher

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.vt.cs.cs5254.dreamcatcher.database.Dream
import edu.vt.cs.cs5254.dreamcatcher.database.DreamEntry
import edu.vt.cs.cs5254.dreamcatcher.database.DreamEntryKind
import edu.vt.cs.cs5254.dreamcatcher.database.DreamWithEntries
import java.util.*

private const val ARG_DREAM_ID = "dream_id"
private const val DIALOG_ADD_DREAM_ENTRY = "DialogAddDreamEntry"
private const val REQUEST_DREAM_ENTRY = 0

@Suppress("DEPRECATION")
class DreamDetailFragment : Fragment(), AddDreamEntryFragment.Callbacks {

    private lateinit var dream: Dream
    private lateinit var dreamEntries: List<DreamEntry>
    private lateinit var titleField: EditText

    private lateinit var isRealizedCheckBox: CheckBox
    private lateinit var isDeferredCheckBox: CheckBox

    private lateinit var dreamEntryRecyclerView: RecyclerView
    private var adapter: DreamEntryAdapter? = null

    private lateinit var photoView: ImageView
    private lateinit var addDreamEntryButton: FloatingActionButton

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
        val view = inflater.inflate(R.layout.fragment_dream_detail, container, false)

        titleField = view.findViewById(R.id.dream_title)

        isRealizedCheckBox = view.findViewById(R.id.dream_realized)
        isDeferredCheckBox = view.findViewById(R.id.dream_deferred)

        dreamEntryRecyclerView =
            view.findViewById(R.id.dream_entry_recycler_view) as RecyclerView
        dreamEntryRecyclerView.layoutManager = LinearLayoutManager(context)

        photoView = view.findViewById(R.id.dream_photo) as ImageView
        addDreamEntryButton = view.findViewById(R.id.add_comment_fab)

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
            addDreamEntryButton.isEnabled = !isChecked

            //add or remove dreamEntry
            dreamEntries = if(isChecked) {
                if(dreamEntries.none { dE -> dE.kind == DreamEntryKind.REALIZED }) {
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
                dreamEntries.filterNot { dE -> dE.kind == DreamEntryKind.REALIZED }
            }

            updateDreamEntryButtons()
        }

        isDeferredCheckBox.setOnCheckedChangeListener { _, isChecked ->
            dream.isDeferred = isChecked
            isRealizedCheckBox.isEnabled = !isChecked
            dreamEntries = if(isChecked) {
                if(dreamEntries.none { dE -> dE.kind == DreamEntryKind.DEFERRED }) {
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
                dreamEntries.filterNot { dE -> dE.kind == DreamEntryKind.DEFERRED }
            }
            updateDreamEntryButtons()
        }

        addDreamEntryButton.setOnClickListener {
            AddDreamEntryFragment().apply {
                setTargetFragment(this@DreamDetailFragment, REQUEST_DREAM_ENTRY)
                show(this@DreamDetailFragment.requireFragmentManager(), DIALOG_ADD_DREAM_ENTRY)
            }
        }
    }

    private fun setButtonColor(button: Button, color: Int, textColor: Int = R.color.text_color) {
        button.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(color))
        button.setTextColor(resources.getColor(textColor))
        button.alpha = 1f
    }

    override fun onStop() {
        super.onStop()
        var dreamWithEntries = DreamWithEntries(dream, dreamEntries)
        dreamDetailViewModel.saveDreamWithEntries(dreamWithEntries)
    }

    override fun onCommentProvided(comment: String) {
        var dreamEntry = DreamEntry(
            dreamId = dream.id,
            kind = DreamEntryKind.COMMENT,
            comment = comment
        )
        dreamEntries += dreamEntry
        updateUI()
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

        updateDreamEntryButtons()
    }

    private fun updateDreamEntryButtons() {
        adapter = DreamEntryAdapter(dreamEntries)
        dreamEntryRecyclerView.adapter = adapter
    }

    companion object {

        fun newInstance(dreamID: UUID): DreamDetailFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DREAM_ID, dreamID)
            }
            return DreamDetailFragment().apply {
                arguments = args
            }
        }
    }

    inner class DreamEntryHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var dreamEntry: DreamEntry

        private val dreamEntryButton: Button = itemView.findViewById(R.id.dream_entry_button)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(dreamEntry: DreamEntry) {
            this.dreamEntry = dreamEntry

            //configure text and button styles
            dreamEntryButton.text = when {
                dreamEntry.kind == DreamEntryKind.COMMENT -> {
                    setButtonColor(dreamEntryButton, R.color.colorAccent, R.color.primary_text)

                    val df = DateFormat.getMediumDateFormat(activity)
                    val commentDate = df.format(dreamEntry.dateCreated)
                    dreamEntry.comment + " (" + commentDate + ")"
                }
                dreamEntry.kind == DreamEntryKind.DEFERRED -> {
                    setButtonColor(dreamEntryButton, R.color.red)
                    dreamEntry.comment
                }
                dreamEntry.kind == DreamEntryKind.REALIZED -> {
                    setButtonColor(dreamEntryButton, R.color.green)
                    dreamEntry.comment
                }
                else -> {
                    //dream revealed button
                    setButtonColor(dreamEntryButton, R.color.colorPrimary)
                    dreamEntry.comment
                }
            }

        }

        //add swipe action here with callback
        override fun onClick(v: View?) {
        }
    }

    private inner class DreamEntryAdapter(var dreamEntries: List<DreamEntry>)
        : RecyclerView.Adapter<DreamEntryHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : DreamEntryHolder {
            val view = layoutInflater.inflate(R.layout.list_item_dream_entry, parent, false)
            return DreamEntryHolder(view)
        }

        override fun getItemCount() = dreamEntries.size

        override fun onBindViewHolder(holder: DreamEntryHolder, position: Int) {
            val dreamEntry = dreamEntries[position]
            holder.bind(dreamEntry)
        }
    }
}
