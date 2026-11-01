package com.tachiyomi.drive.ui.reader

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tachiyomi.drive.ui.theme.TachiyomiAccent
import kotlinx.coroutines.launch

/**
 * 2 Chế độ hiển thị trang theo yêu cầu:
 * - ONE_PAGE: Hiển thị 1 trang duy nhất toàn màn hình, ấn 2 bên mép để chuyển trang
 * - CONTINUOUS: Cuộn dọc liên tục các trang từ trên xuống dưới mượt mà như Webtoon
 */
enum class PageDisplayMode {
    ONE_PAGE,
    CONTINUOUS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TachiyomiReaderScreen(
    mangaTitle: String,
    chapterTitle: String,
    currentPage: Int,
    totalPages: Int,
    currentPageBitmap: Bitmap?,
    onPageChange: (Int) -> Unit,
    onBackClick: () -> Unit,
    onPrevChapterClick: () -> Unit,
    onNextChapterClick: () -> Unit
) {
    var showControls by remember { mutableStateOf(false) }
    var displayMode by remember { mutableStateOf(PageDisplayMode.ONE_PAGE) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Lắng nghe cuộn trong Continuous mode để cập nhật số trang đang xem
    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (displayMode == PageDisplayMode.CONTINUOUS) {
            val newPage = (listState.firstVisibleItemIndex + 1).coerceIn(1, totalPages)
            if (newPage != currentPage) {
                onPageChange(newPage)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // --- NỘI DUNG HIỂN THỊ CHÍNH (FULL MÀN HÌNH) ---
        if (displayMode == PageDisplayMode.ONE_PAGE) {
            // 1. CHẾ ĐỘ ONE PAGE: Hiển thị 1 trang căn giữa toàn màn hình
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (currentPageBitmap != null) {
                    Image(
                        bitmap = currentPageBitmap.asImageBitmap(),
                        contentDescription = "Trang $currentPage",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    CircularProgressIndicator(color = TachiyomiAccent)
                }
            }
        } else {
            // 2. CHẾ ĐỘ CONTINUOUS: Cuộn dọc liên tục các trang từ trên xuống
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 56.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(totalPages) { pageIdx ->
                    val pageNum = pageIdx + 1
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(600.dp)
                            .background(Color(0xFF1A1A1A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Trang $pageNum / $totalPages (Drive PDF)",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // --- CÁC VÙNG CHẠM THÔNG MINH (TAP ZONES) ---
        // 1. Ấn lên trên (Top 22%): Hiện / Ẩn thanh Menu
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.22f)
                .align(Alignment.TopCenter)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showControls = !showControls
                }
        )

        // 2. Vùng ở giữa dưới: Ấn vào 2 bên để chuyển trang (trong chế độ One Page)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.78f)
                .align(Alignment.BottomCenter)
        ) {
            // Vùng mép trái (38%): Chuyển lùi trang
            Box(
                modifier = Modifier
                    .weight(0.38f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (displayMode == PageDisplayMode.ONE_PAGE) {
                            if (currentPage > 1) onPageChange(currentPage - 1)
                        } else {
                            coroutineScope.launch {
                                listState.animateScrollToItem((currentPage - 2).coerceAtLeast(0))
                            }
                        }
                    }
            )

            // Vùng trung tâm (24%): Bật / tắt Menu
            Box(
                modifier = Modifier
                    .weight(0.24f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showControls = !showControls
                    }
            )

            // Vùng mép phải (38%): Chuyển tiến trang
            Box(
                modifier = Modifier
                    .weight(0.38f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (displayMode == PageDisplayMode.ONE_PAGE) {
                            if (currentPage < totalPages) onPageChange(currentPage + 1)
                        } else {
                            coroutineScope.launch {
                                listState.animateScrollToItem(currentPage.coerceAtMost(totalPages - 1))
                            }
                        }
                    }
            )
        }

        // --- HUY HIỆU SỐ TRANG GÓC DƯỚI (LUÔN HIỆN KIỂU TACHIYOMI) ---
        if (!showControls) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "$currentPage / $totalPages • ${if (displayMode == PageDisplayMode.ONE_PAGE) "ONE PAGE" else "CONTINUOUS"}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }
        }

        // --- THANH MENU ĐIỀU KHIỂN TRÊN (TOP BAR OVERLAY) ---
        AnimatedVisibility(
            visible = showControls,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            TopAppBar(
                title = {
                    Column {
                        Text(mangaTitle, maxLines = 1, fontSize = 15.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text(chapterTitle, maxLines = 1, fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Trở lại", tint = Color.White)
                    }
                },
                actions = {
                    // Chuyển đổi giữa 2 chế độ: One Page và Continuous
                    IconButton(
                        onClick = {
                            displayMode = if (displayMode == PageDisplayMode.ONE_PAGE) 
                                PageDisplayMode.CONTINUOUS else PageDisplayMode.ONE_PAGE
                        }
                    ) {
                        Icon(
                            imageVector = if (displayMode == PageDisplayMode.ONE_PAGE) 
                                Icons.Default.VerticalSplit else Icons.Default.ViewCarousel,
                            contentDescription = "Chuyển chế độ đọc",
                            tint = TachiyomiAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.9f)
                )
            )
        }

        // --- THANH MENU ĐIỀU KHIỂN DƯỚI (BOTTOM BAR OVERLAY) ---
        AnimatedVisibility(
            visible = showControls,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Thanh trượt kéo nhanh số trang
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("$currentPage", color = Color.White, fontSize = 13.sp)
                    Slider(
                        value = currentPage.toFloat(),
                        onValueChange = { targetPage ->
                            onPageChange(targetPage.toInt())
                            if (displayMode == PageDisplayMode.CONTINUOUS) {
                                coroutineScope.launch {
                                    listState.scrollToItem((targetPage.toInt() - 1).coerceAtLeast(0))
                                }
                            }
                        },
                        valueRange = 1f..totalPages.coerceAtLeast(1).toFloat(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = TachiyomiAccent,
                            activeTrackColor = TachiyomiAccent
                        )
                    )
                    Text("$totalPages", color = Color.White, fontSize = 13.sp)
                }

                // Các nút chuyển tập và chuyển trang
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onPrevChapterClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("Tập trước", fontSize = 12.sp)
                    }

                    Row {
                        IconButton(
                            onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
                            enabled = currentPage > 1
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Trang trước", tint = Color.White)
                        }

                        IconButton(
                            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
                            enabled = currentPage < totalPages
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Trang sau", tint = Color.White)
                        }
                    }

                    OutlinedButton(
                        onClick = onNextChapterClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("Tập tiếp", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
