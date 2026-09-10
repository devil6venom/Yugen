package eu.kanade.tachiyomi.ui.manga

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.manga.RecommendationSeeAllScreen
import eu.kanade.presentation.util.Screen
import tachiyomi.domain.manga.model.Recommendation
import tachiyomi.domain.manga.model.RecommendationCategory

class RecommendationSeeAllScreen(
    private val title: String,
    private val recommendations: Map<RecommendationCategory, List<Recommendation>>,
    private val onRecommendationClick: (Recommendation) -> Unit,
) : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        RecommendationSeeAllScreen(
            title = title,
            recommendations = recommendations,
            onRecommendationClick = onRecommendationClick,
            navigateUp = navigator::pop,
        )
    }
}
