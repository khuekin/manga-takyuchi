package com.tachiyomi.drive.data.db

import androidx.room.*
import com.tachiyomi.drive.data.model.Chapter
import com.tachiyomi.drive.data.model.Manga
import com.tachiyomi.drive.data.model.ReadingProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {
    @Query("SELECT * FROM mangas ORDER BY isFavorite DESC, lastReadTime DESC")
    fun getAllManga(): Flow<List<Manga>>

    @Query("SELECT * FROM mangas WHERE id = :id")
    suspend fun getMangaById(id: String): Manga?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateManga(manga: Manga)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMangaList(mangas: List<Manga>)

    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId ORDER BY title ASC")
    fun getChaptersForManga(mangaId: String): Flow<List<Chapter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<Chapter>)

    @Query("UPDATE chapters SET lastPageRead = :page, isRead = :isRead WHERE id = :chapterId")
    suspend fun updateChapterProgress(chapterId: String, page: Int, isRead: Boolean)

    @Query("SELECT * FROM reading_history WHERE mangaId = :mangaId")
    suspend fun getProgress(mangaId: String): ReadingProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: ReadingProgress)

    @Query("SELECT * FROM reading_history ORDER BY updatedAt DESC")
    fun getAllHistory(): Flow<List<ReadingProgress>>
}

@Database(entities = [Manga::class, Chapter::class, ReadingProgress::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tachiyomi_drive_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
