# Sync with Latest Mihon Changes

Synchronize the Yugen project with the latest 10 commits from the Mihon upstream repository. This includes updating build dependencies, refreshing GitHub Action versions, and applying a critical fix for a GPU texture leak in the WebGPU reader.

## User Review Required

> [!IMPORTANT]
> **WebGpuViewer.kt Missing**: The file `app/src/main/java/eu/kanade/tachiyomi/ui/reader/viewer/webgpu/WebGpuViewer.kt` was not found in the current project. I will create the directory and the file based on the latest Mihon source (including the leak fix) as part of this sync. Please verify if you intended to include this new viewer feature.

> [!NOTE]
> **New Dependencies**: I am adding `firebase-bom` and `kim` to `libs.versions.toml` to match Mihon's recent dependency updates. While not explicitly used in the `app` module yet, they ensure build consistency with the upstream.

## Proposed Changes

### Build Configuration & Dependencies

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/gradle/libs.versions.toml)
- Update `metro` from `1.4.2` to `1.4.3`.
- Update `kotlin-gradle` from `2.4.10` to `2.4.20`.
- Update `android-gradle` from `9.3.2` to `9.4.0`.
- Update `spotless` from `8.10.1` to `8.10.2`.
- Update `composeRichEditor` from `1.0.0-rc13` to `1.2.0`.
- [NEW] Add `firebase-bom` version `34.19.0`.
- [NEW] Add `kim` version `0.39.1`.

### GitHub Workflows

#### [MODIFY] [build.yml](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/.github/workflows/build.yml)
- Update `actions/setup-java` to use commit `de7274f081f381c8f8158605e0321c36c376e2e6` (v6.0.1).

#### [MODIFY] [release.yml](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/.github/workflows/release.yml)
- Update `actions/setup-java` to use commit `de7274f081f381c8f8158605e0321c36c376e2e6` (v6.0.1).
- Update `softprops/action-gh-release` to use commit `efb35369e0ad2afab669f228072c1b0d510eae64` (v3.0.3).

### Reader (WebGPU)

#### [NEW] [WebGpuViewer.kt](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/app/src/main/java/eu/kanade/tachiyomi/ui/reader/viewer/webgpu/WebGpuViewer.kt)
- Create the `webgpu` directory and add the `WebGpuViewer.kt` source from Mihon.
- Include the fix from PR #3872 (`imagePage.cleanup()`) to prevent GPU texture leaks.

## Verification Plan

### Automated Tests
- Run `./gradlew spotlessCheck` to ensure code formatting complies with the updated Spotless version.
- Run `./gradlew assembleDebug` to verify the project builds successfully with the updated Gradle, Kotlin, and Compose Rich Editor versions.

### Manual Verification
- Verify that the GitHub workflow files show the updated action versions in a pull request or on the Actions tab.
- If the WebGPU viewer is enabled in settings, verify that reading manga pages does not cause unusual memory growth or crashes related to texture leaks.
