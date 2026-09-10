package eu.kanade.presentation.manga.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tachiyomi.domain.manga.model.Recommendation
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.secondaryItemAlpha

@Composable
fun RecommendationRow(
    title: String,
    recommendations: List<Recommendation>,
    onRecommendationClick: (Recommendation) -> Unit,
    onSeeAllClick: () -> Unit,
) {
    if (recommendations.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onSeeAllClick) {
                Text(text = stringResource(MR.strings.label_see_all))
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(recommendations) { recommendation ->
                RecommendationItem(
                    recommendation = recommendation,
                    onClick = { onRecommendationClick(recommendation) },
                )
            }
        }
    }
}

@Composable
private fun RecommendationItem(
    recommendation: Recommendation,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickableNoRipple(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MangaCover.Book(
            data = recommendation.thumbnailUrl,
            modifier = Modifier.size(height = 150.dp, width = 100.dp),
            contentDescription = recommendation.title,
        )
        Text(
            text = recommendation.title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 4.dp)
                .secondaryItemAlpha(),
        )
    }
}

@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick,
    )
}
