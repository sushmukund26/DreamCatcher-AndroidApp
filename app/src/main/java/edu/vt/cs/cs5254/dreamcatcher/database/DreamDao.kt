package edu.vt.cs.cs5254.dreamcatcher.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface DreamDao {

    // Dream methods

    // TODO Implement any Dream methods that you require

    // Entry methods

    // TODO Implement any DreamEntry methods that you require

    // Combined methods

    @Insert
    fun addDream(dream: Dream)

//    @Insert
//    fun addDreamEntry(dreamEntry: DreamEntry)

//    @Update
//    fun updateDream(dream: Dream)

//    @Delete
//    fun deleteDreamEntries(id: UUID)

//    @Query("SELECT * FROM dream WHERE id=(:dreamId)")
//    fun getDreamWithEntries(dreamId: UUID): LiveData<DreamWithEntries>
//
//    @Transaction
//    fun updateDreamWithEntries(dreamWithEntries: DreamWithEntries) {
//        val theDream = dreamWithEntries.dream
//        val theEntries = dreamWithEntries.dreamEntries
//        updateDream(dreamWithEntries.dream)
//        deleteDreamEntries(theDream.id)
//        theEntries.forEach { e -> addDreamEntry(e) }
//    }
//
//    @Transaction
//    fun addDreamWithEntries(dreamWithEntries: DreamWithEntries) {
//        addDream(dreamWithEntries.dream)
//        dreamWithEntries.dreamEntries.forEach { e -> addDreamEntry(e) }
//    }

    @Query("DELETE FROM dream")
    fun deleteAllDreams()

    @Query("SELECT * FROM dream")
    fun getDreams(): LiveData<List<Dream>>

}