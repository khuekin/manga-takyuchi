# Tachiyomi Drive - Manga PDF Reader for Android

Ứng dụng đọc truyện tranh Manga định dạng PDF lưu trữ trên Google Drive với giao diện chuẩn Tachiyomi.

## 🚀 Tính năng nổi bật
1. **Giao diện chuẩn phong cách Tachiyomi**:
   - Giao diện tối AMOLED / Dark Slate tối ưu cho đọc truyện.
   - Thư viện dạng lưới với ảnh bìa, tên truyện, số chương và huy hiệu tiến độ đọc.
   - Quản lý chi tiết truyện và danh sách từng chương (Chapter / Tập).
2. **Trình đọc Manga Reader chuyên nghiệp**:
   - Hỗ trợ chế độ đọc chuẩn Manga Nhật Bản (Right-to-Left: Vuốt từ phải sang trái), Truyện phương Tây (Left-to-Right) và Webtoon (Cuộn dọc).
   - Vùng chạm thông minh (Tap Zones):
     - Mép trái: Sang trang trước / sau tùy chế độ đọc.
     - Vùng trung tâm: Bật / tắt thanh điều khiển (Thanh tiêu đề, slider kéo trang, chuyển chương nhanh).
     - Mép phải: Sang trang tiếp theo / lùi trang.
   - Thanh trượt trang mượt mà (Scrubber slider) hiển thị `Trang X / Tổng số trang`.
   - Nút Next / Previous chuyển nhanh qua lại giữa các tập.
3. **Đọc trực tiếp từ kho truyện Google Drive**:
   - Hỗ trợ duyệt tự động thư mục gốc truyện (Mặc định `Manga Download`).
   - Tự động nhận diện thư mục con là các bộ truyện (ví dụ: *Cô Bé Tinh Nghịch - Asari*, *Deathtopia*, *Flame of Recca*, *Đại Chiến Nhân Thần*, *Dáng Hình Thanh Âm*...).
   - Tải và lưu đệm (cache) PDF thông minh vào bộ nhớ máy để đọc mượt mà không bị giật lag khi mất mạng.
4. **Tự động lưu tiến độ đọc (Bookmarks & History)**:
   - Cơ sở dữ liệu SQLite / Room Database lưu lại chính xác tập và trang đang đọc dở cho từng bộ truyện.
   - Nút **"Đọc tiếp" (Resume Reading)** ngay đầu trang chi tiết cho phép mở lại đúng vị trí trang bạn vừa dừng lại chỉ với 1 chạm.

---

## 🛠 Cách tạo file APK để cài đặt lên điện thoại

### Cách 1: Tự động Build APK bằng GitHub Actions (Không cần cài Android Studio)
1. Tạo một repository mới trên GitHub (Private hoặc Public).
2. Đẩy toàn bộ mã nguồn trong thư mục này lên GitHub:
   ```bash
   git init
   git add .
   git commit -m "Initial commit Tachiyomi Drive"
   git remote add origin https://github.com/<tai-khoan-cua-ban>/tachiyomi-drive.git
   git branch -M main
   git push -u origin main
   ```
3. Vào tab **Actions** trên GitHub repository của bạn.
4. Bạn sẽ thấy quy trình **Build Tachiyomi Drive APK** (đã được cấu hình sẵn trong `.github/workflows/build-apk.yml`) tự động chạy và hoàn tất sau ~2-3 phút.
5. Nhấp vào bản build thành công và tải về file `TachiyomiDrive-Debug-APK` chứa file `.apk` cài trực tiếp vào điện thoại Android.

---

### Cách 2: Mở và Build bằng Android Studio
1. Cài đặt **Android Studio** (phiên bản Iguana, Jellyfish hoặc mới hơn) kèm JDK 17.
2. Chọn **File -> Open...** và dẫn tới thư mục `TachiyomiDriveApp`.
3. Đợi Gradle đồng bộ (Sync Project with Gradle Files).
4. Vào menu **Build -> Build Bundle(s) / APK(s) -> Build APK(s)**.
5. Sau khi build xong, file APK sẽ nằm tại: `app/build/outputs/apk/debug/app-debug.apk`.
6. Sao chép file `.apk` vào điện thoại Android và chọn Cài đặt (Install).

---

## 📂 Cấu trúc thư mục dự án
```
TachiyomiDriveApp/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/tachiyomi/drive/
│   │   │   ├── MainActivity.kt
│   │   │   ├── TachiyomiApp.kt
│   │   │   ├── data/
│   │   │   │   ├── db/AppDatabase.kt
│   │   │   │   ├── drive/GoogleDriveManager.kt
│   │   │   │   ├── model/Manga.kt, Chapter.kt, ReadingProgress.kt
│   │   │   │   └── pdf/PdfPageRenderer.kt
│   │   │   └── ui/
│   │   │       ├── detail/MangaDetailScreen.kt
│   │   │       ├── library/LibraryScreen.kt
│   │   │       ├── reader/TachiyomiReaderScreen.kt
│   │   │       └── theme/Theme.kt
│   │   └── res/values/
│   └── build.gradle.kts
├── .github/workflows/build-apk.yml
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```
