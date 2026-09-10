package tachiyomi.domain.manga.interactor

import dev.zacsweers.metro.Inject
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.manga.repository.MangaRepository

@Inject
class GetMangaBySourceId(
    private val repository: MangaRepository,
) {
    suspend fun await(sourceId: Long): List<Manga> {
        return repository.getMangaBySourceId(sourceId)
    }
}
