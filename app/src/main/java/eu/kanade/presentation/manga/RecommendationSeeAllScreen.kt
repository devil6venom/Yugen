package eu.kanade.presentation.manga

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.manga.components.MangaCover
import tachiyomi.domain.manga.model.Recommendation
import tachiyomi.domain.manga.model.RecommendationCategory
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.plus
import tachiyomi.presentation.core.util.secondaryItemAlpha

@Composable
fun RecommendationSeeAllScreen(
    title: String,
    recommendations: Map<RecommendationCategory, List<Recommendation>>,
    onRecommendationClick: (Recommendation) -> Unit,
    navigateUp: () -> Unit,
) {
    Scaffold(
        topBar = { scrollBehavior ->
            AppBar(
                title = title,
                navigateUp = navigateUp,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        val similarMangaTitle = stringResource(MR.strings.label_rec_similar)
        val groupedRecommendations = remember(recommendations) {
            val authorRecs = recommendations.filter { it.key.id == "author" }
            val discoveryRecs = recommendations.filter { it.key.id == "discovery" }
            val termRecs = recommendations.filter { it.key.id.startsWith("term_") }

            buildList {
                authorRecs.forEach { add(it.toPair()) }
                if (termRecs.isNotEmpty()) {
                    val mergedTerms = termRecs.values.flatten().distinctBy { it.url }
                    add(RecommendationCategory("merged_terms", similarMangaTitle) to mergedTerms)
                }
                discoveryRecs.forEach { add(it.toPair()) }
            }
        }

        LazyColumn(
            contentPadding = paddingValues + PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            groupedRecommendations.forEach { (category, recs) ->
                if (recs.isNotEmpty()) {
                    item {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 12.dp),
                        )
                    }
                    item {
                        RecommendationGrid(
                            recommendations = recs,
                            onRecommendationClick = onRecommendationClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationGrid(
    recommendations: List<Recommendation>,
    onRecommendationClick: (Recommendation) -> Unit,
) {
    val chunks = recommendations.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        chunks.forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { recommendation ->
                    Box(modifier = Modifier.weight(1f)) {
                        RecommendationSeeAllItem(
                            recommendation = recommendation,
                            onClick = { onRecommendationClick(recommendation) },
                        )
                    }
                }
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun RecommendationSeeAllItem(
    recommendation: Recommendation,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MangaCover.Book(
            data = recommendation.thumbnailUrl,
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
