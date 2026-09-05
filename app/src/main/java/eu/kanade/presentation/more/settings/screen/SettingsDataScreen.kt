package eu.kanade.presentation.more.settings.screen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hippo.unifile.UniFile
import eu.kanade.presentation.more.settings.Preference
import eu.kanade.presentation.more.settings.screen.data.CreateBackupScreen
import eu.kanade.presentation.more.settings.screen.data.RestoreBackupScreen
import eu.kanade.presentation.more.settings.screen.data.StorageInfo
import eu.kanade.presentation.more.settings.widget.BasePreferenceWidget
import eu.kanade.presentation.more.settings.widget.PrefsHorizontalPadding
import eu.kanade.presentation.util.relativeTimeSpanString
import eu.kanade.tachiyomi.data.backup.create.BackupCreateJob
import eu.kanade.tachiyomi.data.backup.restore.BackupRestoreJob
import eu.kanade.tachiyomi.data.export.LibraryExporter
import eu.kanade.tachiyomi.data.export.LibraryExporter.ExportOptions
import eu.kanade.tachiyomi.util.system.DeviceUtil
import eu.kanade.tachiyomi.util.system.toast
import eu.kanade.tachiyomi.util.system.workManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import logcat.LogPriority
import mihon.app.di.appGraph
import mihon.icons.materialsymbols.MaterialSymbols
import mihon.icons.materialsymbols.automirroredrounded.Help
import mihon.icons.materialsymbols.rounded.ArrowUpward
import mihon.icons.materialsymbols.rounded.Folder
import mihon.icons.materialsymbols.rounded.KeyboardArrowRight
import mihon.icons.materialsymbols.rounded.Storage
import tachiyomi.core.common.i18n.stringResource
import tachiyomi.core.common.storage.displayablePath
import tachiyomi.core.common.util.lang.launchNonCancellable
import tachiyomi.core.common.util.lang.withUIContext
import tachiyomi.core.common.util.system.logcat
import tachiyomi.domain.backup.service.BackupPreferences
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.storage.service.StoragePreferences
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.TextButton
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.collectAsState

object SettingsDataScreen : SearchableSettings {

    val restorePreferenceKeyString = MR.strings.label_backup
    const val HELP_URL = "https://mihon.app/docs/faq/storage"

    @ReadOnlyComposable
    @Composable
    override fun getTitleRes() = MR.strings.label_data_storage

    @Composable
    override fun RowScope.AppBarAction() {
        val uriHandler = LocalUriHandler.current
        IconButton(onClick = { uriHandler.openUri(HELP_URL) }) {
            Icon(
                imageVector = MaterialSymbols.AutoMirroredRounded.Help,
                contentDescription = stringResource(MR.strings.tracking_guide),
            )
        }
    }

    @Composable
    override fun getPreferences(): List<Preference> {
        val context = LocalContext.current
        val backupPreferences = remember { context.appGraph.backupPreferences }
        val storagePreferences = remember { context.appGraph.storagePreferences }

        return listOf(
            getUseDirectStorageAccessPref(storagePreferences = storagePreferences),
            getStorageLocationPref(storagePreferences = storagePreferences),
            Preference.PreferenceItem.InfoPreference(stringResource(MR.strings.pref_storage_location_info)),

            getBackupAndRestoreGroup(backupPreferences = backupPreferences),
            getDataGroup(),
            getExportGroup(),
        )
    }

