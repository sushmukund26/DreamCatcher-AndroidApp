package edu.vt.cs.cs5254.dreamcatcher

import androidx.lifecycle.ViewModel

class DreamListViewModel : ViewModel() {

    val crimes = mutableListOf<Dream>()

    init {
        for (i in 0 until 100) {
            val crime = Dream()
            crime.title = "Dream #$i"
            crime.isSolved = i % 2 == 0
            crimes += crime
        }
    }
}