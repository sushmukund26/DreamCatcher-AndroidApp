package edu.vt.cs.cs5254.dreamcatcher

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import edu.vt.cs.cs5254.dreamcatcher.database.Dream
import edu.vt.cs.cs5254.dreamcatcher.database.DreamDatabase
import edu.vt.cs.cs5254.dreamcatcher.database.DreamWithEntries
import java.io.File
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "dream_database"

class DreamRepository private constructor(context: Context) {

    private val database : DreamDatabase = Room.databaseBuilder(
        context.applicationContext,
        DreamDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val dreamDao = database.dreamDao()

    private val executor = Executors.newSingleThreadExecutor()

    private val filesDir = context.applicationContext.filesDir

    fun getDreams(): LiveData<List<Dream>> = dreamDao.getDreams()

    fun getDreamWithEntries(dreamId: UUID): LiveData<DreamWithEntries> =
        dreamDao.getDreamWithEntries(dreamId)

    fun updateDreamWithEntries(dreamWithEntries: DreamWithEntries) {
        executor.execute {
            dreamDao.updateDreamWithEntries(dreamWithEntries)
        }
    }

    fun addDreamWithEntries(dreamWithEntries: DreamWithEntries) {
        executor.execute {
            dreamDao.addDreamWithEntries(dreamWithEntries)
        }
    }

    fun deleteAllDreams() {
        executor.execute {
            dreamDao.deleteAllDreams()
        }
    }

    fun getPhotoFile(dream: Dream): File = File(filesDir, dream.photoFileName)

    fun reconstructSampleDatabase() = dreamDao.reconstructSampleDatabase()


    companion object {
        private const val TAG = "DreamRepository"

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