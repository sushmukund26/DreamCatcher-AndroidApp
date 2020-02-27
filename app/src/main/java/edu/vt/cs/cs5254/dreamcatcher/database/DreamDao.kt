package edu.vt.cs.cs5254.dreamcatcher.database

import androidx.room.Dao
import androidx.room.Query
import java.util.*

@Dao
interface DreamDao {

    @Query("SELECT * FROM dream")
    fun getDreams(): List<Dream>

    @Query("SELECT * FROM dream WHERE id=(:id)")
    fun getDream(id: UUID): Dream?
}