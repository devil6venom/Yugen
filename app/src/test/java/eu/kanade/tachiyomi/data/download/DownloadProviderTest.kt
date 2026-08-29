package eu.kanade.tachiyomi.data.download

import android.content.Context
import com.hippo.unifile.UniFile
import eu.kanade.tachiyomi.source.Source
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import tachiyomi.core.common.preference.Preference
import tachiyomi.domain.library.service.LibraryPreferences
import tachiyomi.domain.storage.service.StorageManager

class DownloadProviderTest {

    @Test
    fun `find chapter dir falls back to URL hash when scanlator changes`() {
        val provider = createProvider()
        val source = mockk<Source>()
        val mangaDir = mockk<UniFile>()
        val existingChapter = mockk<UniFile>()
        val chapterUrl = "/chapter/1"
        val existingName = provider.getChapterDirName(
            chapterName = "Chapter 1",
            chapterScanlator = "Old Group",
            chapterUrl = chapterUrl,
        )

        every { provider.findMangaDir("Manga", source) } returns mangaDir
        every { mangaDir.findFile(any()) } returns null
        every { mangaDir.listFiles() } returns arrayOf(existingChapter)
        every { existingChapter.name } returns existingName
        every { existingChapter.isDirectory } returns true
        every { existingChapter.isFile } returns false

        val result = provider.findChapterDir(
            chapterName = "Chapter 1",
            chapterScanlator = null,
            chapterUrl = chapterUrl,
            mangaTitle = "Manga",
            source = source,
        )

        assertSame(existingChapter, result)
    }

    @Test
    fun `find chapter dir falls back to archived chapter by URL hash`() {
        val provider = createProvider()
        val source = mockk<Source>()
        val mangaDir = mockk<UniFile>()
        val existingChapter = mockk<UniFile>()
        val chapterUrl = "/chapter/1"
        val existingName = provider.getChapterDirName(
            chapterName = "Chapter 1",
            chapterScanlator = "Old Group",
            chapterUrl = chapterUrl,
        ) + ".cbz"

        every { provider.findMangaDir("Manga", source) } returns mangaDir
        every { mangaDir.findFile(any()) } returns null
        every { mangaDir.listFiles() } returns arrayOf(existingChapter)
        every { existingChapter.name } returns existingName
        every { existingChapter.isDirectory } returns false
        every { existingChapter.isFile } returns true

        val result = provider.findChapterDir(
            chapterName = "Chapter 1",
            chapterScanlator = null,
            chapterUrl = chapterUrl,
            mangaTitle = "Manga",
            source = source,
        )

        assertSame(existingChapter, result)
    }

    @Test
    fun `find chapter dir rejects ambiguous URL hash fallback`() {
        val provider = createProvider()
        val source = mockk<Source>()
        val mangaDir = mockk<UniFile>()
        val firstChapter = mockk<UniFile>()
        val secondChapter = mockk<UniFile>()
        val chapterUrl = "/chapter/1"

        every { provider.findMangaDir("Manga", source) } returns mangaDir
        every { mangaDir.findFile(any()) } returns null
        every { mangaDir.listFiles() } returns arrayOf(firstChapter, secondChapter)
        every { firstChapter.name } returns provider.getChapterDirName("Chapter 1", "Group A", chapterUrl)
        every { firstChapter.isDirectory } returns true
        every { firstChapter.isFile } returns false
        every { secondChapter.name } returns provider.getChapterDirName("Chapter 1", "Group B", chapterUrl)
        every { secondChapter.isDirectory } returns true
        every { secondChapter.isFile } returns false

        val result = provider.findChapterDir(
            chapterName = "Chapter 1",
            chapterScanlator = null,
            chapterUrl = chapterUrl,
            mangaTitle = "Manga",
            source = source,
        )

        assertNull(result)
    }

    private fun createProvider(): DownloadProvider {
        val libraryPreferences = mockk<LibraryPreferences>()
        val filenamePreference = mockk<Preference<Boolean>>()
        every { libraryPreferences.disallowNonAsciiFilenames } returns filenamePreference
        every { filenamePreference.get() } returns false

        return spyk(
            DownloadProvider(
                context = mockk<Context>(),
                storageManager = mockk<StorageManager>(),
                libraryPreferences = libraryPreferences,
            ),
        )
    }
}
