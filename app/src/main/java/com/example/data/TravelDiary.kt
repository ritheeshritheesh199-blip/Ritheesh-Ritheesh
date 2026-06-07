package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val destination: String,
    val visitDate: String,
    val rating: Int,
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<DiaryEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntry)

    @Delete
    suspend fun deleteEntry(entry: DiaryEntry)

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Int)
}

@Database(entities = [DiaryEntry::class], version = 1, exportSchema = false)
abstract class TravelDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
}

class TravelRepository(private val diaryDao: DiaryDao) {
    val allEntries: Flow<List<DiaryEntry>> = diaryDao.getAllEntries()

    suspend fun insert(entry: DiaryEntry) {
        diaryDao.insertEntry(entry)
    }

    suspend fun delete(entry: DiaryEntry) {
        diaryDao.deleteEntry(entry)
    }

    suspend fun deleteById(id: Int) {
        diaryDao.deleteEntryById(id)
    }
}
