package eu.kanade.presentation.theme.colorscheme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal object GithubColorScheme : BaseColorScheme() {

    override val darkScheme = darkColorScheme(
        primary = Color(0xFF2F81F7), // GitHub Blue (Buttons, active icons)
        onPrimary = Color(0xFFFFFFFF), // White text on buttons
        primaryContainer = Color(0xFF1F6FEB), // Darker blue for filled button containers
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = Color(0xFF58A6FF), // Unread badge / Accent link text
        onSecondary = Color(0xFF0D1117), // Dark text on badge
        secondaryContainer = Color(0xFF21262D), // Navigation bar selector pill / background fill
        onSecondaryContainer = Color(0xFF58A6FF), // Selected icon color
        tertiary = Color(0xFF238636), // GitHub Green (Success, PR open, Merged, Downloaded)
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF2EA44F),
        onTertiaryContainer = Color(0xFFFFFFFF),
        error = Color(0xFFF85149), // GitHub Red (Closed issues, deleted code)
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFDA3633),
        onErrorContainer = Color(0xFFF85149),
        background = Color(0xFF0D1117), // Deep canvas background
        onBackground = Color(0xFFC9D1D9), // Primary text color
        surface = Color(0xFF0D1117),
        onSurface = Color(0xFFC9D1D9),
        surfaceVariant = Color(0xFF161B22), // Top nav bar / header background
        onSurfaceVariant = Color(0xFF8B949E), // Muted secondary text / unselected items
        outline = Color(0xFF30363D), // GitHub's standard border outline color
        outlineVariant = Color(0xFF21262D), // Softer outlines for dividers
        scrim = Color(0xFF010409), // Popover shadows and black overlay
        inverseSurface = Color(0xFFF0F6FC), // Snackbar contrast card
        inverseOnSurface = Color(0xFF0D1117), // Snackbar contrast text
        inversePrimary = Color(0xFF0969DA), // Light mode link/primary accent
        surfaceDim = Color(0xFF0D1117),
        surfaceBright = Color(0xFF30363D),
        surfaceContainerLowest = Color(0xFF010409),
        surfaceContainerLow = Color(0xFF161B22), // Repository cards / lists
        surfaceContainer = Color(0xFF161B22), // File explorer tree backgrounds
        surfaceContainerHigh = Color(0xFF21262D), // Filter menu / dropdown dropdown lists
        surfaceContainerHighest = Color(0xFF30363D), // Untoggled button bg / highlighted code block lines
    )

    override val lightScheme = lightColorScheme(
        primary = Color(0xFF0969DA), // GitHub Light Blue (Buttons, active icons)
        onPrimary = Color(0xFFFFFFFF), // White text on buttons
        primaryContainer = Color(0xFF0550AE), // Darker blue for filled button containers
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = Color(0xFF24292F), // Muted dark text / Unread badge
        onSecondary = Color(0xFFFFFFFF), // Unread badge text
        secondaryContainer = Color(0xFFEFF1F4), // Navigation bar selector pill / gray capsule
        onSecondaryContainer = Color(0xFF0969DA), // Selected icon color
        tertiary = Color(0xFF1A7F37), // GitHub Light Green (Success, PR open, Merged)
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF2DA44E),
        onTertiaryContainer = Color(0xFFFFFFFF),
        error = Color(0xFFCF222E), // GitHub Light Red (Alerts, closed states)
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFA40E26),
        onErrorContainer = Color(0xFFCF222E),
        background = Color(0xFFFFFFFF), // Clean white background
        onBackground = Color(0xFF24292F), // Primary dark gray text
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF24292F),
        surfaceVariant = Color(0xFFF6F8FA), // Top nav bar / gray card sections
        onSurfaceVariant = Color(0xFF57606A), // Secondary muted text / unselected items
        outline = Color(0xFFD0D7DE), // GitHub's standard light border outline color
        outlineVariant = Color(0xFFEBEF24), // Softer outlines for faint dividers
        scrim = Color(0xFF24292F), // Black alpha overlays
        inverseSurface = Color(0xFF24292F), // Snackbar contrast card
        inverseOnSurface = Color(0xFFF6F8FA), // Snackbar contrast text
        inversePrimary = Color(0xFF58A6FF), // Dark mode primary accent
        surfaceDim = Color(0xFFF6F8FA),
        surfaceBright = Color(0xFFFFFFFF),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF6F8FA), // Repository cards / lists
        surfaceContainer = Color(0xFFF6F8FA), // File explorer tree backgrounds
        surfaceContainerHigh = Color(0xFFEFF1F4), // Filter menu / dropdown dropdown lists
        surfaceContainerHighest = Color(0xFFD0D7DE), // Untoggled button bg / highlighted code block lines
    )
}
