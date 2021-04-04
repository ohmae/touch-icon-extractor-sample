/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.webclip

import okhttp3.Interceptor
import okhttp3.OkHttpClient

object OkHttpClientHolder {
    private val networkInterceptors: MutableList<Interceptor> = mutableListOf()

    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(WebViewCookieJar)
            .apply { networkInterceptors.forEach { addNetworkInterceptor(it) } }
            .build()
    }

    fun addNetworkInterceptor(interceptor: Interceptor) {
        networkInterceptors.add(interceptor)
    }
}
