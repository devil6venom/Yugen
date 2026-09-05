package eu.kanade.tachiyomi.network

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress

/**
 * Based on https://github.com/square/okhttp/blob/ef5d0c83f7bbd3a0c0534e7ca23cbc4ee7550f3b/okhttp-dnsoverhttps/src/test/java/okhttp3/dnsoverhttps/DohProviders.java
 */

const val PREF_DOH_CLOUDFLARE = 1
const val PREF_DOH_GOOGLE = 2
const val PREF_DOH_ADGUARD = 3
const val PREF_DOH_QUAD9 = 4
const val PREF_DOH_ALIDNS = 5
const val PREF_DOH_DNSPOD = 6
const val PREF_DOH_360 = 7
const val PREF_DOH_QUAD101 = 8
const val PREF_DOH_MULLVAD = 9
const val PREF_DOH_CONTROLD = 10
const val PREF_DOH_NJALLA = 11
const val PREF_DOH_SHECAN = 12

fun OkHttpClient.Builder.dohCloudflare() = dns(DohProviders.getCloudflare(build()))

fun OkHttpClient.Builder.dohGoogle() = dns(DohProviders.getGoogle(build()))

// AdGuard "Default" DNS works too but for the sake of making sure no site is blacklisted,
// we use "Unfiltered"
fun OkHttpClient.Builder.dohAdGuard() = dns(DohProviders.getAdGuard(build()))

fun OkHttpClient.Builder.dohQuad9() = dns(DohProviders.getQuad9(build()))

fun OkHttpClient.Builder.dohAliDNS() = dns(DohProviders.getAliDNS(build()))

fun OkHttpClient.Builder.dohDNSPod() = dns(DohProviders.getDNSPod(build()))

fun OkHttpClient.Builder.doh360() = dns(DohProviders.get360(build()))

fun OkHttpClient.Builder.dohQuad101() = dns(DohProviders.getQuad101(build()))

/*
 * Mullvad DoH
 * without ad blocking option
 * Source: https://mullvad.net/en/help/dns-over-https-and-dns-over-tls
 */
fun OkHttpClient.Builder.dohMullvad() = dns(DohProviders.getMullvad(build()))

/*
 * Control D
 * unfiltered option
 * Source: https://controld.com/free-dns/?
 */
fun OkHttpClient.Builder.dohControlD() = dns(DohProviders.getControlD(build()))

/*
 * Njalla
 * Non logging and uncensored
 */
fun OkHttpClient.Builder.dohNajalla() = dns(DohProviders.getNajalla(build()))

/**
 * Source: https://shecan.ir/
 */
fun OkHttpClient.Builder.dohShecan() = dns(DohProviders.getShecan(build()))

object DohProviders {
    fun get(client: OkHttpClient, provider: Int): okhttp3.Dns {
        return when (provider) {
            PREF_DOH_CLOUDFLARE -> getCloudflare(client)
            PREF_DOH_GOOGLE -> getGoogle(client)
            PREF_DOH_ADGUARD -> getAdGuard(client)
            PREF_DOH_QUAD9 -> getQuad9(client)
            PREF_DOH_ALIDNS -> getAliDNS(client)
            PREF_DOH_DNSPOD -> getDNSPod(client)
            PREF_DOH_360 -> get360(client)
            PREF_DOH_QUAD101 -> getQuad101(client)
            PREF_DOH_MULLVAD -> getMullvad(client)
            PREF_DOH_CONTROLD -> getControlD(client)
            PREF_DOH_NJALLA -> getNajalla(client)
            PREF_DOH_SHECAN -> getShecan(client)
            else -> okhttp3.Dns.SYSTEM
        }
    }

    fun getCloudflare(client: OkHttpClient) = DnsOverHttps.Builder().client(client)
        .url("https://cloudflare-dns.com/dns-query".toHttpUrl())
        .bootstrapDnsHosts(
            InetAddress.getByName("162.159.36.1"),
            InetAddress.getByName("162.159.46.1"),
            InetAddress.getByName("1.1.1.1"),
            InetAddress.getByName("1.0.0.1"),
            InetAddress.getByName("162.159.132.53"),
            InetAddress.getByName("2606:4700:4700::1111"),
            InetAddress.getByName("2606:4700:4700::1001"),
            InetAddress.getByName("2606:4700:4700::0064"),
            InetAddress.getByName("2606:4700:4700::6400"),
        )
        .build()

