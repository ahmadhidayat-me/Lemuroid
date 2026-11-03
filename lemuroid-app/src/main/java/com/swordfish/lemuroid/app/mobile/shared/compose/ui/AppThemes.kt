package com.swordfish.lemuroid.app.mobile.shared.compose.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/** Themes selectable for the main menu. Keys are persisted in preferences, so they must not change. */
enum class LemuroidAppTheme(val key: String, val isDark: Boolean) {
    DARK("dark", true),
    LIGHT("light", false),
    RETRO("retro", true),
    SWITCH("switch", true),
    EIGHT_BIT("8bit", false),
    ;

    companion object {
        // "default" was the dynamic color theme of the first version, which has been replaced by Dark.
        fun fromKey(key: String?): LemuroidAppTheme = entries.firstOrNull { it.key == key } ?: DARK
    }
}

/** Layouts selectable for the main menu. Keys are persisted in preferences, so they must not change. */
enum class LemuroidAppLayout(val key: String) {
    DEFAULT("default"),
    CONSOLE("console"),
    ;

    companion object {
        fun fromKey(key: String?): LemuroidAppLayout = entries.firstOrNull { it.key == key } ?: DEFAULT
    }
}

// Warm near-black with a single amber accent, like an amber CRT. Everything else stays in warm neutrals.
internal val RetroColorScheme: ColorScheme =
    darkColorScheme(
        primary = Color(0xFFF0B86E),
        onPrimary = Color(0xFF472A00),
        primaryContainer = Color(0xFF654016),
        onPrimaryContainer = Color(0xFFFFDDB5),
        inversePrimary = Color(0xFF83551F),
        secondary = Color(0xFFDCC2A3),
        onSecondary = Color(0xFF3D2D18),
        secondaryContainer = Color(0xFF4A3A28),
        onSecondaryContainer = Color(0xFFF9DEBC),
        tertiary = Color(0xFFD8C4A0),
        onTertiary = Color(0xFF3B2F14),
        tertiaryContainer = Color(0xFF52452A),
        onTertiaryContainer = Color(0xFFF5E0BB),
        background = Color(0xFF18130E),
        onBackground = Color(0xFFEDE0D4),
        surface = Color(0xFF18130E),
        onSurface = Color(0xFFEDE0D4),
        surfaceVariant = Color(0xFF51453A),
        onSurfaceVariant = Color(0xFFD5C4B5),
        surfaceTint = Color(0xFFF0B86E),
        inverseSurface = Color(0xFFEDE0D4),
        inverseOnSurface = Color(0xFF362F28),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        outline = Color(0xFF9D8E81),
        outlineVariant = Color(0xFF51453A),
        scrim = Color(0xFF000000),
        surfaceBright = Color(0xFF3F3833),
        surfaceDim = Color(0xFF18130E),
        surfaceContainerLowest = Color(0xFF120E0A),
        surfaceContainerLow = Color(0xFF201B16),
        surfaceContainer = Color(0xFF251F1A),
        surfaceContainerHigh = Color(0xFF2F2924),
        surfaceContainerHighest = Color(0xFF3A342E),
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

// Neutral greys with a white accent, identical on every device.
internal val NeutralDarkColorScheme: ColorScheme =
    darkColorScheme(
        primary = Color(0xFFE6E6E6),
        onPrimary = Color(0xFF1C1C1C),
        primaryContainer = Color(0xFF3D3D3D),
        onPrimaryContainer = Color(0xFFF2F2F2),
        inversePrimary = Color(0xFF5E5E5E),
        secondary = Color(0xFFC4C4C4),
        onSecondary = Color(0xFF2A2A2A),
        secondaryContainer = Color(0xFF424242),
        onSecondaryContainer = Color(0xFFE6E6E6),
        tertiary = Color(0xFFB0B0B0),
        onTertiary = Color(0xFF262626),
        tertiaryContainer = Color(0xFF3A3A3A),
        onTertiaryContainer = Color(0xFFE0E0E0),
        background = Color(0xFF121212),
        onBackground = Color(0xFFE6E6E6),
        surface = Color(0xFF121212),
        onSurface = Color(0xFFE6E6E6),
        surfaceVariant = Color(0xFF444444),
        onSurfaceVariant = Color(0xFFC7C7C7),
        surfaceTint = Color(0xFFE6E6E6),
        inverseSurface = Color(0xFFE6E6E6),
        inverseOnSurface = Color(0xFF2E2E2E),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        outline = Color(0xFF8F8F8F),
        outlineVariant = Color(0xFF444444),
        scrim = Color(0xFF000000),
        surfaceBright = Color(0xFF383838),
        surfaceDim = Color(0xFF121212),
        surfaceContainerLowest = Color(0xFF0C0C0C),
        surfaceContainerLow = Color(0xFF1A1A1A),
        surfaceContainer = Color(0xFF1E1E1E),
        surfaceContainerHigh = Color(0xFF282828),
        surfaceContainerHighest = Color(0xFF333333),
    )

// Neutral dark grey with a single neon red accent, like a modern hybrid console UI.
internal val SwitchColorScheme: ColorScheme =
    darkColorScheme(
        primary = Color(0xFFFF5A65),
        onPrimary = Color(0xFF3B0006),
        primaryContainer = Color(0xFF5C1A1F),
        onPrimaryContainer = Color(0xFFFFDADB),
        inversePrimary = Color(0xFFBF0031),
        secondary = Color(0xFFC7C6CC),
        onSecondary = Color(0xFF2F3036),
        secondaryContainer = Color(0xFF3E3E44),
        onSecondaryContainer = Color(0xFFE3E2E8),
        tertiary = Color(0xFFC7C6CC),
        onTertiary = Color(0xFF2F3036),
        tertiaryContainer = Color(0xFF3E3E44),
        onTertiaryContainer = Color(0xFFE3E2E8),
        background = Color(0xFF1C1C1E),
        onBackground = Color(0xFFE6E5EA),
        surface = Color(0xFF1C1C1E),
        onSurface = Color(0xFFE6E5EA),
        surfaceVariant = Color(0xFF46464C),
        onSurfaceVariant = Color(0xFFC7C6CC),
        surfaceTint = Color(0xFFFF5A65),
        inverseSurface = Color(0xFFE6E5EA),
        inverseOnSurface = Color(0xFF303033),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        outline = Color(0xFF919096),
        outlineVariant = Color(0xFF46464C),
        scrim = Color(0xFF000000),
        surfaceBright = Color(0xFF424245),
        surfaceDim = Color(0xFF1C1C1E),
        surfaceContainerLowest = Color(0xFF141416),
        surfaceContainerLow = Color(0xFF232325),
        surfaceContainer = Color(0xFF28282B),
        surfaceContainerHigh = Color(0xFF323235),
        surfaceContainerHighest = Color(0xFF3D3D40),
    )

// Pale screen green with the darkest handheld greens for text and accents, so hierarchy and contrast stay clear.
internal val EightBitColorScheme: ColorScheme =
    lightColorScheme(
        primary = Color(0xFF306230),
        onPrimary = Color(0xFFE3EBC8),
        primaryContainer = Color(0xFFB9CD8A),
        onPrimaryContainer = Color(0xFF0F380F),
        inversePrimary = Color(0xFF9BBC0F),
        secondary = Color(0xFF4A6B3A),
        onSecondary = Color(0xFFE3EBC8),
        secondaryContainer = Color(0xFFC9D8A2),
        onSecondaryContainer = Color(0xFF0F380F),
        tertiary = Color(0xFF5A6E2E),
        onTertiary = Color(0xFFE3EBC8),
        tertiaryContainer = Color(0xFFD3DDA8),
        onTertiaryContainer = Color(0xFF0F380F),
        background = Color(0xFFE3EBC8),
        onBackground = Color(0xFF0F380F),
        surface = Color(0xFFE3EBC8),
        onSurface = Color(0xFF0F380F),
        surfaceVariant = Color(0xFFCDD8AE),
        onSurfaceVariant = Color(0xFF2F4A2A),
        surfaceTint = Color(0xFF306230),
        inverseSurface = Color(0xFF0F380F),
        inverseOnSurface = Color(0xFFE3EBC8),
        error = Color(0xFF8C1D18),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF2D3C9),
        onErrorContainer = Color(0xFF410E0B),
        outline = Color(0xFF5E7A4E),
        outlineVariant = Color(0xFFB6C594),
        scrim = Color(0xFF000000),
        surfaceBright = Color(0xFFEEF3D9),
        surfaceDim = Color(0xFFCCD7A8),
        surfaceContainerLowest = Color(0xFFF0F5DE),
        surfaceContainerLow = Color(0xFFDDE6BE),
        surfaceContainer = Color(0xFFD7E1B6),
        surfaceContainerHigh = Color(0xFFD0DBAD),
        surfaceContainerHighest = Color(0xFFC8D4A3),
    )
