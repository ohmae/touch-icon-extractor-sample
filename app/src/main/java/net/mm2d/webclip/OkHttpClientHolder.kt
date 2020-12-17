/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.webclip

import okhttp3.OkHttpClient

object OkHttpClientHolder {
    val client: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(WebViewCookieJar)
        .build()
}
