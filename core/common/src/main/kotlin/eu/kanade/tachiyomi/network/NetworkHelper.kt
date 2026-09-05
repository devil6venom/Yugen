package eu.kanade.tachiyomi.network

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import eu.kanade.tachiyomi.network.interceptor.CloudflareInterceptor
import eu.kanade.tachiyomi.network.interceptor.UncaughtExceptionInterceptor
import eu.kanade.tachiyomi.network.interceptor.UserAgentInterceptor
import okhttp3.Cache
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.net.InetAddress
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Inject
@SingleIn(AppScope::class)
class NetworkHelper(
    private val context: Context,
    private val preferences: NetworkPreferences,
) {

    val cookieJar = AndroidCookieJar()

    private val baseClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .connectTimeout(30.seconds)
        .readTimeout(30.seconds)
        .callTimeout(2.minutes)
        .cache(
            Cache(
                directory = File(context.cacheDir, "network_cache"),
                maxSize = 5L * 1024 * 1024, // 5 MiB
            ),
        )
        .addInterceptor(UncaughtExceptionInterceptor())
        .addInterceptor(UserAgentInterceptor(::defaultUserAgentProvider))

    val baseClient: OkHttpClient = run {
        if (preferences.verboseLogging.get()) {
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            }
            baseClientBuilder.addNetworkInterceptor(httpLoggingInterceptor)
        }
        baseClientBuilder.build()
    }

    private val dynamicDns = DynamicDns(baseClient, preferences)

    val client: OkHttpClient = baseClient.newBuilder()
        .dns(dynamicDns)
        .addInterceptor(
            CloudflareInterceptor(context, cookieJar, ::defaultUserAgentProvider),
        )
        .build()

    private class DynamicDns(
        private val client: OkHttpClient,
        private val preferences: NetworkPreferences,
    ) : Dns {
        private val resolvers = mutableMapOf<Int, Dns>()

        override fun lookup(hostname: String): List<InetAddress> {
            val provider = preferences.dohProvider.get()
            if (provider == -1) {
                return Dns.SYSTEM.lookup(hostname)
            }

            val resolver = resolvers.getOrPut(provider) {
                DohProviders.get(client, provider)
            }
            return resolver.lookup(hostname)
        }
    }

    /**
     * @deprecated Since extension-lib 1.5
     */
    @Deprecated("The regular client handles Cloudflare by default")
    @Suppress("UNUSED")
    val cloudflareClient: OkHttpClient = client

    fun defaultUserAgentProvider() = preferences.defaultUserAgent.get().trim()
}
