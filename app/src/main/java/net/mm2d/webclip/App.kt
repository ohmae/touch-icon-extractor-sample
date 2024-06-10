/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.webclip

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.webkit.WebView
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import net.mm2d.webclip.settings.SettingsRepository
import javax.inject.Inject

@HiltAndroidApp
open class App :
    Application(),
    ImageLoaderFactory {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        initializeOverrideWhenDebug()
        WebView.setWebContentsDebuggingEnabled(true)
    }

    protected open fun initializeOverrideWhenDebug() {
        setUpStrictMode()
    }

    private fun setUpStrictMode() {
        StrictMode.setThreadPolicy(ThreadPolicy.LAX)
        StrictMode.setVmPolicy(VmPolicy.LAX)
    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .okHttpClient { OkHttpClientHolder.client }
            .build()
}
