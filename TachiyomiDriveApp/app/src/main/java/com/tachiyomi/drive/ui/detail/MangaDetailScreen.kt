package com.tachiyomi.drive.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tachiyomi.drive.data.model.Chapter
import com.tachiyomi.drive.data.model.Manga
import com.tachiyomi.drive.ui.theme.TachiyomiAccent
import com.tachiyomi.drive.ui.theme.TachiyomiSurface
import com.tachiyomi.drive.ui.theme.TachiyomiTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailScreen(
    manga: Manga,
    chapters: List<Chapter>,
    onBackClick: () -> Unit,
    onChapterClick: (Chapter, Int) -> Unit,
    onResumeClick: () -> Unit,
    onDownloadChapter: (Chapter) -> Unit = {},
    onDownloadAllChapters: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(manga.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Trở lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = manga.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kho truyện Google Drive • Hỗ trợ tải Offline",
                        fontSize = 13.sp,
                        color = TachiyomiTextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Hàng nút: Đọc tiếp & Tải tất cả Offline
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onResumeClick,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = TachiyomiAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (manga.lastReadChapterTitle != null) 
                                    "Đọc tiếp (Trang ${manga.lastReadPage})"
                                else "Bắt đầu đọc",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = onDownloadAllChapters,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tải Offline", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${chapters.size} CHƯƠNG",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TachiyomiAccent
                        )
                        val downloadedCount = chapters.count { it.isDownloaded }
                        if (downloadedCount > 0) {
                            Text(
                                text = "Đã tải $downloadedCount tập về máy",
                                fontSize = 12.sp,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                    Divider(modifier = Modifier.padding(top = 8.dp), color = TachiyomiSurface)
                }
            }

            // Danh sách các tập/chương
            items(chapters) { chapter ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChapterClick(chapter, if (chapter.lastPageRead > 0) chapter.lastPageRead else 1) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = chapter.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (chapter.isRead) TachiyomiTextSecondary else MaterialTheme.colorScheme.onBackground
                            )
                            if (chapter.isDownloaded) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Offline",
                                    fontSize = 10.sp,
                                    color = Color(0xFF10B981),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        if (chapter.lastPageRead > 0) {
                            Text(
                                text = "Đang đọc trang ${chapter.lastPageRead}",
                                fontSize = 12.sp,
                                color = TachiyomiAccent
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Nút Tải về Offline
                        IconButton(onClick = { onDownloadChapter(chapter) }) {
                            Icon(
                                imageVector = if (chapter.isDownloaded) Icons.Default.CheckCircle else Icons.Default.FileDownload,
                                contentDescription = "Tải về offline",
                                tint = if (chapter.isDownloaded) Color(0xFF10B981) else Color.Gray
                            )
                        }

                        // Nút Đọc
                        IconButton(onClick = { onChapterClick(chapter, if (chapter.lastPageRead > 0) chapter.lastPageRead else 1) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Đọc", tint = TachiyomiAccent)
                        }
                    }
                }
                Divider(color = TachiyomiSurface, thickness = 0.5.dp)
            }
        }
    }
}
