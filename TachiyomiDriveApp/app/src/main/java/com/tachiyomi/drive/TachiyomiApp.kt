package com.tachiyomi.drive

import android.app.Application
import com.tachiyomi.drive.data.db.AppDatabase
import com.tachiyomi.drive.data.drive.GoogleDriveManager

class TachiyomiApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val driveManager by lazy { GoogleDriveManager(this) }

    override fun onCreate() {
        super.onCreate()
    }
}
