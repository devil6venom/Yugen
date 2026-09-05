package eu.kanade.presentation.more.settings.screen.network

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metrox.viewmodel.metroViewModel
import eu.kanade.presentation.more.settings.Preference
import eu.kanade.presentation.more.settings.PreferenceScaffold
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.network.DnsTestResult
import eu.kanade.tachiyomi.network.DnsTester
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.network.NetworkPreferences
import eu.kanade.tachiyomi.network.PREF_DOH_360
import eu.kanade.tachiyomi.network.PREF_DOH_ADGUARD
import eu.kanade.tachiyomi.network.PREF_DOH_ALIDNS
import eu.kanade.tachiyomi.network.PREF_DOH_CLOUDFLARE
import eu.kanade.tachiyomi.network.PREF_DOH_CONTROLD
import eu.kanade.tachiyomi.network.PREF_DOH_DNSPOD
import eu.kanade.tachiyomi.network.PREF_DOH_GOOGLE
import eu.kanade.tachiyomi.network.PREF_DOH_MULLVAD
import eu.kanade.tachiyomi.network.PREF_DOH_NJALLA
import eu.kanade.tachiyomi.network.PREF_DOH_QUAD101
import eu.kanade.tachiyomi.network.PREF_DOH_QUAD9
import eu.kanade.tachiyomi.network.PREF_DOH_SHECAN
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mihon.icons.materialsymbols.MaterialSymbols
import mihon.icons.materialsymbols.rounded.Refresh
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

class DnsTestScreen : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val viewModel = metroViewModel<DnsTestViewModel>()
        val state by viewModel.state.collectAsState()

        PreferenceScaffold(
            titleRes = MR.strings.pref_dns_over_https,
            onBackPressed = navigator::pop,
            actions = {
                IconButton(onClick = { viewModel.runAllTests(context) }) {
                    Icon(MaterialSymbols.Rounded.Refresh, contentDescription = null)
                }
            },
            itemsProvider = {
                listOf(
                    Preference.PreferenceGroup(
                        title = "DNS Hosts",
                        preferenceItems = ALL_PROVIDERS.map { (id, name) ->
                            val result = state.results[id]
                            Preference.PreferenceItem.TextPreference(
                                title = name,
                                subtitle = when (result) {
                                    is DnsTestResult.Testing -> "Testing..."
                                    is DnsTestResult.Success -> "Ping: ${result.ping}ms"
                                    is DnsTestResult.Failure -> "Failed"
                                    else -> "Idle"
                                },
                                widget = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (result is DnsTestResult.Success) {
                                            PingIndicator(result.ping)
                                        }
                                        IconButton(onClick = { viewModel.runTest(context, id) }) {
                                            Icon(MaterialSymbols.Rounded.Refresh, contentDescription = "Retry")
                                        }
                                    }
                                },
                                onClick = {
                                    viewModel.applyDns(id)
                                    context.toast("DNS applied instantly")
                                },
                            )
                        },
                    ),
                )
            },
        )
    }

    @Composable
    private fun PingIndicator(ping: Long) {
        val color = when {
            ping < 50 -> Color.Green
            ping < 150 -> Color(0xFFFFA500) // Amber
            else -> Color.Red
        }
        Text(
            text = "●",
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
    }

    companion object {
        val ALL_PROVIDERS = mapOf(
            PREF_DOH_CLOUDFLARE to "Cloudflare",
            PREF_DOH_GOOGLE to "Google",
            PREF_DOH_ADGUARD to "AdGuard",
            PREF_DOH_QUAD9 to "Quad9",
            PREF_DOH_ALIDNS to "AliDNS",
            PREF_DOH_DNSPOD to "DNSPod",
            PREF_DOH_360 to "360",
            PREF_DOH_QUAD101 to "Quad 101",
            PREF_DOH_MULLVAD to "Mullvad",
            PREF_DOH_CONTROLD to "Control D",
            PREF_DOH_NJALLA to "Njalla",
            PREF_DOH_SHECAN to "Shecan",
        )
    }
}

@ContributesIntoMap(AppScope::class)
@ViewModelKey(DnsTestViewModel::class)
@Inject
class DnsTestViewModel(
    private val networkHelper: NetworkHelper,
    private val networkPreferences: NetworkPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(DnsTestState())
    val state: StateFlow<DnsTestState> = _state

    private val tester = DnsTester(networkHelper.baseClient)
    private val deadProviders = mutableSetOf<Int>()

    fun runTest(context: android.content.Context, providerId: Int) {
        viewModelScope.launch {
            context.toast("Testing ${DnsTestScreen.ALL_PROVIDERS[providerId] ?: "Unknown"}...")
            tester.testProvider(providerId).collectLatest { result ->
                _state.update { it.copy(results = it.results + (providerId to result)) }
                if (result is DnsTestResult.Failure) {
                    deadProviders.add(providerId)
                } else if (result is DnsTestResult.Success) {
                    deadProviders.remove(providerId)
                }
            }
        }
    }

    fun runAllTests(context: android.content.Context) {
        DnsTestScreen.ALL_PROVIDERS.keys.forEach { id ->
            if (id !in deadProviders) {
                runTest(context, id)
            }
        }
    }

    fun applyDns(providerId: Int) {
        networkPreferences.dohProvider.set(providerId)
    }
}

data class DnsTestState(
    val results: Map<Int, DnsTestResult> = emptyMap(),
)
