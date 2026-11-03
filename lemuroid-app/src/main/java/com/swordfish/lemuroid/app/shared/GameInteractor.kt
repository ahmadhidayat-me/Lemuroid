package com.swordfish.lemuroid.app.shared

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import com.swordfish.lemuroid.R
import com.swordfish.lemuroid.app.mobile.feature.shortcuts.ShortcutsGenerator
import com.swordfish.lemuroid.app.shared.game.GameLauncher
import com.swordfish.lemuroid.app.shared.main.BusyActivity
import com.swordfish.lemuroid.common.displayToast
import com.swordfish.lemuroid.lib.library.covers.CustomCoverManager
import com.swordfish.lemuroid.lib.library.db.RetrogradeDatabase
import com.swordfish.lemuroid.lib.library.db.entity.Game
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class GameInteractor(
    private val activity: BusyActivity,
    private val retrogradeDb: RetrogradeDatabase,
    private val useLeanback: Boolean,
    private val shortcutsGenerator: ShortcutsGenerator,
    private val gameLauncher: GameLauncher,
    private val customCoverManager: CustomCoverManager,
) {
    // Failures (e.g. an unreadable image picked from a cloud provider) must not crash the app.
    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable, "Game action failed")
            activity.activity().runOnUiThread {
                activity.activity().displayToast(R.string.game_interactor_action_failed)
            }
        }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)

    fun onGamePlay(game: Game) {
        if (!ensureNotBusy()) {
            return
        }
        if (!ensureNotificationsPermissionAvailable()) {
            return
        }
        gameLauncher.launchGameAsync(activity.activity(), game, true, useLeanback)
    }

    fun onGameRestart(game: Game) {
        if (!ensureNotBusy()) {
            return
        }
        if (!ensureNotificationsPermissionAvailable()) {
            return
        }
        gameLauncher.launchGameAsync(activity.activity(), game, false, useLeanback)
    }

    fun onFavoriteToggle(
        game: Game,
        isFavorite: Boolean,
    ) {
        scope.launch {
            retrogradeDb.gameDao().updateFavorite(game.id, isFavorite)
        }
    }

    fun onCreateShortcut(game: Game) {
        scope.launch {
            shortcutsGenerator.pinShortcutForGame(game)
        }
    }

    fun supportShortcuts(): Boolean {
        return shortcutsGenerator.supportShortcuts()
    }

    fun onSetCustomName(
        game: Game,
        customName: String,
    ) {
        scope.launch {
            retrogradeDb.gameDao().updateCustomName(game.id, customName)
        }
    }

    fun onClearCustomName(game: Game) {
        scope.launch {
            retrogradeDb.gameDao().updateCustomName(game.id, null)
        }
    }

    /**
     * Copies the image at [sourceUri] into app-private storage, then stores
     * the local file URI in the database.  This ensures the cover survives
     * app restarts regardless of where the user picked the file from.
     */
    fun onSetCustomCoverUri(
        game: Game,
        sourceUri: String,
    ) {
        scope.launch {
            val localUri = customCoverManager.importCover(game.id, Uri.parse(sourceUri))
            retrogradeDb.gameDao().updateCustomCoverUri(game.id, localUri)
        }
    }

    /**
     * Removes the custom cover: deletes the file from private storage and
     * clears the database field.
     */
    fun onClearCustomCoverUri(game: Game) {
        scope.launch {
            retrogradeDb.gameDao().updateCustomCoverUri(game.id, null)
            customCoverManager.deleteCover(game.id)
        }
    }

    private fun ensureNotificationsPermissionAvailable(): Boolean {
        if (useLeanback || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        val permissionResult =
            ContextCompat.checkSelfPermission(
                activity.activity(),
                Manifest.permission.POST_NOTIFICATIONS,
            )

        if (permissionResult == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        activity.activity().displayToast(R.string.game_interactor_notification_permission_required)
        return false
    }

    private fun ensureNotBusy(): Boolean {
        if (activity.isBusy()) {
            activity.activity().displayToast(R.string.game_interactory_busy)
            return false
        }
        return true
    }
}
