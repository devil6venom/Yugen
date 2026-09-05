package eu.kanade.presentation.more.settings.screen.browse.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.more.settings.screen.browse.PopularStore
import mihon.domain.extension.model.ExtensionStore
import mihon.icons.materialsymbols.MaterialSymbols
import mihon.icons.materialsymbols.rounded.Add
import mihon.icons.materialsymbols.rounded.ContentCopy
import mihon.icons.materialsymbols.rounded.Delete
import mihon.icons.materialsymbols.rounded.Public
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun ExtensionStoresContent(
    repos: List<ExtensionStore>,
    lazyListState: LazyListState,
    paddingValues: PaddingValues,
    onCopy: (ExtensionStore) -> Unit,
    onOpenWebsite: (ExtensionStore) -> Unit,
    onClickDelete: (ExtensionStore) -> Unit,
    onToggle: (ExtensionStore, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = lazyListState,
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.medium),
        modifier = modifier,
    ) {
        repos.forEach {
            item(key = it.indexUrl) {
                ExtensionStoresListItem(
                    modifier = Modifier.animateItem(),
                    store = it,
                    onOpenWebsite = { onOpenWebsite(it) },
                    onCopy = { onCopy(it) },
                    onDelete = { onClickDelete(it) },
                    onToggle = { enabled -> onToggle(it, enabled) },
                )
            }
        }
    }
}

@Composable
fun PopularStoreListItem(
    store: PopularStore,
    onClickAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.padding.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(store.iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Column(
                modifier = Modifier
                    .padding(start = MaterialTheme.padding.medium)
                    .weight(1f),
            ) {
                Text(
                    text = stringResource(store.nameRes),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            IconButton(onClick = onClickAdd) {
                Icon(
                    imageVector = MaterialSymbols.Rounded.Add,
                    contentDescription = stringResource(MR.strings.action_add),
                )
            }
        }
    }
}

@Composable
private fun ExtensionStoresListItem(
    store: ExtensionStore,
    onOpenWebsite: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = MaterialTheme.padding.medium,
                    top = MaterialTheme.padding.medium,
                    end = MaterialTheme.padding.medium,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val icon = when {
                store.indexUrl.contains("keiyoushi", ignoreCase = true) -> eu.kanade.tachiyomi.R.mipmap.keiyoushi_repo
                store.indexUrl.contains("suwayomi", ignoreCase = true) -> eu.kanade.tachiyomi.R.mipmap.suwayomi_repo
                store.indexUrl.contains("yuzuno", ignoreCase = true) ||
                    store.indexUrl.contains(
                        "yuzono",
                        ignoreCase = true,
                    ) -> eu.kanade.tachiyomi.R.mipmap.cursedyuzuno_repo
                store.indexUrl.contains("Kareadita", ignoreCase = true) -> eu.kanade.tachiyomi.R.mipmap.kavita_repo
                store.indexUrl.contains("Nyora", ignoreCase = true) -> eu.kanade.tachiyomi.R.mipmap.repo_nyora_manga
                else -> eu.kanade.tachiyomi.R.mipmap.other_repo
            }
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Column(
                modifier = Modifier
                    .padding(start = MaterialTheme.padding.medium)
                    .weight(1f),
            ) {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Switch(
                checked = store.enabled,
                onCheckedChange = onToggle,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            IconButton(onClick = onOpenWebsite) {
                Icon(
                    imageVector = MaterialSymbols.Rounded.Public,
                    contentDescription = stringResource(MR.strings.action_open_in_browser),
                )
            }

            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = MaterialSymbols.Rounded.ContentCopy,
                    contentDescription = stringResource(MR.strings.action_copy_to_clipboard),
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = MaterialSymbols.Rounded.Delete,
                    contentDescription = stringResource(MR.strings.action_delete),
                )
            }
        }
    }
}
