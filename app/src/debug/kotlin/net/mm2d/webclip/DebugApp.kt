/*
 * Copyright (c) 2021 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.webclip

import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import okhttp3.logging.HttpLoggingInterceptor

class DebugApp : App() {
    override fun initializeOverrideWhenDebug() {
        setUpStrictMode()
        setUpOkHttp()
    }

    private fun setUpStrictMode() {
        StrictMode.setThreadPolicy(ThreadPolicy.Builder().detectAll().penaltyLog().build())
        StrictMode.setVmPolicy(VmPolicy.Builder().detectDefault().penaltyLog().build())
    }

    private fun VmPolicy.Builder.detectDefault(): VmPolicy.Builder =
        apply {
            detectActivityLeaks()
            detectLeakedClosableObjects()
            detectLeakedRegistrationObjects()
            detectFileUriExposure()
            detectCleartextNetwork()
            detectContentUriWithoutPermission()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                detectCredentialProtectedWhileLocked()
            }
        }

    private fun setUpOkHttp() {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClientHolder.addNetworkInterceptor(loggingInterceptor)
    }
}
