package tachiyomi.domain.manga.model

import androidx.compose.runtime.Immutable

@Immutable
data class Recommendation(
    val id: Long,
    val mangaId: Long?,
    val title: String,
    val thumbnailUrl: String?,
    val sourceId: Long,
    val url: String,
    val categoryId: String,
)

data class RecommendationCategory(
    val id: String,
    val title: String,
)
