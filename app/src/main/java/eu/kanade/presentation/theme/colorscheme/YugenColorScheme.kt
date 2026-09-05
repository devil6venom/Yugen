package eu.kanade.presentation.theme.colorscheme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Colors for Yugen theme
 * A yellowish mixed theme with balanced reader aesthetic.
 */
internal object YugenColorScheme : BaseColorScheme() {

    override val darkScheme = darkColorScheme(
        primary = Color(0xFFE6B422),
        onPrimary = Color(0xFF3D2D00),
        primaryContainer = Color(0xFF584200),
        onPrimaryContainer = Color(0xFFFFDF93),
        inversePrimary = Color(0xFFC69800),
        secondary = Color(0xFFB4C5C0),
        onSecondary = Color(0xFF1F322E),
        secondaryContainer = Color(0xFF364844),
        onSecondaryContainer = Color(0xFFD0E2DC),
        tertiary = Color(0xFFC6C6C6),
        onTertiary = Color(0xFF2F3131),
        tertiaryContainer = Color(0xFF464747),
        onTertiaryContainer = Color(0xFFE2E2E2),
        background = Color(0xFF1C1C16),
        onBackground = Color(0xFFE6E2D3),
        surface = Color(0xFF1C1C16),
        onSurface = Color(0xFFE6E2D3),
        surfaceVariant = Color(0xFF4D4734),
        onSurfaceVariant = Color(0xFFD0C6AE),
        surfaceTint = Color(0xFFE6B422),
        inverseSurface = Color(0xFFE6E2D3),
        inverseOnSurface = Color(0xFF31302C),
        outline = Color(0xFF99907A),
        surfaceContainerLowest = Color(0xFF12120D),
        surfaceContainerLow = Color(0xFF1E1E18),
        surfaceContainer = Color(0xFF22221C),
        surfaceContainerHigh = Color(0xFF2C2C26),
        surfaceContainerHighest = Color(0xFF373731),
    )

    override val lightScheme = lightColorScheme(
        primary = Color(0xFFC69800),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFF0C2),
        onPrimaryContainer = Color(0xFF221B00),
        inversePrimary = Color(0xFFF1C40F),
        secondary = Color(0xFF40514E),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFC2D1CF),
        onSecondaryContainer = Color(0xFF00201C),
        tertiary = Color(0xFF5D5F5E),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFE2E2E2),
        onTertiaryContainer = Color(0xFF1A1C1C),
        background = Color(0xFFFFFBEC),
        onBackground = Color(0xFF1C1C16),
        surface = Color(0xFFFFFBEC),
        onSurface = Color(0xFF1C1C16),
        surfaceVariant = Color(0xFFEDE7D1),
        onSurfaceVariant = Color(0xFF4D4734),
        surfaceTint = Color(0xFFC69800),
        inverseSurface = Color(0xFF31302C),
        inverseOnSurface = Color(0xFFF5F0E7),
        outline = Color(0xFF7E7762),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFAF4E2),
        surfaceContainer = Color(0xFFF4EEDC),
        surfaceContainerHigh = Color(0xFFEEE9D6),
        surfaceContainerHighest = Color(0xFFE9E3D1),
    )
}
