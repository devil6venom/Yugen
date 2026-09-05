package eu.kanade.presentation.browse.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.tachiyomi.util.system.LocaleHelper
import tachiyomi.presentation.core.components.Pill

@Composable
fun LanguageBadge(
    lang: String?,
    modifier: Modifier = Modifier,
) {
    if (lang.isNullOrEmpty()) return

    if (lang == "all") {
        Text(
            text = "🌎",
            modifier = modifier.padding(start = 4.dp),
        )
    } else {
        val code = LocaleHelper.get3LetterLanguageCode(lang)
        Pill(
            text = code,
            modifier = modifier.padding(start = 4.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                fontSize = 10.sp,
            ),
        )
    }
}
