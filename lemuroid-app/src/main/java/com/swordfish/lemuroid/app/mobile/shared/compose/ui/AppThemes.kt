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

// Dark grey with neon red and neon blue accents, like a modern hybrid console UI.
internal val SwitchColorScheme: ColorScheme =
    darkColorScheme(
        primary = Color(0xFFFF5A65),
        onPrimary = Color(0xFF410008),
        primaryContainer = Color(0xFF92001F),
        onPrimaryContainer = Color(0xFFFFDADB),
        inversePrimary = Color(0xFFBF0031),
        secondary = Color(0xFF00C3E3),
        onSecondary = Color(0xFF00363F),
        secondaryContainer = Color(0xFF004F5C),
        onSecondaryContainer = Color(0xFF9EEFFF),
        tertiary = Color(0xFFE6E95A),
        onTertiary = Color(0xFF323300),
        tertiaryContainer = Color(0xFF494B00),
        onTertiaryContainer = Color(0xFFF0F378),
        background = Color(0xFF1E1E1E),
        onBackground = Color(0xFFEDEDED),
        surface = Color(0xFF1E1E1E),
        onSurface = Color(0xFFEDEDED),
        surfaceVariant = Color(0xFF4A4A4A),
        onSurfaceVariant = Color(0xFFCFCFCF),
        surfaceTint = Color(0xFFFF5A65),
        inverseSurface = Color(0xFFEDEDED),
        inverseOnSurface = Color(0xFF303030),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        outline = Color(0xFF969696),
        outlineVariant = Color(0xFF4A4A4A),
        scrim = Color(0xFF000000),
        surfaceBright = Color(0xFF444444),
        surfaceDim = Color(0xFF1E1E1E),
        surfaceContainerLowest = Color(0xFF171717),
        surfaceContainerLow = Color(0xFF262626),
        surfaceContainer = Color(0xFF2B2B2B),
        surfaceContainerHigh = Color(0xFF353535),
        surfaceContainerHighest = Color(0xFF404040),
    )

// The four shades of green of early monochrome handheld screens.
private val EightBitDarkest = Color(0xFF0F380F)
private val EightBitDark = Color(0xFF306230)
private val EightBitLight = Color(0xFF8BAC0F)
private val EightBitLightest = Color(0xFF9BBC0F)

internal val EightBitColorScheme: ColorScheme =
    lightColorScheme(
        primary = EightBitDarkest,
        onPrimary = EightBitLightest,
        primaryContainer = EightBitDark,
        onPrimaryContainer = EightBitLightest,
        inversePrimary = EightBitLight,
        secondary = EightBitDark,
        onSecondary = EightBitLightest,
        secondaryContainer = EightBitLight,
        onSecondaryContainer = EightBitDarkest,
        tertiary = EightBitDark,
        onTertiary = EightBitLightest,
        tertiaryContainer = EightBitLight,
        onTertiaryContainer = EightBitDarkest,
        background = EightBitLightest,
        onBackground = EightBitDarkest,
        surface = EightBitLightest,
        onSurface = EightBitDarkest,
        surfaceVariant = EightBitLight,
        onSurfaceVariant = EightBitDarkest,
        surfaceTint = EightBitDark,
        inverseSurface = EightBitDarkest,
        inverseOnSurface = EightBitLightest,
        error = Color(0xFF7A1F1F),
        onError = EightBitLightest,
        errorContainer = Color(0xFFD9B3A6),
        onErrorContainer = Color(0xFF3B0A0A),
        outline = EightBitDark,
        outlineVariant = EightBitLight,
        scrim = Color(0xFF000000),
        surfaceBright = EightBitLightest,
        surfaceDim = EightBitLight,
        surfaceContainerLowest = EightBitLightest,
        surfaceContainerLow = Color(0xFF96B70F),
        surfaceContainer = Color(0xFF91B20F),
        surfaceContainerHigh = EightBitLight,
        surfaceContainerHighest = Color(0xFF7E9E0E),
    )
