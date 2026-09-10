package eu.kanade.tachiyomi.data.recommendation

import dev.zacsweers.metro.Inject
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.model.FilterList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import tachiyomi.domain.manga.interactor.GetMangaBySourceId
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.manga.model.Recommendation
import tachiyomi.domain.manga.model.RecommendationCategory
import tachiyomi.domain.source.service.SourceManager
import java.util.Locale

@Inject
class RecommendationEngine(
    private val getMangaBySourceId: GetMangaBySourceId,
    private val sourceManager: SourceManager,
) {
    private val priorityKeywords = listOf(
        "Regressed", "Reborn", "Cultivate", "System", "Harmony Wonder", "Big Breast",
        "Wonder", "Mother", "Reincarnated", "Leveling", "Player", "Dungeon", "Isekai",
        "Martial", "Cultivation", "Return", "Vengeance", "Villainess", "Otome"
    )
    private val commonWords = setOf(
        "The", "And", "For", "With", "From", "That", "This", "Your", "Some", "What",
        "How", "Who", "Are", "Was", "Were", "Been", "Has", "Have", "Had", "Will",
        "Shall", "Can", "Could", "Should", "Would", "May", "Might", "Must", "Into",
        "Onto", "Upon", "Across", "Along", "Through", "Between", "Among", "During",
        "Before", "After", "Under", "Over", "Above", "Below", "Around"
    )

    suspend fun fetch(manga: Manga): Map<RecommendationCategory, List<Recommendation>> = coroutineScope {
        val source = sourceManager.get(manga.source) as? CatalogueSource ?: return@coroutineScope emptyMap()

        // 1. Aggressive Extraction of Search Terms
        val bracketContent = extractBrackets(manga.title)

        // Split title into significant words (excluding filler and symbols)
        val significantTitleWords = manga.title.split(Regex("[^a-zA-Z]"))
            .map { it.trim() }
            .filter { word ->
                word.length > 3 && !commonWords.any { it.equals(word, ignoreCase = true) }
            }

        val discoveredKeywords = extractCapitalizedKeywords(manga.title)
        val activePriorityKeywords = priorityKeywords.filter {
            manga.title.contains(it, ignoreCase = true) || manga.description?.contains(it, ignoreCase = true) == true
        }
        val randomTags = manga.genre?.shuffled()?.take(3) ?: emptyList()

        // Unique search pool: Brackets first, then priority keywords, then title words, then tags
        val searchPool = (bracketContent + activePriorityKeywords + significantTitleWords + discoveredKeywords + randomTags)
            .distinctBy { it.lowercase() }
            .filter { it.length > 2 }
            .shuffled()
            .take(8) // Increased search breadth

        val tasks = mutableListOf<Deferred<Pair<RecommendationCategory, List<Recommendation>>>>()

        // 2. Parallel Search for each term
        searchPool.forEach { term ->
            tasks.add(async {
                val category = RecommendationCategory("term_$term", "Similar to $term")
                category to fetchRemote(source, term, category.id, manga.url)
            })
        }

        // 3. Author Category
        manga.author?.takeIf { it.isNotBlank() }?.let { author ->
            tasks.add(async {
                val category = RecommendationCategory("author", "More by $author")
                category to fetchRemote(source, author, category.id, manga.url)
            })
        }

        // 4. Source Discovery (Latest)
        tasks.add(async {
            val category = RecommendationCategory("discovery", "Discovery from ${source.name}")
            val candidates = try {
                source.getLatestUpdates(1).mangas
                    .map { it.toDomainManga(source.id).toRecommendation(category.id) }
                    .filter { it.url != manga.url }
            } catch (e: Exception) { emptyList() }
            category to candidates
        })

        val results = awaitAll(*tasks.toTypedArray())
        val seenUrls = mutableSetOf<String>(manga.url)

        results.associate { (category, recommendations) ->
            val uniqueRecs = recommendations
                .filter { seenUrls.add(it.url) }
                .shuffled()
                .take(50)
            category to uniqueRecs
        }.filter { it.value.isNotEmpty() }
    }

    private suspend fun fetchRemote(
        source: CatalogueSource,
        query: String,
        categoryId: String,
        excludeUrl: String,
    ): List<Recommendation> {
        return try {
            // Search remote with the term
            source.getSearchManga(1, query, FilterList()).mangas
                .map { it.toDomainManga(source.id).toRecommendation(categoryId) }
                .filter { it.url != excludeUrl }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun extractBrackets(text: String): List<String> {
        val regex = Regex("[\\[({](.*?)[\\])}]")
        return regex.findAll(text).map { it.groupValues[1] }.toList()
    }

    private fun extractCapitalizedKeywords(text: String): List<String> {
        // Match words starting with Capital that are 4+ letters and not common filler
        val regex = Regex("\\b([A-Z][a-z]{3,})\\b")
        return regex.findAll(text)
            .map { it.groupValues[1] }
            .filter { word -> !commonWords.any { it.equals(word, ignoreCase = true) } }
            .toList()
    }

    private fun Manga.toRecommendation(categoryId: String): Recommendation {
        return Recommendation(
            id = id,
            mangaId = if (favorite) id else null,
            title = title,
            thumbnailUrl = thumbnailUrl,
            sourceId = source,
            url = url,
            categoryId = categoryId
        )
    }

    private fun eu.kanade.tachiyomi.source.model.SManga.toDomainManga(sourceId: Long): Manga {
        return Manga.create().copy(
            url = url,
            title = title,
            thumbnailUrl = thumbnail_url,
            author = author,
            source = sourceId
        )
    }
}
