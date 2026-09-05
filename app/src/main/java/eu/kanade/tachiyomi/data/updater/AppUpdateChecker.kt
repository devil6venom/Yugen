package eu.kanade.tachiyomi.data.updater

import dev.zacsweers.metro.Inject
import eu.kanade.domain.base.BasePreferences
import eu.kanade.tachiyomi.BuildConfig
import eu.kanade.tachiyomi.util.system.isFossBuildType
import tachiyomi.core.common.util.lang.withIOContext
import tachiyomi.domain.release.interactor.GetApplicationRelease

@Inject
class AppUpdateChecker(
    private val getApplicationRelease: GetApplicationRelease,
    private val basePreferences: BasePreferences,
) {

    suspend fun checkForUpdate(forceCheck: Boolean = false): GetApplicationRelease.Result {
        if (!forceCheck && System.currentTimeMillis() - basePreferences.lastAppUpdateCheck.get() < 24 * 60 * 60 * 1000) {
            return GetApplicationRelease.Result.NoNewUpdate
        }

        // Disable app update checks for older Android versions that we're going to drop support for
        // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        //     return GetApplicationRelease.Result.OsTooOld
        // }

        return withIOContext {
            val result = getApplicationRelease.await(
                GetApplicationRelease.Arguments(
                    isFossBuildType,
                    BuildConfig.VERSION_NAME,
                    GITHUB_REPO,
                    forceCheck,
                ),
            )

            if (result is GetApplicationRelease.Result.NewUpdate || result is GetApplicationRelease.Result.NoNewUpdate) {
                basePreferences.lastAppUpdateCheck.set(System.currentTimeMillis())
            }

            result
        }
    }
}

val GITHUB_REPO = "devil6venom/Yugen"

val RELEASE_TAG = "v${BuildConfig.VERSION_NAME}"

val RELEASE_URL = "https://github.com/$GITHUB_REPO/releases/tag/$RELEASE_TAG"
