package com.swordfish.lemuroid.lib.library.covers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import com.swordfish.lemuroid.lib.library.db.entity.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Manages custom cover artwork files stored in the app's private external storage.
 *
 * Images are decoded, downscaled and re-encoded into [getCoversDirectory] with a filename based on the
 * game's database id so that:
 *   - The URI never expires (it's a local file we own).
 *   - Cleanup is trivial: delete the files when the game or the custom artwork is removed.
 *   - Each import gets a new name, so image caches never show a stale cover.
 */
class CustomCoverManager(private val appContext: Context) {
    /** Returns (and creates if necessary) the directory for custom covers. */
    fun getCoversDirectory(): File =
        File(appContext.getExternalFilesDir(null), "custom-covers").apply {
            mkdirs()
        }

    /**
     * Imports the image at [sourceUri] and returns the local file:// URI string that should be persisted in
     * [Game.customCoverUri]. Throws if the source is not a readable image. The previous cover is only removed
     * once the new one has been fully written.
     */
    suspend fun importCover(
        gameId: Int,
        sourceUri: Uri,
    ): String =
        withContext(Dispatchers.IO) {
            val bitmap =
                decodeScaledBitmap(sourceUri)
                    ?: throw IllegalArgumentException("Cannot decode image $sourceUri")

            val directory = getCoversDirectory()
            val destFile = File(directory, "cover_${gameId}_${System.currentTimeMillis()}.$COVER_EXTENSION")
            val tmpFile = File(directory, "$TMP_PREFIX${destFile.name}")

            try {
                tmpFile.outputStream().use { bitmap.compress(coverFormat(), COVER_QUALITY, it) }
                if (!tmpFile.renameTo(destFile)) error("Cannot rename $tmpFile")
            } catch (e: Throwable) {
                tmpFile.delete()
                throw e
            } finally {
                bitmap.recycle()
            }

            deleteCoversForGameId(gameId, keep = destFile)
            Uri.fromFile(destFile).toString()
        }

    /** Deletes the locally-stored cover for [gameId], regardless of extension. */
    fun deleteCover(gameId: Int) {
        deleteCoversForGameId(gameId)
    }

    /** Batch-deletes covers for a list of games. Called during library cleanup. */
    fun deleteCoversForGames(games: List<Game>) {
        games.forEach { game ->
            if (game.customCoverUri != null) {
                deleteCoversForGameId(game.id)
            }
        }
    }

    private fun decodeScaledBitmap(sourceUri: Uri): Bitmap? {
        val resolver = appContext.contentResolver

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(sourceUri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        // Subsample while decoding so that huge pictures never get fully loaded in memory.
        var sampleSize = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sampleSize * 2) >= MAX_COVER_SIZE) {
            sampleSize *= 2
        }

        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val decoded = resolver.openInputStream(sourceUri)?.use { BitmapFactory.decodeStream(it, null, options) }
        return decoded?.let { downscale(it) }
    }

    private fun downscale(bitmap: Bitmap): Bitmap {
        val largestSide = max(bitmap.width, bitmap.height)
        if (largestSide <= MAX_COVER_SIZE) return bitmap

        val ratio = MAX_COVER_SIZE.toFloat() / largestSide
        val scaled =
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * ratio).roundToInt(),
                (bitmap.height * ratio).roundToInt(),
                true,
            )
        if (scaled != bitmap) bitmap.recycle()
        return scaled
    }

    @Suppress("DEPRECATION")
    private fun coverFormat(): Bitmap.CompressFormat {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            Bitmap.CompressFormat.WEBP
        }
    }

    /** Deletes the covers of [gameId] in both the legacy (cover_<id>.ext) and current (cover_<id>_<time>.ext) format. */
    private fun deleteCoversForGameId(
        gameId: Int,
        keep: File? = null,
    ) {
        val legacyPrefix = "cover_$gameId."
        val prefix = "cover_${gameId}_"
        getCoversDirectory().listFiles()
            ?.filter { it.name.startsWith(legacyPrefix) || it.name.startsWith(prefix) }
            ?.filter { it != keep }
            ?.forEach { file ->
                val deleted = file.delete()
                Timber.d("Deleted custom cover ${file.name}: $deleted")
            }
    }

    companion object {
        private const val MAX_COVER_SIZE = 1024
        private const val COVER_QUALITY = 90
        private const val COVER_EXTENSION = "webp"
        private const val TMP_PREFIX = ".tmp_"
    }
}
