package edu.vt.cs.cs5254.dreamcatcher.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface DreamDao {

    // Dream methods
    @Insert
    fun addDream(dream: Dream)

    @Transaction
    @Query("SELECT * FROM dream WHERE id=(:dreamId)")
    fun getDream(dreamId: UUID): LiveData<Dream>

    @Query("SELECT * FROM dream")
    fun getDreams(): LiveData<List<Dream>>

    @Update
    fun updateDream(dream: Dream)


    // Entry methods
    @Insert
    fun addDreamEntry(dreamEntry: DreamEntry)

    @Query("DELETE FROM dream_entry WHERE dreamId=(:id)")
    fun deleteDreamEntries(id: UUID)


    // Combined methods
    @Transaction
    @Query("SELECT * FROM dream WHERE id=(:dreamId)")
    fun getDreamWithEntries(dreamId: UUID): LiveData<DreamWithEntries>

    @Transaction
    fun updateDreamWithEntries(dreamWithEntries: DreamWithEntries) {
        val theDream = dreamWithEntries.dream
        val theEntries = dreamWithEntries.dreamEntries
        updateDream(dreamWithEntries.dream)
        deleteDreamEntries(theDream.id)
        theEntries.forEach { e -> addDreamEntry(e) }
    }

    @Transaction
    fun addDreamWithEntries(dreamWithEntries: DreamWithEntries) {
        addDream(dreamWithEntries.dream)
        dreamWithEntries.dreamEntries.forEach { e -> addDreamEntry(e) }
    }

    @Query("DELETE FROM dream")
    fun deleteAllDreams()

    @Query("DELETE FROM dream_entry")
    fun deleteAllDreamEntries()

    @Transaction
    fun reconstructSampleDatabase() {
        deleteAllDreams()

        val dream0 = Dream(
            description = "Dream #0",
            isRealized = false
        )
        val dream0Entries = listOf(
            DreamEntry(
                dreamId = dream0.id,
                kind = DreamEntryKind.REVEALED,
                comment = "Dream Revealed"
            ),
            DreamEntry(
                dreamId = dream0.id,
                kind = DreamEntryKind.COMMENT,
                comment = "Dream 0 Entry 1"
            )
        )
        addDreamWithEntries(DreamWithEntries(dream0, dream0Entries))


        val dream1 = Dream(
            description = "Dream #1",
            isDeferred = true
        )
        val dream1Entries = listOf(
            DreamEntry(
                dreamId = dream1.id,
                kind = DreamEntryKind.REVEALED,
                comment = "Dream Revealed"
            ),
            DreamEntry(
                dreamId = dream1.id,
                kind = DreamEntryKind.COMMENT,
                comment = "Dream 1 Entry 1"
            ),
            DreamEntry(
                dreamId = dream1.id,
                kind = DreamEntryKind.COMMENT,
                comment = "Dream 1 Entry 2"
            ),
            DreamEntry(
                dreamId = dream1.id,
                kind = DreamEntryKind.DEFERRED,
                comment = "Dream Deferred"
            )
        )
        addDreamWithEntries(DreamWithEntries(dream1, dream1Entries))


        val dream2 =
            Dream(description = "Dream #2", isRealized = true)
        val dream2Entries = listOf(
            DreamEntry(
                dreamId = dream2.id,
                kind = DreamEntryKind.REVEALED,
                comment = "Dream Revealed"
            ),
            DreamEntry(
                dreamId = dream2.id,
                kind = DreamEntryKind.COMMENT,
                comment = "Dream 2 Entry 1"
            ),
            DreamEntry(
                dreamId = dream2.id,
                kind = DreamEntryKind.COMMENT,
                comment = "Dream 2 Entry 2"
            ),
            DreamEntry(
                dreamId = dream2.id,
                kind = DreamEntryKind.COMMENT,
                comment = "Dream 2 Entry 3"
            ),
            DreamEntry(
                dreamId = dream2.id,
                kind = DreamEntryKind.REALIZED,
                comment = "Dream Realized"
            )
        )
        addDreamWithEntries(DreamWithEntries(dream2, dream2Entries))

    }

}