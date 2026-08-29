package eu.kanade.presentation.theme.colorscheme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal object GothamColorScheme : BaseColorScheme() {

    override val darkScheme = darkColorScheme(
        primary = Color(0xFF008787),
        onPrimary = Color(0xFF000000),
        primaryContainer = Color(0xFF004F4F),
        onPrimaryContainer = Color(0xFF9EEBEB),
        secondary = Color(0xFF5F8787), // Unread badge
        onSecondary = Color(0xFF11151C), // Unread badge text
        secondaryContainer = Color(0xFF1C2D30), // Navigation bar selector pill & progress indicator (remaining)
        onSecondaryContainer = Color(0xFFD1E3E3), // Navigation bar selector icon
        tertiary = Color(0xFFDFA731), // Volume and brightness bars, Downloaded badge
        onTertiary = Color(0xFF000000), // Downloaded badge text
        tertiaryContainer = Color(0xFF573E00),
        onTertiaryContainer = Color(0xFFFFE0A1),
        error = Color(0xFFFF5555),
        onError = Color(0xFF11151C),
        errorContainer = Color(0xFF681016),
        onErrorContainer = Color(0xFFFFDAD9),
        background = Color(0xFF0C1014),
        onBackground = Color(0xFF9E9E9E),
        surface = Color(0xFF0C1014),
        onSurface = Color(0xFF9E9E9E),
        surfaceVariant = Color(0xFF11151C), // Navigation bar background (ThemePrefWidget)
        onSurfaceVariant = Color(0xFFBCBCBC), // Button (unselected)
        outline = Color(0xFF4E5A65),
        outlineVariant = Color(0xFF1C212A), // Outlines for buttons
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF9E9E9E), // Snackbar or whatever they called
        inverseOnSurface = Color(0xFF0C1014), // Snackbar text
        inversePrimary = Color(0xFF005F5F), // Snackbar accent
        surfaceDim = Color(0xFF0C1014),
        surfaceBright = Color(0xFF29323F),
        surfaceContainerLowest = Color(0xFF070A0D),
        surfaceContainerLow = Color(0xFF101419), // Repo cards
        surfaceContainer = Color(0xFF141920),
        surfaceContainerHigh = Color(0xFF1A2029), // Filter menu
        surfaceContainerHighest = Color(0xFF212833), // Untoggle button bg
    )

    override val lightScheme = lightColorScheme(
        primary = Color(0xFF005F5F),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF9EEBEB),
        onPrimaryContainer = Color(0xFF002020),
        secondary = Color(0xFF4E5A65), // Unread badge
        onSecondary = Color(0xFFFFFFFF), // Unread badge text
        secondaryContainer = Color(0xFFD6E2ED), // Navigation bar selector pill & progress indicator (remaining)
        onSecondaryContainer = Color(0xFF0F171E), // Navigation bar selector icon
        tertiary = Color(0xFFC28500), // Volume and brightness bars, Downloaded badge
        onTertiary = Color(0xFFFFFFFF), // Downloaded badge text
        tertiaryContainer = Color(0xFFFFE0A1),
        onTertiaryContainer = Color(0xFF241600),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        background = Color(0xFFEEEEEE),
        onBackground = Color(0xFF0C1014),
        surface = Color(0xFFEEEEEE),
        onSurface = Color(0xFF0C1014),
        surfaceVariant = Color(0xFFD8DEC9), // Navigation bar background (ThemePrefWidget)
        onSurfaceVariant = Color(0xFF4E5A65), // Button (unselected)
        outline = Color(0xFF8A959E),
        outlineVariant = Color(0xFFC5D1DC), // Outlines for buttons
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF0C1014), // Snackbar
        inverseOnSurface = Color(0xFFEEEEEE), // Snackbar text
        inversePrimary = Color(0xFF008787), // Snackbar accent
        surfaceDim = Color(0xFFE2E2E2),
        surfaceBright = Color(0xFFFAFAFA),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF5F5F5), // Repo cards
        surfaceContainer = Color(0xFFEAEAEA), // Navigation bar background
        surfaceContainerHigh = Color(0xFFDFDFDF), // Filter menu
        surfaceContainerHighest = Color(0xFFD4D4D4), // Untoggle bg
    )
}
