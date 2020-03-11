package edu.vt.cs.cs5254.dreamcatcher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import edu.vt.cs.cs5254.dreamcatcher.database.Dream
import edu.vt.cs.cs5254.dreamcatcher.database.DreamWithEntries
import java.util.*

class DreamDetailViewModel : ViewModel() {

    private val dreamRepository = DreamRepository.get()
    private val dreamIdLiveData = MutableLiveData<UUID>()

    var dreamLiveData: LiveData<Dream?> =
        Transformations.switchMap(dreamIdLiveData) { dreamID ->
            dreamRepository.getDream(dreamID)
        }

    var dreamWithEntriesLiveData: LiveData<DreamWithEntries?> =
        Transformations.switchMap(dreamIdLiveData) { dreamID ->
            dreamRepository.getDreamWithEntries(dreamID)
        }

    fun loadDream(dreamID: UUID) {
        dreamIdLiveData.value = dreamID
    }

    fun saveDream(dream: Dream) {
        dreamRepository.updateDream(dream)
    }
}