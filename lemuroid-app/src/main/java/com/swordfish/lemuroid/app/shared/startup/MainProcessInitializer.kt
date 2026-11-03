package com.swordfish.lemuroid.app.shared.startup

import android.content.Context
import androidx.startup.Initializer
import androidx.work.WorkManagerInitializer
import com.swordfish.lemuroid.app.shared.library.LibraryIndexScheduler
import com.swordfish.lemuroid.app.shared.savesync.SaveSyncWork
import com.swordfish.lemuroid.lib.storage.DirectoriesManager
import timber.log.Timber
import kotlin.concurrent.thread

class MainProcessInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Timber.i("Requested initialization of main process tasks")
        // Move saves to their user visible location without waiting for a game to be launched.
        thread(name = "SavesMigration") {
            runCatching { DirectoriesManager(context.applicationContext).migrateLegacyUserData() }
                .onFailure { Timber.e(it, "Error while migrating saves") }
        }
        SaveSyncWork.enqueueAutoWork(context, 0)
        LibraryIndexScheduler.scheduleCoreUpdate(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(WorkManagerInitializer::class.java, DebugInitializer::class.java)
    }
}
