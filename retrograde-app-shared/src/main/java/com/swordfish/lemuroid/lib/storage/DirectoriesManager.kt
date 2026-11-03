package com.swordfish.lemuroid.lib.storage

import android.content.Context
import timber.log.Timber
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.ConcurrentHashMap

class DirectoriesManager(private val appContext: Context) {
    @Deprecated("Use the external states directory")
    fun getInternalStatesDirectory(): File =
        File(appContext.filesDir, "states").apply {
            mkdirs()
        }

    fun getCoresDirectory(): File =
        File(appContext.filesDir, "cores").apply {
            mkdirs()
        }

    fun getSystemDirectory(): File =
        File(appContext.filesDir, "system").apply {
            mkdirs()
        }

    fun getStatesDirectory(): File = getUserDataDirectory("states")

    fun getStatesPreviewDirectory(): File = getUserDataDirectory("state-previews")

    fun getSavesDirectory(): File = getUserDataDirectory("saves")

    fun migrateLegacyUserData() {
        getSavesDirectory()
        getStatesDirectory()
        getStatesPreviewDirectory()
    }

    fun getInternalRomsDirectory(): File =
        File(appContext.getExternalFilesDir(null), "roms").apply {
            mkdirs()
        }

    /**
     * Saves live in Android/media/<package> so that users can browse and back them up with any file
     * manager. Android/data (used by older versions) is no longer accessible to them since Android 11.
     */
    private fun getUserDataDirectory(name: String): File {
        val directory = File(getUserDataRoot(), name)
        migrateLegacyDirectory(File(appContext.getExternalFilesDir(null), name), directory)
        directory.mkdirs()
        return directory
    }

    @Suppress("DEPRECATION")
    private fun getUserDataRoot(): File {
        val mediaRoot =
            appContext.externalMediaDirs.firstOrNull { it != null }
                ?: return appContext.getExternalFilesDir(null)!!

        // Keep state previews out of the gallery.
        val noMediaFile = File(mediaRoot.apply { mkdirs() }, NO_MEDIA_FILE_NAME)
        if (!noMediaFile.exists()) {
            runCatching { noMediaFile.createNewFile() }
        }
        return mediaRoot
    }

    /** Moves files from the legacy Android/data location once per process. Existing files are never overwritten. */
    private fun migrateLegacyDirectory(
        legacyDirectory: File,
        directory: File,
    ) {
        if (legacyDirectory.absolutePath == directory.absolutePath) return
        if (directory.absolutePath in migratedDirectories) return

        // Callers must not see a half migrated directory, so they wait for the migration to complete.
        synchronized(migratedDirectories) {
            if (directory.absolutePath in migratedDirectories) return

            if (legacyDirectory.exists()) {
                // Main and game processes can both get here, so we also serialize across processes.
                RandomAccessFile(File(appContext.filesDir, MIGRATION_LOCK_FILE_NAME), "rw").channel.use { channel ->
                    channel.lock().use {
                        legacyDirectory.walkBottomUp()
                            .filter { it.isFile }
                            .forEach { moveFile(it, File(directory, it.toRelativeString(legacyDirectory))) }

                        legacyDirectory.walkBottomUp()
                            .filter { it.isDirectory }
                            .forEach { it.delete() } // Only succeeds on empty directories
                    }
                }
            }

            migratedDirectories.add(directory.absolutePath)
        }
    }

    private fun moveFile(
        source: File,
        destination: File,
    ) {
        if (destination.exists()) {
            // Never overwrite, but don't leave the legacy file behind either: it might be the newer one.
            val backup = File(destination.parentFile, "${destination.name}$LEGACY_BACKUP_SUFFIX")
            Timber.w("Save migration conflict for $source, keeping it as $backup")
            if (!backup.exists()) moveFile(source, backup)
            return
        }

        runCatching {
            destination.parentFile?.mkdirs()
            if (!source.renameTo(destination)) {
                val tmpFile = File(destination.parentFile, "${destination.name}.tmp")
                source.copyTo(tmpFile, overwrite = true)
                // Timestamps drive SavesCoherencyEngine and save sync, so they must survive the copy.
                if (!tmpFile.setLastModified(source.lastModified())) {
                    Timber.w("Cannot preserve last modified time for $source")
                }
                if (!tmpFile.renameTo(destination)) error("Cannot rename $tmpFile")
                source.delete()
            }
        }.onFailure { Timber.e(it, "Error while migrating save file $source") }
    }

    companion object {
        private const val NO_MEDIA_FILE_NAME = ".nomedia"
        private const val MIGRATION_LOCK_FILE_NAME = "saves_migration.lock"
        private const val LEGACY_BACKUP_SUFFIX = ".android-data-backup"

        private val migratedDirectories: MutableSet<String> = ConcurrentHashMap.newKeySet()
    }
}
