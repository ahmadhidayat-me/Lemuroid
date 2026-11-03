package com.swordfish.lemuroid.app.mobile.shared.compose.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/** Themes selectable for the main menu. Keys are persisted in preferences, so they must not change. */
enum class LemuroidAppTheme(val key: String, val isDark: Boolean) {
    DEFAULT("default", true),
    RETRO("retro", true),
    LIGHT("light", false),
    ;

    companion object {
        fun fromKey(key: String?): LemuroidAppTheme = entries.firstOrNull { it.key == key } ?: DEFAULT
    }
}

// Warm amber on near black, reminiscent of arcade cabinets and amber CRT monitors.
internal val RetroColorScheme: ColorScheme =
    darkColorScheme(
        primary = Color(0xFFFFB86B),
        onPrimary = Color(0xFF4A2800),
        primaryContainer = Color(0xFF6A3C00),
        onPrimaryContainer = Color(0xFFFFDCBE),
        inversePrimary = Color(0xFF8B5000),
        secondary = Color(0xFF7FD8C7),
        onSecondary = Color(0xFF00382F),
        secondaryContainer = Color(0xFF005045),
        onSecondaryContainer = Color(0xFF9CF2E2),
        tertiary = Color(0xFFFF8A80),
        onTertiary = Color(0xFF5F1412),
        tertiaryContainer = Color(0xFF7E2B26),
        onTertiaryContainer = Color(0xFFFFDAD6),
        background = Color(0xFF16120E),
        onBackground = Color(0xFFEFE0D2),
        surface = Color(0xFF16120E),
        onSurface = Color(0xFFEFE0D2),
        surfaceVariant = Color(0xFF51443A),
        onSurfaceVariant = Color(0xFFD5C3B5),
        surfaceTint = Color(0xFFFFB86B),
        inverseSurface = Color(0xFFEFE0D2),
        inverseOnSurface = Color(0xFF352F2A),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        outline = Color(0xFF9E8E81),
        outlineVariant = Color(0xFF51443A),
        scrim = Color(0xFF000000),
        surfaceBright = Color(0xFF3E3731),
        surfaceDim = Color(0xFF16120E),
        surfaceContainerLowest = Color(0xFF110D09),
        surfaceContainerLow = Color(0xFF1F1A15),
        surfaceContainer = Color(0xFF231E19),
        surfaceContainerHigh = Color(0xFF2E2823),
        surfaceContainerHighest = Color(0xFF39332D),
    )

// A light variant of the Lemuroid green. Containers are explicit so navigation bars and cards stay neutral.
internal val DaylightColorScheme: ColorScheme =
    lightColorScheme(
        primary = Color(0xFF006E28),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF6DFE8B),
        onPrimaryContainer = Color(0xFF002107),
        inversePrimary = Color(0xFF3EE366),
        secondary = Color(0xFF516350),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFD4E8D0),
        onSecondaryContainer = Color(0xFF0F1F10),
        tertiary = Color(0xFF39656C),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFBDEAF2),
        onTertiaryContainer = Color(0xFF001F23),
        background = Color(0xFFF8FAF3),
        onBackground = Color(0xFF1A1C19),
        surface = Color(0xFFF8FAF3),
        onSurface = Color(0xFF1A1C19),
        surfaceVariant = Color(0xFFDEE5D9),
        onSurfaceVariant = Color(0xFF424940),
        surfaceTint = Color(0xFF006E28),
        inverseSurface = Color(0xFF2F312D),
        inverseOnSurface = Color(0xFFF0F1EB),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        outline = Color(0xFF72796F),
        outlineVariant = Color(0xFFC2C9BD),
        scrim = Color(0xFF000000),
        surfaceBright = Color(0xFFF8FAF3),
        surfaceDim = Color(0xFFD9DBD5),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF2F4ED),
        surfaceContainer = Color(0xFFECEFE7),
        surfaceContainerHigh = Color(0xFFE7E9E2),
        surfaceContainerHighest = Color(0xFFE1E3DC),
    )
