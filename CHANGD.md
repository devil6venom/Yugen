# Changd

All notable changes to this repository will be documented in this file.

> [!NOTE]
> Keep in mind that the commit SHA may changed each time I do a rebase, so the linked commit may not be accurate.

The format is a modified version of [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this repository don't fucking care to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
- `Added` - for new features.
- `Changed ` - for changes in existing functionality.
- `Improved` - for enhancement or optimization in existing functionality.
- `Removed` - for currently removed features.
- `Fixed` - for any bug fixes.
- `Other` - for technical stuff.

### Added
- Add a toggle to turn on or off Last Used extensions from sources tab menu ([added](https://github.com/ddCeka/mihon/commit/d4eb4d320b742d1850082760a2f932827c265716))
- Add option to disable doubletap action to paged reader ([added](https://github.com/ddCeka/mihon/commit/f50f51e4e6ca34a8a9aa70b500747511eca60ae0))
- Add Gotham colorscheme ([added](https://github.com/ddCeka/mihon/commit/6453c5073955cf390be2ec8dab7ba1b5a762b6b5))
- Add uninstall orphaned button on extension list ([added](https://github.com/ddCeka/mihon/commit/03689319c4af3067f964831e1b266e7273250f84))
- Add option to save one-shot type manga as pdf ([added](https://github.com/ddCeka/mihon/commit/cf7684a082d8f08244ad57e27576e64233dc274c))
- Add legacy storage support by directly access to path folders, bypassing SAF picker ([added](https://github.com/ddCeka/mihon/commit/cc9121117b0e79bb9d1556c50dc02fb4bb691c36))
- Add option delete history on time range ([added](https://github.com/ddCeka/mihon/commit/482a170819cfbf7258ade4a9774d68dc5088d09a))
- Add option to resume History from last seen page ([added](https://github.com/ddCeka/mihon/commit/e56d3ad475836ba1c3b96a163cd09501cb00004d))
- Add toggle hardware bitmaps for manga covers ([added](https://github.com/ddCeka/mihon/commit/62b586c96bb3a346e9e1459bf33bcb4f390621d7))
- Add parallel chapters download ([added](https://github.com/ddCeka/mihon/commit/ae93fcf23c5241aaf979d22939554686aa9dcaba))
- Add Github colorscheme ([added](https://github.com/ddCeka/mihon/commit/333de6fb824459ef6764d016091a6eb1e105ad21))
- Add setting to hide pending extension updates count ([added](https://github.com/ddCeka/mihon/commit/ee9ed7d80069874a9a899249fc489008e006eea9))
- Add local source filtering ([added](https://github.com/ddCeka/mihon/commit/a03c7290ff2756589cc5640b5031a629b29ca6cf))
- Add legacy storage permission check for saving covers ([added](https://github.com/ddCeka/mihon/commit/a579d123faf19766dab1a8cf934e6b09d06f426d))
- Add webtoon smooth scroll preference ([added](https://github.com/ddCeka/mihon/commit/727c0a406ce67e3f4bec2f75f9df95fdea5202d6))
- Add global search history ([added](https://github.com/ddCeka/mihon/commit/559f2b98dbb72a5b47d33e739719de1ee20f24f0))
- Add local on App tracking, basically statistics is tracked without any 3rd party trackers. ([added](https://github.com/ddCeka/mihon/commit/d0a6c4366a05f01bcfb1f0f38d5790da740e31d6))

### Changed
- Rebranding to differentiate with upstream ([modified](https://github.com/ddCeka/mihon/commit/66bf6843f37667190a458eb315b50cd4ed48fae6))
- Increase the parallel download page limit to 30 ([modified](https://github.com/ddCeka/mihon/commit/f40fc6509e273cf2b55a513ed352f517cdd4c993))
- Disable the annoying download notification warning when updating library ([modified](https://github.com/ddCeka/mihon/commit/66bf6843f37667190a458eb315b50cd4ed48fae6))
- Keep the legacy image decoder for this repository ([modified](https://github.com/ddCeka/mihon/commit/66bf6843f37667190a458eb315b50cd4ed48fae6))
- Revert Fix reader tap zones triggering after scrolling is stopped by tapping, because it doesn't need a fix ([source:#2680](https://github.com/mihonapp/mihon/pull/2680)) ([modified](https://github.com/ddCeka/mihon/commit/62876f7fbe698e9bfac71b0d6f8be25f996a7379))
- Make Smart Update feature to off by default ([modified](https://github.com/ddCeka/mihon/commit/52f91873c8a1f6accb391d5b7784f1a77f40f84c))
- Use custom decoder in wide page operations ([modified](https://github.com/ddCeka/mihon/commit/3a997c084097ee66cb7d06d91bc0bc0ec670c792))
- Partially Revert Check for app and extension update on every cold start ([source:#3658](https://github.com/mihonapp/mihon/pull/3658)) ([modified](https://github.com/ddCeka/mihon/commit/e679a5ac21b69b537d5ea1e01963b422f0b4a2ed))
- Hide redundant categories screen in more screen ([modified](https://github.com/ddCeka/mihon/commit/66bf6843f37667190a458eb315b50cd4ed48fae6))
- Hide statistics screen from settings ([modified](https://github.com/ddCeka/mihon/commit/40d8043b7579dcbfc099efa1e3faca029df229fd))
- Handle downloads for chapters removed from source ([modified](https://github.com/ddCeka/mihon/commit/06963cd34286aa2fb47ef1b85f1573e7c8360fee))
- Limit author and artist name lines to 1 with ellipsis ([modified](https://github.com/ddCeka/mihon/commit/9f841330a90a117a944b3238daaac0115e61af68))
- Changed downloaded chapters update sorting ([modified](https://github.com/ddCeka/mihon/commit/ce4b2929192f682b41ba97ac45368b05c7239ce7))
- Changed default theme from monet to base app colour ([modified](https://github.com/ddCeka/mihon/commit/aaffcda90a585e23b2457d445e9bb48da9135610))
- Changed monet palette style from expressive to tonalspot ([modified](https://github.com/ddCeka/mihon/commit/7523cf5349d08e4b5201cc936eca53dea4dfda58))

### Improved
- Make "Support Us" no more prominent in this repository ([deleted](https://github.com/ddCeka/mihon/commit/66bf6843f37667190a458eb315b50cd4ed48fae6))
- Include read history when migrating entry with chapters ([modified](https://github.com/ddCeka/mihon/commit/0c063d8ed82bc6fd998edfa00bafe44cd30e6454))
- Parallelize per-manga chapter listing ([modified](https://github.com/ddCeka/mihon/commit/2451ddf6dea570cd12cfb0fe35c68c2c03f6ff90))
- Support dynamic theme for older android ([modified](https://github.com/ddCeka/mihon/commit/c9ec91fca0b9a4ea3c74b57f7e3f360845771de3))
- Reduce unnecessary media scanning ([modified](https://github.com/ddCeka/mihon/commit/f3c77446ba984e086b1e320c7a9cbaf2aa9d3856))
- Include local source in global search results ([modified](https://github.com/ddCeka/mihon/commit/20f26bacf4086d38210b868cea6590abc563df0f))
- Ui from rectangle to rounded covers and tweak appbar titles ([modified](https://github.com/ddCeka/mihon/commit/b18d4f0b79ba71aa15b5f8fcec171b3a63b8512c))
- Make Cloudflare Interceptor attempt to solve challenge when interactive ([modified](https://github.com/ddCeka/mihon/commit/78b27d3943bbf52b916a55e9c6707deae2e67cc0))

### Removed
- Removed uneccessary Telemetry ([deleted](https://github.com/ddCeka/mihon/commit/66bf6843f37667190a458eb315b50cd4ed48fae6))
- Removed discord related codes and cleanup unused stuff ([deleted](https://github.com/ddCeka/mihon/commit/66bf6843f37667190a458eb315b50cd4ed48fae6))
- Removed Installation Id "Feature Flags" ([deleted](https://github.com/ddCeka/mihon/commit/66bf6843f37667190a458eb315b50cd4ed48fae6))
- Removed user timezone tracking in debug logs ([deleted](https://github.com/ddCeka/mihon/commit/66bf6843f37667190a458eb315b50cd4ed48fae6))
- Removed uneccessary clutter in more screen ([deleted](https://github.com/ddCeka/mihon/commit/66bf6843f37667190a458eb315b50cd4ed48fae6))
- Removed annoying updater flags, let the user choose when to update and not get spoon-fed just because it have a "New Update Screen" ([deleted](https://github.com/ddCeka/mihon/commit/66bf6843f37667190a458eb315b50cd4ed48fae6))
- Removed download 1 chapter from drop down menu since it's redundant ([deleted](https://github.com/ddCeka/mihon/commit/9928fd05ac7587784451fb8351f00b6b46df18e7))
- Removed Feature animation speed and scroll distance settings to reader [source commit](https://github.com/ddCeka/mihon/commit/fe5ebfe392845756981519b2c7ad5684cb4c9586) ([deleted](https://github.com/ddCeka/mihon/commit/4309265abe784604d3884afeb8912bfe0d75b711))
  + **NOTE** this need powerful device to utilize the feature else cause stuttering on opening app
- Removed resume downloads after network recovery [source commit](https://github.com/ddCeka/mihon/commit/28fb219b3a054a8c09e437d8c5a2b19b4ebfa561) ([deleted](https://github.com/ddCeka/mihon/commit/29f71dba69f4fddc3ac8a7d21646e93b0df28639))
  + **NOTE** this cause downloader worker to stuck and eat up battery on some occasion
- Removed x86 abis build ([deleted](https://github.com/ddCeka/mihon/commit/1be3a150466432ea30ade983b52ee7d3ba54dec8))
- Removed the smart update logics so that now when a global update occurs then all the manga in the category will be updated. This change will use more data. ([deleted](https://github.com/ddCeka/mihon/commit/0267b80fb97043f911ecea55788ff7abc913d3e1))
- Removed the not needed 3rd party tracking fluff. This commit while destructive is still compatible with backup restore from Mihon. ([deleted](https://github.com/ddCeka/mihon/commit/3c1e3a07b974a1eab240aa6f051b5dc044d23a0e))

### Fixed
- Fixed Search keyboard not closing on Enter and reopening on navigation back ([fix](https://github.com/ddCeka/mihon/commit/a835770b5ec4fb337936435936cc1b6cf63ed191))
- Fixed janky custom animation splash screen exit for android 12 and below ([fix](https://github.com/ddCeka/mihon/commit/66bf6843f37667190a458eb315b50cd4ed48fae6))
- Fixed IndexOutOfBoundsException crash when using split wide pages ([fix](https://github.com/ddCeka/mihon/commit/9a591fec9f1b0f945b2bddb258ecd77dcac95d4f))
- Fixed blank image on oversized webtoon pages by using tiled decoding ([fix](https://github.com/ddCeka/mihon/commit/4e3db450e35c20ebcde37d44848356cfb41651cc))
- Fixed page flashing on auto background ([fix](https://github.com/ddCeka/mihon/commit/ea412a1288d7186f765aee5359b0b83d2a5ae7fb))
- Fixed tracking date selection for all timezones ([fix](https://github.com/ddCeka/mihon/commit/a519d4f57c3c33508813eacd67f442a1e90309b6))
- Fixed update badge overflow when having extensions in the 3 digit ([fix](https://github.com/ddCeka/mihon/commit/780cb12a80aa561b19df892aa93c94574ad476af))
- Fixed calendar to follow system locale change ([fix](https://github.com/ddCeka/mihon/commit/cfba16a4b0e05d559d3fdd6f41d6a73b63edffbb))

### Other
- Refactor release build into foss ([modified](https://github.com/ddCeka/mihon/commit/66bf6843f37667190a458eb315b50cd4ed48fae6))
