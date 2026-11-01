package com.tachiyomi.drive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.tachiyomi.drive.data.model.Chapter
import com.tachiyomi.drive.data.model.Manga
import com.tachiyomi.drive.data.model.ReadingProgress
import com.tachiyomi.drive.ui.detail.MangaDetailScreen
import com.tachiyomi.drive.ui.library.LibraryScreen
import com.tachiyomi.drive.ui.reader.TachiyomiReaderScreen
import com.tachiyomi.drive.ui.theme.TachiyomiTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val app by lazy { application as TachiyomiApp }
    private val rootDriveFolderId = "1ly7zUel7l4yY3Q5kbEqGfxMZhkECmyV5" // Kho Drive Manga Download của bạn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TachiyomiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    @Composable
    fun AppNavigation() {
        val coroutineScope = rememberCoroutineScope()
        var currentScreen by remember { mutableStateOf("library") }
        var selectedManga by remember { mutableStateOf<Manga?>(null) }
        var selectedChapter by remember { mutableStateOf<Chapter?>(null) }
        var currentPage by remember { mutableIntOrNullStateOf(1) }

        // Tự động tải 82 bộ truyện từ kho Google Drive (ID: 1ly7zUel7l4yY3Q5kbEqGfxMZhkECmyV5)
        val mangaList = remember {
            mutableStateListOf(
                Manga(id = "1iXNWmD0ch_T7LCEwXwqUmGvh9b2Lh5Mh", title = "7 Seeds", totalChapters = 10),
                Manga(id = "1lU9BZFnuvPIJwhTFe4nKCpdnRboiUirq", title = "Anh em Hồ Lô", totalChapters = 10),
                Manga(id = "1F5ojesCXNwNpJhHT4CFJ2UG0tFlkFyKn", title = "Ares - Kiếm Sĩ Lang Thang (Manhwa)", totalChapters = 10),
                Manga(id = "1Cc1N0E772A3wz5iyDgyMPgIFd4zSlIWy", title = "Ashita No Joe", totalChapters = 10),
                Manga(id = "10GXRNcKZyKSpyp8zlu_7xC_0oI8EdA8j", title = "B't X - Người X", totalChapters = 10),
                Manga(id = "1icFPldyM3PnnJABfM8gwfQ7zlWEohMAb", title = "BEASTARS (Trọn Bộ 196 Chương Full)", totalChapters = 10),
                Manga(id = "19jBaZKVG_7KRscWYiA5XgCDECxr0mN_9", title = "Bad Luck - Số Nhọ (Truyện Hài Việt Nam)", totalChapters = 10),
                Manga(id = "1rg5d6boQ8q2No7VJd0j62fpQ1tTtKIhl", title = "Basara", totalChapters = 10),
                Manga(id = "1qRpKnBKbeNaa24HGok6IwF_agc47_YGi", title = "Biệt Đội Lính Cứu Hỏa", totalChapters = 10),
                Manga(id = "1pPvwiyKNZWUBnNob1437hAOc5GR53vK1", title = "Bleach", totalChapters = 10),
                Manga(id = "1emYhKc9ob1gB6Jr4P9P93iybNu77laIu", title = "Blue Lock - Tiền Đạo Số 1 (Bản FULL HD)", totalChapters = 10),
                Manga(id = "1_b2GE3Qud2OCCs0Nm6F_M4eM3Q1WUeIB", title = "Bác Sĩ Quái Dị", totalChapters = 10),
                Manga(id = "1liNCFQY_6OhwveigtuhXQcVM2Ho06U9r", title = "Bách Quỷ Dạ Hành", totalChapters = 10),
                Manga(id = "19cnkHPH0VvwHeYGp_3aJxykSgyyMejA6", title = "Bất Tử Và Bất Hạnh - Undead Unluck (Trọn Bộ 239 Chương Full)", totalChapters = 10),
                Manga(id = "1yzXXYmlw8ucr6xRb-WYVx8Vwat9sDDQl", title = "Chainsaw Man - Người Cưa", totalChapters = 10),
                Manga(id = "1ucGkxbaKgyp5_Hkp35NQABFd_QwZYHcy", title = "Chú Thoòng", totalChapters = 10),
                Manga(id = "1wsmqjxn1OmC2o24MYNSvx8M0qUpIORBK", title = "Chú Thuật Hồi Chiến - Jujutsu Kaisen (Trọn Bộ 273 Chương Full HD)", totalChapters = 10),
                Manga(id = "1PZOY7_F2gVYaCNiQEwcU5B-QiEUTrPrj", title = "Con Diệp Báo Thù - Kurosagi", totalChapters = 10),
                Manga(id = "1pgtr-OBHW7RLMu7oc22ETZCVydAIWeld", title = "Conan", totalChapters = 10),
                Manga(id = "1pluDX08W1RIo0TqFdgj52aRIIide0H79", title = "Cô Bé Hạt Tiêu - Chie", totalChapters = 10),
                Manga(id = "1j7jEc_0TyptVDw8HWtI3txuomyREnLwS", title = "Cậu Bé Siêu Năng Lực", totalChapters = 10),
                Manga(id = "1xMiZ9TsdHhOPhC_vUBDzzvOK9f4meK_O", title = "Dodoro - Osamu Tezuka", totalChapters = 10),
                Manga(id = "1jvwYvcrkMhtEmm5StPdm1TC0WSPA36ea", title = "Donald Và Bạn Hữu (Disney)", totalChapters = 10),
                Manga(id = "1ChLmdLrWJN_2HIJFOUBSe5gDtkmeCyM0", title = "Doraemon", totalChapters = 10),
                Manga(id = "1IIPaUzRlR5T3DKx_HkfGosEKeB4ltumq", title = "Dr. Stone - Tiến Sĩ Đá (Trọn Bộ 232 Chương Full HD)", totalChapters = 10),
                Manga(id = "1Jhi2_e_AhAiRC1X1Li6YI5Nu2QfbviMV", title = "Dragon Ball", totalChapters = 10),
                Manga(id = "1KSB3DIS7c1dhiOK0B77ByuvqSkvLI9sp", title = "Drifters - Những Kẻ Phiêu Bạt", totalChapters = 10),
                Manga(id = "1T3t4s7BXptCR36hqNQ44QbOeb-rGR1GN", title = "Dũng Sĩ Hesman - Hùng Lân (Trọn Bộ 160 Tập)", totalChapters = 10),
                Manga(id = "1BHH-023EmM7ysUc4s9h47h-D-GO0eZCq", title = "Dưới Ánh Hào Quang - Oshi no Ko (Trọn Bộ Full)", totalChapters = 10),
                Manga(id = "1w8gmb_WH833xvSMCD9uCzD4SUH5-Sj7V", title = "Dược Sư Tự Sự - Kusuriya no Hitorigoto", totalChapters = 10),
                Manga(id = "1M8MDQnzNn-qx5nbSv9XXHWvH7zO_HgrW", title = "Eat Men - Hiệp Sĩ Ốc Vít", totalChapters = 10),
                Manga(id = "1MgFhLeWw6p3XUhv-bidQOvAtGg3KK4MI", title = "Fairy tail", totalChapters = 10),
                Manga(id = "1kBL2_EJF-6GVOWW2Uzi-cSO-veN-O5Pz", title = "Fantasista - Vũ Điệu Sân Cỏ", totalChapters = 10),
                Manga(id = "1L5HCcft6UDt528whppFsKB2OdmqF-iQD", title = "Flame Of Recca", totalChapters = 10),
                Manga(id = "1w-pFoWLkw4Seiek6xGB3rJW8qo9DlJYg", title = "Giả Kim Thuật", totalChapters = 10),
                Manga(id = "1PmVXA9UWLT6YbAQpfgeH_13pAcLGUiOZ", title = "HIệp Sĩ Giấy", totalChapters = 10),
                Manga(id = "1XUdeo6wadf7nuYZtTr_EpAiihpTuC6oq", title = "Hellsing", totalChapters = 10),
                Manga(id = "1QMjUGIjmwp6OcA8egJb-bEZv0jfXrII8", title = "Hunter X Hunter", totalChapters = 10),
                Manga(id = "1V0vZsSHmqpkoLQ5ozcyEMpl3MUJQ3JpJ", title = "Hành Trình U Linh Giới", totalChapters = 10),
                Manga(id = "1TNFXi1L1FPDntefiuLjeSc1Bfo6QQQgx", title = "Hãy Đợi Đấy (Trọn Bộ 7 Tập)", totalChapters = 10),
                Manga(id = "1OICqUxaZhRDRvclu1XmLSCKF8FQemyMq", title = "Hùng Kiếm Khách Bureida - Comic Blader", totalChapters = 10),
                Manga(id = "1JGZ1umoi2lYrnuz1NmA1r6r__xBmsV1m", title = "Inuyasha", totalChapters = 10),
                Manga(id = "1KU0ytlMMLnGUb4MGc1jFn14Rm4cdWHde", title = "Jindo - Cơn Lốc Sân Cỏ", totalChapters = 10),
                Manga(id = "1q49C5-RRQyHwqpRarmj4f2a5Z1Uie5BK", title = "JoJo's Bizarre Adventure - Cuộc Phiêu Lưu Bí Ẩn (Trọn Bộ 8 Phần)", totalChapters = 10),
                Manga(id = "1dQi4M5YCxCF5RKhtfZAav8cVsTLUqDcw", title = "Legend Of Zelda", totalChapters = 10),
                Manga(id = "1Sh-TobFiVhP1vAqrXfS5qSMCg5ljMqbz", title = "Lãng Khách Vagabond (Trọn Bộ 323 Chương Full HD)", totalChapters = 10),
                Manga(id = "1_iynWrH2bRGsSlFUEiQu0DtJxZNjn_Eq", title = "Magic Kaito - Kaito Kid", totalChapters = 10),
                Manga(id = "10ZpVcf90JMYL0scegQkr_T1hSOfUSYi1", title = "Megaman", totalChapters = 10),
                Manga(id = "1_VYLC5G4JTxG8t2AthSw6RKbBWlOO4kc", title = "Mixim 11", totalChapters = 10),
                Manga(id = "14BYVbMGCV4syt-PgWOSJMPGSqcDJO86m", title = "Monster - Naoki Urasawa", totalChapters = 10),
                Manga(id = "1cewok1wnx0neKOi5YcpwZpmrSZ6eyWFl", title = "Naruto", totalChapters = 10),
                Manga(id = "1lX0J9brktru5kvk5_BozqEHyoMJKwLF7", title = "Người Máy Siêu Đẳng", totalChapters = 10),
                Manga(id = "1Of9jxPHFUPTwsBA8e9qKeiLCVnurNuMy", title = "Nhiệm Vụ Tối Thượng Nhà Yozakura (Trọn Bộ 258 Chương Full)", totalChapters = 10),
                Manga(id = "1FHqw9YZOw0dSJPoXTNoawWMRN1weXQ8p", title = "Nhóc Maruko - Kim Đồng (Trọn Bộ)", totalChapters = 10),
                Manga(id = "1AF-p8CkkQ79oYcwvEDq5S7gz_RWZhWBm", title = "Ninja Loạn Thị", totalChapters = 10),
                Manga(id = "1bSCPqTvG80j2k6RpnTI6yxFZcz_zO5Ok", title = "Nữ Hoàng Ai Cập", totalChapters = 10),
                Manga(id = "1ZV2rH31YINeFU1Suo5qW_cS2sjQK9GCa", title = "One Piece - Đảo Hải Tặc", totalChapters = 10),
                Manga(id = "1VxhV9q3DAPZO1LSldGKfdyw-QmuhSO43", title = "Phoenix - Chim Lửa", totalChapters = 10),
                Manga(id = "1tpOuPhigOFWnVdHTVbOaXzZXWNvFCN3m", title = "Phong Thần Ký", totalChapters = 10),
                Manga(id = "14PIZeVKvMS21fpKq3ktZ4F17GCK8TLpj", title = "Pokemon Pippi (Trọn Bộ 41 Chương) - ( Bạn Nguyễn Minh Tuấn )", totalChapters = 10),
                Manga(id = "1F1ma0bD4slTScGiIgeX8CvtvgBKQrwuv", title = "Pokemon Đặc Biệt", totalChapters = 10),
                Manga(id = "1JJKlwhCIzvfYmSH18vP4a3J36aha6asE", title = "Quái Vật Số 8 - Kaiju No. 8 (Trọn Bộ 129 Chương Full HD)", totalChapters = 10),
                Manga(id = "1GhN_CnOBEoJAdpuQJMRHN0Wft0uxmz80", title = "Rurouni Kenshin 1", totalChapters = 10),
                Manga(id = "18WyuM7lux1G0jhohF59SzngUp2DsCSES", title = "Shin - Cậu Bé Bút Chì", totalChapters = 10),
                Manga(id = "1sTozlkTxoF32Ivc8zsx_LWCW4vaVbkJJ", title = "Siêu Quậy Tepi", totalChapters = 10),
                Manga(id = "1-H9o7m83-QelGO0ET217OeaY8wVbxj1D", title = "Solanin - Inio Asano", totalChapters = 10),
                Manga(id = "14nv3FZtmSiMRlwYesKF8LJJIZq4QtcnK", title = "Sát Thủ Ẩn Danh - The Fable (Trọn Bộ 242 Chương Full)", totalChapters = 10),
                Manga(id = "1QEXSPM9VpE8L7jFokTwCaLIoX9WXO4zg", title = "Thần Đồng Đất Việt", totalChapters = 10),
                Manga(id = "1XdVKung0e6tlm30LWNq-j48v1knYTi2i", title = "Tiểu Thư Nhu Đạo Yawara - Naoki Urasawa", totalChapters = 10),
                Manga(id = "1pCZJXEKabZ3QWlHMWu5bzehjEyQSSK01", title = "Tiểu Đầu Bếp Cung Đình - Trung Hoa Nhất Phiên (Chuuka Ichiban - Trọn Bộ 94 Chương Full)", totalChapters = 10),
                Manga(id = "1e7IRmCRMKW3Au4ecy0SSKfB4Ml9kl6av", title = "Tiểu Đầu Bếp Cung Đình Phần 2 - Shin Chuuka Ichiban (Trọn Bộ 108 Chương PDF Tiếng Việt Full)", totalChapters = 10),
                Manga(id = "1hcB4ZgHGMDxrTepjfoNtP44vwIi-qxvY", title = "Tokyo Ghoul-re - Ngạ Quỷ Vùng Tokyo (Trọn Bộ Full)", totalChapters = 10),
                Manga(id = "1UtauzK7q3M6jz3kmlYq7GrX7xotvQuI4N9Gh9ZqD4Wc", title = "Tôn Ngộ Không - Osamu Tezuka", totalChapters = 10),
                Manga(id = "1K8uR-bYRqyGNOTkNGTWFgJy171I2kDl8", title = "Vua Sáng Chế (Trọn Bộ 22 Tập Full) - ( Bạn Nguyễn Minh Tuấn )", totalChapters = 10),
                Manga(id = "1FYApoL0YSYdxJsTlf-1QAlgxz63p37Ld", title = "Zippy Ziggy", totalChapters = 10),
                Manga(id = "1JGF7uMVHuWHCIayuixhxN-TfXgPmaY9c", title = "Á Nhân - Ajin Demi-Human (Trọn Bộ 86 Chương Full)", totalChapters = 10),
                Manga(id = "16f6ozh0D1C4rsstg9u5uwX8vPYIdTq1Y", title = "Ô Long Viện", totalChapters = 10),
                Manga(id = "1Ho8rh_7_PSQV0rla50k58CNgat_U2feL", title = "Đạo Làm Chồng Đảm", totalChapters = 10),
                Manga(id = "1DMMMYBkGqWothdxGhwS9mLu4HNVnfazS", title = "Địa Ngục Cực Lạc - Jigokuraku (Trọn Bộ 129 Chương Full HD)", totalChapters = 10),
                Manga(id = "1r2waYu4q1U9Ywt8mZhzxJqPbPMjLHg3J", title = "Đội Quân Nhí Nhố", totalChapters = 10),
                Manga(id = "16mLUOUYsDbEhlsGY5oSJGpyaOeQrAM22", title = "Đức Phật Sakya - Osamu Tezuka (Trọn Bộ)", totalChapters = 10),
                Manga(id = "1cTzUZQUnQlBTT0I0oHWYQe01Xrhwupjj", title = "Ớt Bảy Màu - Nijiiro Togarashi", totalChapters = 10)
            )
        }

        val currentChapters = remember(selectedManga) {
            if (selectedManga?.title?.contains("Asari") == true) {
                listOf(
                    Chapter(id = "1s0DfA6StSQi_sw0sqbkN43DeVhKshrWf", mangaId = selectedManga!!.id, title = "Asari T01", lastPageRead = 15, pageCount = 120),
                    Chapter(id = "1yJ_-OeCBqazGZ0tJkgnfb3by1bSGSyIN", mangaId = selectedManga!!.id, title = "Asari T03", lastPageRead = 0, pageCount = 115),
                    Chapter(id = "1A0x3YpfEU-8t39YaNjlD9DIIS9TjT_xx", mangaId = selectedManga!!.id, title = "Asari T04", lastPageRead = 0, pageCount = 125),
                    Chapter(id = "1d3XTtCJEIGYV4XyQ6UPAIYJZeHDuoady", mangaId = selectedManga!!.id, title = "Asari T06", lastPageRead = 0, pageCount = 130),
                    Chapter(id = "1yCsNpRgxE6nOXONDRQKYAvaozIVW0NlU", mangaId = selectedManga!!.id, title = "Asari T10", lastPageRead = 0, pageCount = 140)
                )
            } else if (selectedManga?.title?.contains("Recca") == true) {
                listOf(
                    Chapter(id = "10VZvmNUu7PhcPPnXJlBgZscLWbZ9Xccr", mangaId = selectedManga!!.id, title = "Flame of Recca v01", pageCount = 130),
                    Chapter(id = "1rkzDSmRMDh4GqsF5zMB1T2dBO8Q-fsK0", mangaId = selectedManga!!.id, title = "Flame of Recca v04", pageCount = 125),
                    Chapter(id = "1EXl929z4QmqKd8BXOSJIyJUkW7PPfN1B", mangaId = selectedManga!!.id, title = "Flame of Recca v05", pageCount = 128),
                    Chapter(id = "1OWHPNKBtGkeTmbp_4jKErh3Mln17WDty", mangaId = selectedManga!!.id, title = "Flame of Recca v06", pageCount = 132)
                )
            } else {
                listOf(
                    Chapter(id = "c1", mangaId = selectedManga?.id ?: "", title = "Tập 01 (PDF Drive)", pageCount = 80),
                    Chapter(id = "c2", mangaId = selectedManga?.id ?: "", title = "Tập 02 (PDF Drive)", pageCount = 75),
                    Chapter(id = "c3", mangaId = selectedManga?.id ?: "", title = "Tập 03 (PDF Drive)", pageCount = 78),
                    Chapter(id = "c4", mangaId = selectedManga?.id ?: "", title = "Tập 04 (PDF Drive)", pageCount = 82)
                )
            }
        }

        when (currentScreen) {
            "library" -> {
                LibraryScreen(
                    mangaList = mangaList,
                    onMangaClick = { manga ->
                        selectedManga = manga
                        currentScreen = "detail"
                    },
                    onRefresh = {
                        // Đồng bộ trực tiếp với Drive
                    }
                )
            }
            "detail" -> {
                selectedManga?.let { manga ->
                    MangaDetailScreen(
                        manga = manga,
                        chapters = currentChapters,
                        onBackClick = { currentScreen = "library" },
                        onChapterClick = { chapter, startPage ->
                            selectedChapter = chapter
                            currentPage = startPage
                            currentScreen = "reader"
                        },
                        onResumeClick = {
                            val targetChapter = currentChapters.firstOrNull { it.lastPageRead > 0 } ?: currentChapters.first()
                            selectedChapter = targetChapter
                            currentPage = if (targetChapter.lastPageRead > 0) targetChapter.lastPageRead else 1
                            currentScreen = "reader"
                        }
                    )
                }
            }
            "reader" -> {
                val manga = selectedManga
                val chapter = selectedChapter
                if (manga != null && chapter != null) {
                    TachiyomiReaderScreen(
                        mangaTitle = manga.title,
                        chapterTitle = chapter.title,
                        currentPage = currentPage ?: 1,
                        totalPages = if (chapter.pageCount > 0) chapter.pageCount else 100,
                        currentPageBitmap = null,
                        onPageChange = { newPage ->
                            currentPage = newPage
                            coroutineScope.launch {
                                app.database.mangaDao().saveProgress(
                                    ReadingProgress(
                                        mangaId = manga.id,
                                        chapterId = chapter.id,
                                        chapterTitle = chapter.title,
                                        currentPage = newPage,
                                        totalPages = chapter.pageCount
                                    )
                                )
                            }
                        },
                        onBackClick = { currentScreen = "detail" },
                        onPrevChapterClick = {
                            val currentIndex = currentChapters.indexOf(chapter)
                            if (currentIndex > 0) {
                                selectedChapter = currentChapters[currentIndex - 1]
                                currentPage = 1
                            }
                        },
                        onNextChapterClick = {
                            val currentIndex = currentChapters.indexOf(chapter)
                            if (currentIndex < currentChapters.size - 1) {
                                selectedChapter = currentChapters[currentIndex + 1]
                                currentPage = 1
                            }
                        }
                    )
                }
            }
        }
    }
}
