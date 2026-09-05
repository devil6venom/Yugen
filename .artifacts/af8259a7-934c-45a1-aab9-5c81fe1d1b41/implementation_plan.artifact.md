# Implement Library Grouping Feature

This plan outlines the implementation of a new grouping feature in the Library screen, allowing users to organize their manga by criteria such as Source, Status, and more.

## User Review Required

> [!IMPORTANT]
> The grouping feature will introduce headers within the library list/grid. This might slightly change the visual density of the library.
> We need to decide if grouping should be global or per-category. The proposed plan implements it per-category but respects a global setting.

## Proposed Changes

### Domain Module

#### [NEW] [LibraryGroup.kt](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/domain/src/main/java/tachiyomi/domain/library/model/LibraryGroup.kt)
Define a new `LibraryGroup` model to represent the grouping criteria.
- `BY_DEFAULT` (No grouping/By Category)
- `BY_SOURCE`
- `BY_STATUS`
- `BY_TRACKING_STATUS`
- `BY_LANGUAGE`

#### [MODIFY] [LibraryPreferences.kt](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/domain/src/main/java/tachiyomi/domain/library/service/LibraryPreferences.kt)
- Add `groupingMode` preference using the new `LibraryGroup` enum.

---

### App Module (Logic)

#### [MODIFY] [LibraryViewModel.kt](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/app/src/main/java/eu/kanade/tachiyomi/ui/library/LibraryViewModel.kt)
- Update `DisplayPreferences` to include `groupingMode`.
- Enhance `applyGrouping` logic to support non-category grouping.
- Update `LibraryViewModel.State` to handle grouped items and headers.
- Implement logic to generate headers based on the selected grouping mode.

#### [MODIFY] [LibrarySettingsViewModel.kt](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/app/src/main/java/eu/kanade/tachiyomi/ui/library/LibrarySettingsViewModel.kt)
- Add methods to update the grouping mode preference.

---

### Presentation / UI

#### [MODIFY] [LibrarySettingsDialog.kt](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/app/src/main/java/eu/kanade/presentation/library/LibrarySettingsDialog.kt)
- Add a "Group by" selection row in the `Display` tab of the settings dialog.
- Use chips or a dropdown to allow users to select the grouping mode.

#### [MODIFY] [LibraryContent.kt](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/app/src/main/java/eu/kanade/presentation/library/components/LibraryContent.kt)
- Update to pass grouping information to the pager and subsequent list/grid components.

#### [MODIFY] [LibraryList.kt](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/app/src/main/java/eu/kanade/presentation/library/components/LibraryList.kt) & [LibraryCompactGrid.kt](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/app/src/main/java/eu/kanade/presentation/library/components/LibraryCompactGrid.kt)
- Update these components to support rendering headers.
- Use `LazyColumn` / `LazyVerticalGrid`'s `item` DSL for headers.
- Implement `LibraryGroupHeader` component for a clean look.

---

### Resources

#### [MODIFY] [strings.xml / MR.strings](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/i18n/src/commonMain/moko-resources/base/strings.xml)
- Add string resources for grouping options:
    - `action_group_by`
    - `group_by_source`
    - `group_by_status`
    - `group_by_language`
    - etc.

## Benefits and Drawbacks

### Benefits
- **Improved Organization**: Users with large libraries can find manga more easily.
- **Flexibility**: Multiple grouping criteria cater to different user needs.
- **Consistency**: Follows the existing pattern of filtering and sorting.

### Drawbacks
- **UI Complexity**: Adding headers might make the grid look more cluttered for some users.
- **Performance**: Additional grouping logic in the ViewModel, though mitigated by `Dispatchers.IO` and `StateFlow`.

## Verification Plan

### Automated Tests
- Unit tests for `LibraryViewModel#applyGrouping` with various criteria.
- Verify that `LibraryPreferences` correctly stores and retrieves the grouping mode.

### Manual Verification
1. Open Library Settings.
2. Navigate to the Display tab.
3. Change "Group by" to "Source".
4. Verify that the library list/grid now shows headers for each source.
5. Repeat for "Status" and other grouping modes.
6. Verify that filtering and sorting still work correctly within groups.