    @Composable
    fun storageLocationPicker(
        storageDirPref: tachiyomi.core.common.preference.Preference<String>,
    ): ManagedActivityResultLauncher<Uri?, Uri?> {
        val context = LocalContext.current

        return rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree(),
        ) { uri ->
            if (uri != null) {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                // For some reason InkBook devices do not implement the SAF properly. Persistable URI grants do not
                // work. However, simply retrieving the URI and using it works fine for these devices. Access is not
                // revoked after the app is closed or the device is restarted.
                // This also holds for some Samsung devices. Thus, we simply execute inside of a try-catch block and
                // ignore the exception if it is thrown.
                try {
                    context.contentResolver.takePersistableUriPermission(uri, flags)
                } catch (e: SecurityException) {
                    logcat(LogPriority.ERROR, e)
                    context.toast(MR.strings.file_picker_uri_permission_unsupported)
                }

                UniFile.fromUri(context, uri)?.let {
                    storageDirPref.set(it.uri.toString())
                }
            }
        }
    }

    @Composable
    fun storageLocationText(
        storageDirPref: tachiyomi.core.common.preference.Preference<String>,
    ): String {
        val context = LocalContext.current
        val storageDir by storageDirPref.collectAsState()

        val file = remember(storageDir) { UniFile.fromUri(context, storageDir.toUri()) }
        if (file?.exists() != true) {
            return stringResource(MR.strings.no_location_set)
        }

        return file.displayablePath
    }

    private fun hasManageExternalStorage(): Boolean {
        return android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R ||
            android.os.Environment.isExternalStorageManager()
    }

    private fun requestManageExternalStorage(context: Context) {
        try {
            context.startActivity(
                Intent(
                    android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION,
                    "package:${context.packageName}".toUri(),
                ),
            )
        } catch (e: ActivityNotFoundException) {
            try {
                // Some OEM ROMs don't implement the per-app deep link; fall back to the
                // general management screen.
                context.startActivity(Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
            } catch (e2: ActivityNotFoundException) {
                context.toast(MR.strings.file_picker_error)
            }
        }
    }

    /**
     * Toggles whether "Storage location" below opens the direct filesystem folder browser
     * (MANAGE_EXTERNAL_STORAGE, no DocumentsUI dependency) or the stock SAF picker.
     */
    @Composable
    private fun getUseDirectStorageAccessPref(
        storagePreferences: StoragePreferences,
    ): Preference.PreferenceItem.SwitchPreference {
        return Preference.PreferenceItem.SwitchPreference(
            preference = storagePreferences.useDirectStorageAccess,
            title = stringResource(MR.strings.pref_storage_access),
            subtitle = stringResource(MR.strings.pref_storage_access_summary),
        )
    }

    /**
     * Human-readable volume name, matching what Android's own storage picker shows
     * ("Internal storage", "SD card", or a user-assigned label) instead of the raw
     * mount-point folder name (often a volume UUID like "3A8E-1F1D").
     */
    private fun volumeLabel(context: Context, dir: java.io.File): String {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val storageManager = context.getSystemService(android.os.storage.StorageManager::class.java)
            val match = storageManager?.storageVolumes?.firstOrNull { it.directory?.path == dir.path }
            if (match != null) {
                return match.getDescription(context)
            }
        }
        return if (dir.path == android.os.Environment.getExternalStorageDirectory().path) {
            "Internal storage"
        } else {
            dir.name
        }
    }

    /**
     * Direct filesystem folder browser, bypassing the SAF picker's DocumentsUI dependency.
     * Requires MANAGE_EXTERNAL_STORAGE on API 30+ to list arbitrary directories; on older
     * APIs plain file access already works.
     */
    @Composable
    private fun DirectStorageLocationDialog(
        storagePreferences: StoragePreferences,
        onDismissRequest: () -> Unit,
    ) {
        val context = LocalContext.current
        val volumeRoots = remember {
            context.getExternalFilesDirs(null)
                .filterNotNull()
                .mapNotNull { f ->
                    val idx = f.path.indexOf("/Android/data/")
                    if (idx >= 0) java.io.File(f.path.substring(0, idx)) else null
                }
                .distinct()
        }
        val volumeLabels = remember(volumeRoots) {
            volumeRoots.associateWith { volumeLabel(context, it) }
        }
        var currentDir by remember { mutableStateOf<java.io.File?>(null) }
        val hasPermission = hasManageExternalStorage()

        val entries = remember(currentDir, hasPermission) {
            if (!hasPermission) {
                emptyList()
            } else if (currentDir == null) {
                volumeRoots
            } else {
                currentDir!!.listFiles { f -> f.isDirectory && !f.isHidden }
                    ?.sortedBy { it.name.lowercase() }
                    ?: emptyList()
            }
        }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                val titleText = currentDir?.let { volumeLabels[it] ?: it.name } ?: "Select a storage volume"
                Text(
                    text = titleText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            text = {
                if (!hasPermission) {
                    Text(text = stringResource(MR.strings.pref_storage_access_permission))
                } else {
                    Column(modifier = Modifier.heightIn(max = 400.dp)) {
                        currentDir?.let {
                            Text(
                                text = it.path,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                            )
                            HorizontalDivider()
                        }
                        LazyColumn {
                            if (currentDir != null) {
                                item {
                                    ListItem(
                                        leadingContent = {
                                            Icon(
                                                imageVector = MaterialSymbols.Rounded.ArrowUpward,
                                                contentDescription = null,
                                            )
                                        },
                                        headlineContent = { Text(text = "Up") },
                                        modifier = Modifier.clickable {
                                            val dir = currentDir!!
                                            currentDir = if (volumeRoots.any { it.path == dir.path }) {
                                                null
                                            } else {
                                                dir.parentFile
                                            }
                                        },
                                    )
                                    HorizontalDivider()
                                }
                            }
                            if (entries.isEmpty()) {
                                item {
                                    Text(
                                        text = stringResource(MR.strings.pref_storage_access_subdirectory),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(16.dp),
                                    )
                                }
                            }
                            items(entries) { entry ->
                                ListItem(
                                    leadingContent = {
                                        Icon(
                                            imageVector = if (currentDir == null) {
                                                MaterialSymbols.Rounded.Storage
                                            } else {
                                                MaterialSymbols.Rounded.Folder
                                            },
                                            contentDescription = null,
                                        )
                                    },
                                    headlineContent = {
                                        val label = if (currentDir == null) {
                                            volumeLabels[entry] ?: entry.name
                                        } else {
                                            entry.name
                                        }
                                        Text(text = label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    },
                                    trailingContent = {
                                        Icon(
                                            imageVector = MaterialSymbols.Rounded.KeyboardArrowRight,
                                            contentDescription = null,
                                        )
                                    },
                                    modifier = Modifier.clickable { currentDir = entry },
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (!hasPermission) {
                    TextButton(
                        onClick = {
                            requestManageExternalStorage(context)
                            onDismissRequest()
                        },
                    ) {
                        Text(text = stringResource(MR.strings.pref_storage_access_grant_permission))
                    }
                } else {
                    TextButton(
                        enabled = currentDir != null,
                        onClick = {
                            storagePreferences.baseStorageDirectory.set(currentDir!!.toUri().toString())
                            onDismissRequest()
                        },
                    ) {
                        Text(text = stringResource(MR.strings.pref_storage_access_pick_folder))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(MR.strings.action_cancel))
                }
            },
        )
    }

    @Composable
    private fun getStorageLocationPref(
        storagePreferences: StoragePreferences,
    ): Preference.PreferenceItem.TextPreference {
        val context = LocalContext.current
        val useDirect by storagePreferences.useDirectStorageAccess.collectAsState()
        val pickStorageLocation = storageLocationPicker(storagePreferences.baseStorageDirectory)
        var showDirectDialog by remember { mutableStateOf(false) }

        if (showDirectDialog) {
            DirectStorageLocationDialog(
                storagePreferences = storagePreferences,
                onDismissRequest = { showDirectDialog = false },
            )
        }

        return Preference.PreferenceItem.TextPreference(
            title = stringResource(MR.strings.pref_storage_location),
            subtitle = storageLocationText(storagePreferences.baseStorageDirectory),
            onClick = {
                if (useDirect) {
                    showDirectDialog = true
                } else {
                    try {
                        pickStorageLocation.launch(null)
                    } catch (e: ActivityNotFoundException) {
                        context.toast(MR.strings.file_picker_error)
                    }
                }
            },
        )
    }

    @Composable
    private fun getBackupAndRestoreGroup(backupPreferences: BackupPreferences): Preference.PreferenceGroup {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val lastAutoBackup by backupPreferences.lastAutoBackupTimestamp.collectAsState()

        val chooseBackup = rememberLauncherForActivityResult(
            object : ActivityResultContracts.GetContent() {
                override fun createIntent(context: Context, input: String): Intent {
                    val intent = super.createIntent(context, input)
                    return Intent.createChooser(intent, context.stringResource(MR.strings.file_select_backup))
                }
            },
        ) {
            if (it == null) {
                context.toast(MR.strings.file_null_uri_error)
                return@rememberLauncherForActivityResult
            }

            navigator.push(RestoreBackupScreen(it.toString()))
        }

        return Preference.PreferenceGroup(
            title = stringResource(MR.strings.label_backup),
            preferenceItems = listOf(
                // Manual actions
                Preference.PreferenceItem.CustomPreference(
                    title = stringResource(restorePreferenceKeyString),
                ) {
                    BasePreferenceWidget(
                        subcomponent = {
                            MultiChoiceSegmentedButtonRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(intrinsicSize = IntrinsicSize.Min)
                                    .padding(horizontal = PrefsHorizontalPadding),
                            ) {
                                SegmentedButton(
                                    modifier = Modifier.fillMaxHeight(),
                                    checked = false,
                                    onCheckedChange = { navigator.push(CreateBackupScreen()) },
                                    shape = SegmentedButtonDefaults.itemShape(0, 2),
                                ) {
                                    Text(stringResource(MR.strings.pref_create_backup))
                                }
                                SegmentedButton(
                                    modifier = Modifier.fillMaxHeight(),
                                    checked = false,
                                    onCheckedChange = {
                                        if (!BackupRestoreJob.isRunning(context.workManager)) {
                                            if (DeviceUtil.isMiui && DeviceUtil.isMiuiOptimizationDisabled()) {
                                                context.toast(MR.strings.restore_miui_warning)
                                            }

                                            // no need to catch because it's wrapped with a chooser
                                            chooseBackup.launch("*/*")
                                        } else {
                                            context.toast(MR.strings.restore_in_progress)
                                        }
                                    },
                                    shape = SegmentedButtonDefaults.itemShape(1, 2),
                                ) {
                                    Text(stringResource(MR.strings.pref_restore_backup))
                                }
                            }
                        },
                    )
                },

                // Automatic backups
                Preference.PreferenceItem.ListPreference(
                    preference = backupPreferences.backupInterval,
                    entries = mapOf(
                        0 to stringResource(MR.strings.off),
                        6 to stringResource(MR.strings.update_6hour),
                        12 to stringResource(MR.strings.update_12hour),
                        24 to stringResource(MR.strings.update_24hour),
                        48 to stringResource(MR.strings.update_48hour),
                        168 to stringResource(MR.strings.update_weekly),
                    ),
                    title = stringResource(MR.strings.pref_backup_interval),
                    onValueChanged = {
                        BackupCreateJob.setupTask(context, it)
                        true
                    },
                ),
                Preference.PreferenceItem.InfoPreference(
                    stringResource(MR.strings.backup_info) + "\n\n" +
                        stringResource(MR.strings.last_auto_backup_info, relativeTimeSpanString(lastAutoBackup)),
                ),
            ),
        )
    }

    @Composable
    private fun getDataGroup(): Preference.PreferenceGroup {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val libraryPreferences = remember { context.appGraph.libraryPreferences }

        val chapterCache = remember { context.appGraph.chapterCache }
        var cacheReadableSizeSema by remember { mutableIntStateOf(0) }
        val cacheReadableSize = remember(cacheReadableSizeSema) { chapterCache.readableSize }

        return Preference.PreferenceGroup(
            title = stringResource(MR.strings.pref_storage_usage),
            preferenceItems = listOf(
                Preference.PreferenceItem.CustomPreference(
                    title = stringResource(MR.strings.pref_storage_usage),
                ) {
                    BasePreferenceWidget(
                        subcomponent = {
                            StorageInfo(
                                modifier = Modifier.padding(horizontal = PrefsHorizontalPadding),
                            )
                        },
                    )
                },

                Preference.PreferenceItem.TextPreference(
                    title = stringResource(MR.strings.pref_clear_chapter_cache),
                    subtitle = stringResource(MR.strings.used_cache, cacheReadableSize),
                    onClick = {
                        scope.launchNonCancellable {
                            try {
                                val deletedFiles = chapterCache.clear()
                                withUIContext {
                                    context.toast(context.stringResource(MR.strings.cache_deleted, deletedFiles))
                                    cacheReadableSizeSema++
                                }
                            } catch (e: Throwable) {
                                logcat(LogPriority.ERROR, e)
                                withUIContext { context.toast(MR.strings.cache_delete_error) }
                            }
                        }
                    },
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = libraryPreferences.autoClearChapterCache,
                    title = stringResource(MR.strings.pref_auto_clear_chapter_cache),
                ),
            ),
        )
    }

    @Composable
    private fun getExportGroup(): Preference.PreferenceGroup {
        var showDialog by remember { mutableStateOf(false) }
        var exportOptions by remember {
            mutableStateOf(
                ExportOptions(
                    includeTitle = true,
                    includeAuthor = true,
                    includeArtist = true,
                ),
            )
        }

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val getFavorites = remember { context.appGraph.getFavorites }
        var favorites by remember { mutableStateOf<List<Manga>>(emptyList()) }
        LaunchedEffect(Unit) {
            favorites = getFavorites.await()
        }

        val saveFileLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("text/csv"),
        ) { uri ->
            uri?.let {
                scope.launch {
                    LibraryExporter.exportToCsv(
                        context = context,
                        uri = it,
                        favorites = favorites,
                        options = exportOptions,
                        onExportComplete = {
                            scope.launch(Dispatchers.Main) {
                                context.toast(MR.strings.library_exported)
                            }
                        },
                    )
                }
            }
        }

        if (showDialog) {
            ColumnSelectionDialog(
                options = exportOptions,
                onConfirm = { options ->
                    exportOptions = options
                    saveFileLauncher.launch("mihon_library.csv")
                },
                onDismissRequest = { showDialog = false },
            )
        }

        return Preference.PreferenceGroup(
            title = stringResource(MR.strings.export),
            preferenceItems = listOf(
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(MR.strings.library_list),
                    onClick = { showDialog = true },
                ),
            ),
        )
    }

    @Composable
    private fun ColumnSelectionDialog(
        options: ExportOptions,
        onConfirm: (ExportOptions) -> Unit,
        onDismissRequest: () -> Unit,
    ) {
        var titleSelected by remember { mutableStateOf(options.includeTitle) }
        var authorSelected by remember { mutableStateOf(options.includeAuthor) }
        var artistSelected by remember { mutableStateOf(options.includeArtist) }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(MR.strings.migration_dialog_what_to_include))
            },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = titleSelected,
                            onCheckedChange = { checked ->
                                titleSelected = checked
                                if (!checked) {
                                    authorSelected = false
                                    artistSelected = false
                                }
                            },
                        )
                        Text(text = stringResource(MR.strings.title))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = authorSelected,
                            onCheckedChange = { authorSelected = it },
                            enabled = titleSelected,
                        )
                        Text(text = stringResource(MR.strings.author))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = artistSelected,
                            onCheckedChange = { artistSelected = it },
                            enabled = titleSelected,
                        )
                        Text(text = stringResource(MR.strings.artist))
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(
                            ExportOptions(
                                includeTitle = titleSelected,
                                includeAuthor = authorSelected,
                                includeArtist = artistSelected,
                            ),
                        )
                        onDismissRequest()
                    },
                ) {
                    Text(text = stringResource(MR.strings.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(MR.strings.action_cancel))
                }
            },
        )
    }
}
