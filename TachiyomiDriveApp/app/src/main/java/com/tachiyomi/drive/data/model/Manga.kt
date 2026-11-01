package com.tachiyomi.drive.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mangas")
data class Manga(
    @PrimaryKey val id: String, // Google Drive Folder ID
    val title: String,
    val coverUrl: String? = null,
    val totalChapters: Int = 0,
    val lastReadChapterId: String? = null,
    val lastReadChapterTitle: String? = null,
    val lastReadPage: Int = 1,
    val lastReadTime: Long = 0L,
    val isFavorite: Boolean = false
)

@Entity(tableName = "chapters")
data class Chapter(
    @PrimaryKey val id: String, // Google Drive File ID
    val mangaId: String,
    val title: String,
    val pageCount: Int = 0,
    val lastPageRead: Int = 0,
    val isRead: Boolean = false,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null,
    val fileSizeBytes: Long = 0L
)

@Entity(tableName = "reading_history")
data class ReadingProgress(
    @PrimaryKey val mangaId: String,
    val chapterId: String,
    val chapterTitle: String,
    val currentPage: Int,
    val totalPages: Int,
    val updatedAt: Long = System.currentTimeMillis()
)
