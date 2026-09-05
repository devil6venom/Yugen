# Add "Trust All" Button to Extensions Screen

This plan adds a "Trust All" button to the extensions list, which appears when there are untrusted extensions pending. Clicking this button will trust all currently untrusted extensions and then disappear.

## Proposed Changes

### [ViewModel]

#### [MODIFY] [ExtensionsViewModel.kt](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/app/src/main/java/eu/kanade/tachiyomi/ui/browse/extension/ExtensionsViewModel.kt)
- Add `trustAllExtensions()` method to trust all `Extension.Untrusted` items currently in the state.

### [UI Components]

#### [MODIFY] [ExtensionsScreen.kt](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/app/src/main/java/eu/kanade/presentation/browse/ExtensionsScreen.kt)
- Add `onTrustAllExtensions` callback to `ExtensionScreen` and `ExtensionContent`.
- Update `ExtensionContent` header logic for `MR.strings.ext_installed` to show a "Trust All" button if the group contains any untrusted extensions.

#### [MODIFY] [ExtensionsTab.kt](file:///C:/Users/Rexx-Gaming-PC/AndroidStudioProjects/Yugen/app/src/main/java/eu/kanade/tachiyomi/ui/browse/extension/ExtensionsTab.kt)
- Pass `extensionsViewModel::trustAllExtensions` to `ExtensionScreen`.

## Verification Plan

### Manual Verification
1. Install one or more extensions from an untrusted source (or simulate untrusted extensions).
2. Navigate to the Browse -> Extensions tab.
3. Observe the "Trust All" button appearing next to the "Installed" header.
4. Click "Trust All".
5. Verify that all untrusted extensions are now trusted and the button disappears.
