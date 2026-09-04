package mihon.domain.migration.usecases

import dev.zacsweers.metro.Inject
import eu.kanade.domain.manga.interactor.UpdateManga
import eu.kanade.domain.manga.model.hasCustomCover
import eu.kanade.domain.source.service.SourcePreferences
import eu.kanade.tachiyomi.data.cache.CoverCache
import eu.kanade.tachiyomi.data.download.DownloadManager
import kotlinx.coroutines.CancellationException
import mihon.domain.migration.models.MigrationFlag
import mihon.domain.source.interactor.UpdateMangaFromRemote
import tachiyomi.domain.category.interactor.GetCategories
import tachiyomi.domain.category.interactor.SetMangaCategories
import tachiyomi.domain.chapter.interactor.GetChaptersByMangaId
import tachiyomi.domain.chapter.interactor.UpdateChapter
import tachiyomi.domain.chapter.model.ChapterUpdate
import tachiyomi.domain.chapter.model.toChapterUpdate
import tachiyomi.domain.history.interactor.GetHistory
import tachiyomi.domain.history.interactor.UpsertHistory
import tachiyomi.domain.history.model.HistoryUpdate
import tachiyomi.domain.history.model.toHistoryUpdate
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.manga.model.MangaUpdate
import kotlin.time.Clock

@Inject
class MigrateMangaUseCase(
    private val sourcePreferences: SourcePreferences,
    private val downloadManager: DownloadManager,
    private val updateManga: UpdateManga,
    private val getChaptersByMangaId: GetChaptersByMangaId,
    private val getHistoryByMangaId: GetHistory,
    private val updateChapter: UpdateChapter,
    private val updateHistory: UpsertHistory,
    private val getCategories: GetCategories,
    private val setMangaCategories: SetMangaCategories,
    private val coverCache: CoverCache,
    private val updateMangaFromRemote: UpdateMangaFromRemote,
) {
    suspend operator fun invoke(current: Manga, target: Manga, replace: Boolean) {
        val flags = sourcePreferences.migrationFlags.get()

        try {
            updateMangaFromRemote(target, fetchChapters = true).getOrThrow()

            // Update chapters read state, history, bookmark and dateFetch
            if (MigrationFlag.CHAPTER in flags) {
                val chapterUpdates = mutableListOf<ChapterUpdate>()
                val targetChapters = getChaptersByMangaId.await(target.id)
                val currentChapters = getChaptersByMangaId.await(current.id)
                val historyUpdates = mutableListOf<HistoryUpdate>()
                val targetHistory = getHistoryByMangaId.await(target.id)
                val currentHistory = getHistoryByMangaId.await(current.id)

                val maxChapterRead = currentChapters
                    .filter { it.read }
                    .maxOfOrNull { it.chapterNumber }

                targetChapters.forEach { mangaChapter ->
                    var updatedChapter = mangaChapter

                    if (updatedChapter.isRecognizedNumber) {
                        val prevChapter = currentChapters
                            .find { it.isRecognizedNumber && it.chapterNumber == updatedChapter.chapterNumber }

                        if (prevChapter != null) {
                            updatedChapter = updatedChapter.copy(
                                dateFetch = prevChapter.dateFetch,
                                bookmark = prevChapter.bookmark,
                            )

                            var updatedHistory = currentHistory.find { it.chapterId == prevChapter.id }
                            val chapterHasHistory =
                                mangaChapter.read && targetHistory.find { it.chapterId == mangaChapter.id } != null

                            if (!chapterHasHistory && updatedHistory?.readAt != null) {
                                updatedHistory = updatedHistory.copy(chapterId = updatedChapter.id)
                                historyUpdates.add(updatedHistory.toHistoryUpdate())
                            }
                        }

                        if (maxChapterRead != null && updatedChapter.chapterNumber <= maxChapterRead) {
                            updatedChapter = updatedChapter.copy(read = true)
                        }
                    }
                    chapterUpdates.add(updatedChapter.toChapterUpdate())
                }

                updateChapter.awaitAll(chapterUpdates)
                updateHistory.awaitAll(historyUpdates)
            }

            // Update categories
            if (MigrationFlag.CATEGORY in flags) {
                val categoryIds = getCategories.await(current.id).map { it.id }
                setMangaCategories.await(target.id, categoryIds)
            }

            // Delete downloaded
            if (MigrationFlag.REMOVE_DOWNLOAD in flags) {
                downloadManager.deleteManga(current)
            }

            // Update custom cover (recheck if custom cover exists)
            if (MigrationFlag.CUSTOM_COVER in flags && current.hasCustomCover()) {
                coverCache.setCustomCoverToCache(target, coverCache.getCustomCoverFile(current.id).inputStream())
            }

            val currentMangaUpdate = MangaUpdate(
                id = current.id,
                favorite = false,
                dateAdded = 0,
            )
                .takeIf { replace }
            val targetMangaUpdate = MangaUpdate(
                id = target.id,
                favorite = true,
                chapterFlags = current.chapterFlags,
                viewerFlags = current.viewerFlags,
                dateAdded = if (replace) current.dateAdded else Clock.System.now().toEpochMilliseconds(),
                notes = if (MigrationFlag.NOTES in flags) current.notes else null,
            )

            updateManga.awaitAll(listOfNotNull(currentMangaUpdate, targetMangaUpdate))
        } catch (e: Throwable) {
            if (e is CancellationException) {
                throw e
            }
        }
    }
}
