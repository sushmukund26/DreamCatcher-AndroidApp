package edu.vt.cs.cs5254.dreamcatcher.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface DreamDao {

    @Insert
    fun addDream(dream: Dream)

    @Insert
    fun addDreamEntry(dreamEntry: DreamEntry)

    @Transaction
    fun addDreamWithEntries(dreamWithEntries: DreamWithEntries) {
        addDream(dreamWithEntries.dream)
        dreamWithEntries.dreamEntries.forEach { e -> addDreamEntry(e) }
    }

    @Transaction
    @Query("SELECT * FROM dream WHERE id=(:dreamId)")
    fun getDream(dreamId: UUID): LiveData<Dream>

    @Query("SELECT * FROM dream")
    fun getDreams(): LiveData<List<Dream>>

    @Transaction
    @Query("SELECT * FROM dream WHERE id=(:dreamId)")
    fun getDreamWithEntries(dreamId: UUID): LiveData<DreamWithEntries>

    @Update
    fun updateDream(dream: Dream)

    @Transaction
    fun updateDreamWithEntries(dreamWithEntries: DreamWithEntries) {
        val theDream = dreamWithEntries.dream
        val theEntries = dreamWithEntries.dreamEntries
        updateDream(dreamWithEntries.dream)
        deleteDreamEntries(theDream.id)
        theEntries.forEach { e -> addDreamEntry(e) }
    }

    @Query("DELETE FROM dream")
    fun deleteAllDreams()

    @Query("DELETE FROM dream_entry WHERE id=(:id)")
    fun deleteDreamEntries(id: UUID)

}