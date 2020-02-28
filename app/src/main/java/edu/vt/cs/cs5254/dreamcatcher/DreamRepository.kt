package edu.vt.cs.cs5254.dreamcatcher

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import edu.vt.cs.cs5254.dreamcatcher.database.*
import edu.vt.cs.cs5254.dreamcatcher.database.DreamEntryKind.COMMENT
import edu.vt.cs.cs5254.dreamcatcher.database.DreamEntryKind.REVEALED
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "dream_database"

class DreamRepository private constructor(context: Context) {

    private val repopulateRoomDatabaseCallback: RoomDatabase.Callback =
        object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                executor.execute {
                    dreamDao.apply {
                        deleteAllDreams()

                        val dream0 = Dream(
                            description = "My First Dream",
                            isRealized = true
                        )
                        addDream(dream0)
//                        val dream0Entries = listOf(
//                            DreamEntry(
//                                dreamId = dream0.id,
//                                kind = REVEALED,
//                                comment = "Dream Revealed"
//                            ),
//                            DreamEntry(
//                                dreamId = dream0.id,
//                                kind = COMMENT,
//                                comment = "Comment 1"
//                            ),
//                            DreamEntry(
//                                dreamId = dream0.id,
//                                kind = COMMENT,
//                                comment = "Comment 2"
//                            ),
//                            DreamEntry(
//                                dreamId = dream0.id,
//                                kind = COMMENT,
//                                comment = "Comment 3"
//                            )
//                        )
//                        addDreamWithEntries(DreamWithEntries(dream0, dream0Entries))


                        val dream1 = Dream(
                            description = "My Second Dream",
                            isDeferred = true
                        )
                        addDream(dream1)
//                        val dream1Entries = listOf(
//                            DreamEntry(
//                                dreamId = dream1.id,
//                                kind = REVEALED,
//                                comment = "Dream Revealed"
//                            ),
//                            DreamEntry(
//                                dreamId = dream1.id,
//                                kind = COMMENT,
//                                comment = "Comment 1"
//                            ),
//                            DreamEntry(
//                                dreamId = dream1.id,
//                                kind = COMMENT,
//                                comment = "Comment 2"
//                            ),
//                            DreamEntry(
//                                dreamId = dream1.id,
//                                kind = DreamEntryKind.DEFERRED,
//                                comment = "Dream Deferred"
//                            ),
//                            DreamEntry(
//                                dreamId = dream1.id,
//                                kind = COMMENT,
//                                comment = "Comment 3"
//                            )
//                        )
//                        addDreamWithEntries(DreamWithEntries(dream1, dream1Entries))


                        val dream2 =
                            Dream(description = "My Third Dream")
                        addDream(dream2)
//                        val dream2Entries = listOf(
//                            DreamEntry(
//                                dreamId = dream2.id,
//                                kind = REVEALED,
//                                comment = "Dream Revealed"
//                            ),
//                            DreamEntry(
//                                dreamId = dream2.id,
//                                kind = COMMENT,
//                                comment = "Comment 1"
//                            ),
//                            DreamEntry(
//                                dreamId = dream2.id,
//                                kind = COMMENT,
//                                comment = "Comment 2"
//                            )
//                        )
//                        addDreamWithEntries(DreamWithEntries(dream2, dream2Entries))

                        for (i in 3..20) {
                            val dream =
                                Dream(description = "Dream $i")
                            addDream(dream)
//                            val entries = listOf(
//                                DreamEntry(
//                                    dreamId = dream.id,
//                                    kind = REVEALED,
//                                    comment = "Dream Revealed"
//                                )
//                            )
//                            addDreamWithEntries(DreamWithEntries(dream, entries))
                        }

                    }
                }
            }
        }

    private val database : DreamDatabase = Room.databaseBuilder(
        context.applicationContext,
        DreamDatabase::class.java,
        DATABASE_NAME
    ).addCallback(repopulateRoomDatabaseCallback).build()

    private val dreamDao = database.dreamDao()

    private val executor = Executors.newSingleThreadExecutor()

    fun getDreams(): LiveData<List<Dream>> = dreamDao.getDreams()

//    fun getDreamWithEntries(dreamId: UUID): LiveData<DreamWithEntries> =
//        dreamDao.getDreamWithEntries(dreamId)
//
//    fun updateDreamWithEntries(dreamWithEntries: DreamWithEntries) {
//        executor.execute {
//            dreamDao.updateDreamWithEntries(dreamWithEntries)
//        }
//    }

    companion object {
        private var INSTANCE: DreamRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = DreamRepository(context)
            }
        }

        fun get(): DreamRepository {
            return INSTANCE ?:
            throw IllegalStateException("DreamRepository must be initialized")
        }
    }
}