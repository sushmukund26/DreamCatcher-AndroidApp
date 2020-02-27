package edu.vt.cs.cs5254.dreamcatcher

import android.content.Context
import androidx.room.Room
import edu.vt.cs.cs5254.dreamcatcher.database.Dream
import edu.vt.cs.cs5254.dreamcatcher.database.DreamDatabase
import java.util.*

private const val DATABASE_NAME = "dream-database"

class DreamRepository private constructor(context: Context) {

    private val database : DreamDatabase = Room.databaseBuilder(
        context.applicationContext,
        DreamDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val dreamDao = database.dreamDao()

    fun getDreams(): List<Dream> = dreamDao.getDreams()

    fun getDream(id: UUID): Dream? = dreamDao.getDream(id)

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