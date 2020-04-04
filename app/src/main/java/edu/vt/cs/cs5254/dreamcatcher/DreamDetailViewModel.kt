package edu.vt.cs.cs5254.dreamcatcher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import edu.vt.cs.cs5254.dreamcatcher.database.Dream
import edu.vt.cs.cs5254.dreamcatcher.database.DreamWithEntries
import java.io.File
import java.util.*

class DreamDetailViewModel : ViewModel() {

    private val dreamRepository = DreamRepository.get()
    private val dreamIdLiveData = MutableLiveData<UUID>()

    var dreamWithEntriesLiveData: LiveData<DreamWithEntries?> =
        Transformations.switchMap(dreamIdLiveData) { dreamID ->
            dreamRepository.getDreamWithEntries(dreamID)
        }

    fun loadDream(dreamID: UUID) {
        dreamIdLiveData.value = dreamID
    }

    fun saveDreamWithEntries(dreamWithEntries: DreamWithEntries) {
        dreamRepository.updateDreamWithEntries(dreamWithEntries)
    }

    fun getPhotoFile(dream: Dream): File {
        return dreamRepository.getPhotoFile(dream)
    }
}