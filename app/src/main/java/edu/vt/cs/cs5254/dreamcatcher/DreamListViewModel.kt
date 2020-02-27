package edu.vt.cs.cs5254.dreamcatcher

import androidx.lifecycle.ViewModel
import edu.vt.cs.cs5254.dreamcatcher.database.Dream

class DreamListViewModel : ViewModel() {

    val dreams = mutableListOf<Dream>()

    init {
        for (i in 0 until 100) {
            val dream = Dream()
            dream.title = "Dream #$i"
            dream.isSolved = i % 2 == 0
            dreams += dream
        }
    }
}