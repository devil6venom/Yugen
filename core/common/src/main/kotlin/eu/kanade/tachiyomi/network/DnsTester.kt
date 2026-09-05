package eu.kanade.tachiyomi.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import kotlin.system.measureTimeMillis

class DnsTester(private val client: OkHttpClient) {

    fun testProvider(providerId: Int): Flow<DnsTestResult> = flow {
        emit(DnsTestResult.Testing(providerId))

        val result = withContext(Dispatchers.IO) {
            try {
                val resolver = DohProviders.get(client, providerId)
                var ping: Long = -1

                withTimeout(5000L) {
                    ping = measureTimeMillis {
                        resolver.lookup("google.com")
                    }
                }

                if (ping >= 0) {
                    DnsTestResult.Success(providerId, ping)
                } else {
                    DnsTestResult.Failure(providerId)
                }
            } catch (e: TimeoutCancellationException) {
                DnsTestResult.Failure(providerId)
            } catch (e: Exception) {
                DnsTestResult.Failure(providerId)
            }
        }
        emit(result)
    }
}

sealed class DnsTestResult {
    abstract val providerId: Int

    data class Testing(override val providerId: Int) : DnsTestResult()
    data class Success(override val providerId: Int, val ping: Long) : DnsTestResult()
    data class Failure(override val providerId: Int) : DnsTestResult()
}