    fun getGoogle(client: OkHttpClient) = DnsOverHttps.Builder().client(client)
        .url("https://dns.google/dns-query".toHttpUrl())
        .bootstrapDnsHosts(
            InetAddress.getByName("8.8.4.4"),
            InetAddress.getByName("8.8.8.8"),
            InetAddress.getByName("2001:4860:4860::8888"),
            InetAddress.getByName("2001:4860:4860::8844"),
        )
        .build()

    fun getAdGuard(client: OkHttpClient) = DnsOverHttps.Builder().client(client)
        .url("https://dns-unfiltered.adguard.com/dns-query".toHttpUrl())
        .bootstrapDnsHosts(
            InetAddress.getByName("94.140.14.140"),
            InetAddress.getByName("94.140.14.141"),
            InetAddress.getByName("2a10:50c0::1:ff"),
            InetAddress.getByName("2a10:50c0::2:ff"),
        )
        .build()

    fun getQuad9(client: OkHttpClient) = DnsOverHttps.Builder().client(client)
        .url("https://dns.quad9.net/dns-query".toHttpUrl())
        .bootstrapDnsHosts(
            InetAddress.getByName("9.9.9.9"),
            InetAddress.getByName("149.112.112.112"),
            InetAddress.getByName("2620:fe::fe"),
            InetAddress.getByName("2620:fe::9"),
        )
        .build()

    fun getAliDNS(client: OkHttpClient) = DnsOverHttps.Builder().client(client)
        .url("https://dns.alidns.com/dns-query".toHttpUrl())
        .bootstrapDnsHosts(
            InetAddress.getByName("223.5.5.5"),
            InetAddress.getByName("223.6.6.6"),
            InetAddress.getByName("2400:3200::1"),
            InetAddress.getByName("2400:3200:baba::1"),
        )
        .build()

    fun getDNSPod(client: OkHttpClient) = DnsOverHttps.Builder().client(client)
        .url("https://doh.pub/dns-query".toHttpUrl())
        .bootstrapDnsHosts(
            InetAddress.getByName("1.12.12.12"),
            InetAddress.getByName("120.53.53.53"),
        )
        .build()

    fun get360(client: OkHttpClient) = DnsOverHttps.Builder().client(client)
        .url("https://doh.360.cn/dns-query".toHttpUrl())
        .bootstrapDnsHosts(
            InetAddress.getByName("101.226.4.6"),
            InetAddress.getByName("218.30.118.6"),
            InetAddress.getByName("123.125.81.6"),
            InetAddress.getByName("140.207.198.6"),
            InetAddress.getByName("180.163.249.75"),
            InetAddress.getByName("101.199.113.208"),
            InetAddress.getByName("36.99.170.86"),
        )
        .build()

    fun getQuad101(client: OkHttpClient) = DnsOverHttps.Builder().client(client)
        .url("https://dns.twnic.tw/dns-query".toHttpUrl())
        .bootstrapDnsHosts(
            InetAddress.getByName("101.101.101.101"),
            InetAddress.getByName("2001:de4::101"),
            InetAddress.getByName("2001:de4::102"),
        )
        .build()

    fun getMullvad(client: OkHttpClient) = DnsOverHttps.Builder().client(client)
        .url(" https://dns.mullvad.net/dns-query".toHttpUrl())
        .bootstrapDnsHosts(
            InetAddress.getByName("194.242.2.2"),
            InetAddress.getByName("2a07:e340::2"),
        )
        .build()

    fun getControlD(client: OkHttpClient) = DnsOverHttps.Builder().client(client)
        .url("https://freedns.controld.com/p0".toHttpUrl())
        .bootstrapDnsHosts(
            InetAddress.getByName("76.76.2.0"),
            InetAddress.getByName("76.76.10.0"),
            InetAddress.getByName("2606:1a40::"),
            InetAddress.getByName("2606:1a40:1::"),
        )
        .build()

    fun getNajalla(client: OkHttpClient) = DnsOverHttps.Builder().client(client)
        .url("https://dns.njal.la/dns-query".toHttpUrl())
        .bootstrapDnsHosts(
            InetAddress.getByName("95.215.19.53"),
            InetAddress.getByName("2001:67c:2354:2::53"),
        )
        .build()

    fun getShecan(client: OkHttpClient) = DnsOverHttps.Builder().client(client)
        .url("https://free.shecan.ir/dns-query".toHttpUrl())
        .bootstrapDnsHosts(
            InetAddress.getByName("178.22.122.100"),
            InetAddress.getByName("185.51.200.2"),
        )
        .build()
}
