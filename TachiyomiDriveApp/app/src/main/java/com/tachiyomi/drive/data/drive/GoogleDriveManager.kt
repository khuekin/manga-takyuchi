package com.tachiyomi.drive.data.drive

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.tachiyomi.drive.data.model.Chapter
import com.tachiyomi.drive.data.model.Manga
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Collections

class GoogleDriveManager(private val context: Context) {

    private var driveService: Drive? = null

    fun initService(accountName: String) {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, Collections.singleton(DriveScopes.DRIVE_READONLY)
        ).setSelectedAccountName(accountName)

        driveService = Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Tachiyomi Drive Reader").build()
    }

    /**
     * Quét thư mục gốc "Manga Download" trên Google Drive
     */
    suspend fun scanMangaFolders(rootFolderId: String = "1ly7zUel7l4yY3Q5kbEqGfxMZhkECmyV5"): List<Manga> = withContext(Dispatchers.IO) {
        val service = driveService ?: return@withContext emptyList()
        val query = "'$rootFolderId' in parents and mimeType = 'application/vnd.google-apps.folder' and trashed = false"
        
        val fileList = service.files().list()
            .setQ(query)
            .setFields("files(id, name)")
            .execute()

        val mangas = mutableListOf<Manga>()
        for (f in fileList.files ?: emptyList()) {
            mangas.add(
                Manga(
                    id = f.id,
                    title = f.name,
                    totalChapters = countChapters(f.id)
                )
            )
        }
        mangas
    }

    /**
     * Lấy danh sách các tập PDF của 1 bộ truyện
     */
    suspend fun getChapters(mangaFolderId: String): List<Chapter> = withContext(Dispatchers.IO) {
        val service = driveService ?: return@withContext emptyList()
        val chapters = mutableListOf<Chapter>()

        // Quét file PDF trực tiếp và trong các subfolder (ví dụ Phần 1, Phần 2)
        val query = "'$mangaFolderId' in parents and trashed = false"
        val items = service.files().list()
            .setQ(query)
            .setFields("files(id, name, mimeType, size)")
            .execute()

        for (item in items.files ?: emptyList()) {
            if (item.mimeType == "application/pdf") {
                chapters.add(
                    Chapter(
                        id = item.id,
                        mangaId = mangaFolderId,
                        title = item.name.removeSuffix(".pdf"),
                        fileSizeBytes = item.getSize() ?: 0L
                    )
                )
            } else if (item.mimeType == "application/vnd.google-apps.folder") {
                // Quét tiếp subfolder
                val subQuery = "'${item.id}' in parents and mimeType = 'application/pdf' and trashed = false"
                val subItems = service.files().list()
                    .setQ(subQuery)
                    .setFields("files(id, name, size)")
                    .execute()
                for (subItem in subItems.files ?: emptyList()) {
                    chapters.add(
                        Chapter(
                            id = subItem.id,
                            mangaId = mangaFolderId,
                            title = "${item.name} - ${subItem.name.removeSuffix(".pdf")}",
                            fileSizeBytes = subItem.getSize() ?: 0L
                        )
                    )
                }
            }
        }
        chapters.sortedBy { it.title }
    }

    private fun countChapters(folderId: String): Int {
        val service = driveService ?: return 0
        return try {
            val q = "'$folderId' in parents and (mimeType = 'application/pdf' or mimeType = 'application/vnd.google-apps.folder') and trashed = false"
            service.files().list().setQ(q).setFields("files(id)").execute().files?.size ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Tải file PDF về bộ nhớ cache cục bộ để đọc mượt mà
     */
    suspend fun downloadPdfChapter(fileId: String, fileName: String, onProgress: (Float) -> Unit = {}): File = withContext(Dispatchers.IO) {
        val cacheFile = File(context.cacheDir, "manga_pdf_${fileId}.pdf")
        if (cacheFile.exists() && cacheFile.length() > 0) {
            return@withContext cacheFile
        }

        val service = driveService ?: throw IllegalStateException("Drive service chưa được khởi tạo")
        FileOutputStream(cacheFile).use { output ->
            service.files().get(fileId).executeMediaAndDownloadTo(output)
        }
        cacheFile
    }
}
