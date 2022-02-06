/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.webclip

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import dagger.hilt.android.AndroidEntryPoint
import net.mm2d.webclip.databinding.ActivityMainBinding
import net.mm2d.webclip.dialog.IconDialog

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpWebView()
        binding.fab.setOnClickListener {
            IconDialog.show(
                activity = this@MainActivity,
                title = binding.webView.title ?: "",
                siteUrl = binding.webView.url ?: "",
            )
        }
        val url = extractUrlToLoad(intent)
        if (url.isNotEmpty()) {
            binding.webView.loadUrl(url)
        } else {
            binding.webView.loadUrl(DEFAULT_URL)
        }
        binding.backButton.setOnClickListener { binding.webView.goBack() }
        binding.forwardButton.setOnClickListener { binding.webView.goForward() }
        binding.reloadButton.setOnClickListener { binding.webView.reload() }
        binding.settingsButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }
        binding.goButton.setOnClickListener {
            val text = binding.editUrl.text.toString()
            if (text.isEmpty()) return@setOnClickListener
            if (URLUtil.isNetworkUrl(text)) {
                binding.webView.loadUrl(text)
            } else {
                binding.webView.loadUrl("https://www.google.com/search?q=$text")
            }
            binding.editUrl.setText("")
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent ?: return
        setIntent(intent)
        val url = extractUrlToLoad(intent)
        if (url.isNotEmpty()) {
            binding.webView.loadUrl(url)
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            return
        }
        super.onBackPressed()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        binding.webView.settings.also {
            it.javaScriptEnabled = true
            it.setSupportZoom(true)
            it.builtInZoomControls = true
            it.displayZoomControls = false
            it.useWideViewPort = true
            it.loadWithOverviewMode = true
            it.domStorageEnabled = true

            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                val nightMode =
                    (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                val forceDarkMode =
                    if (nightMode) WebSettingsCompat.FORCE_DARK_ON else WebSettingsCompat.FORCE_DARK_OFF
                WebSettingsCompat.setForceDark(it, forceDarkMode)
            }
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
                WebSettingsCompat.setForceDarkStrategy(
                    it, WebSettingsCompat.DARK_STRATEGY_WEB_THEME_DARKENING_ONLY
                )
            }
        }
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                binding.progressBar.progress = newProgress
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                binding.siteTitle.text = title
            }
        }
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                binding.progressBar.progress = 0
                binding.progressBar.visibility = View.VISIBLE
                binding.siteUrl.text = url
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.progressBar.visibility = View.INVISIBLE
                binding.siteUrl.text = url
                binding.siteTitle.text = view?.title
            }
        }
        ExtractorHolder.local.userAgent = binding.webView.settings.userAgentString
        ExtractorHolder.library.userAgent = binding.webView.settings.userAgentString
    }

    companion object {
        private const val DEFAULT_URL = "https://www.google.com/"
        private const val YAHOO_SEARCH_URL = "https://search.yahoo.co.jp/search?ei=UTF-8"
        private const val YAHOO_SEARCH_QUERY_KEY = "p"

        private fun extractUrlToLoad(intent: Intent): String = when (intent.action) {
            Intent.ACTION_VIEW ->
                intent.data?.toString() ?: ""
            Intent.ACTION_SEARCH, Intent.ACTION_WEB_SEARCH ->
                makeSearchUrl(
                    intent.getStringExtra(
                        SearchManager.QUERY
                    ) ?: ""
                )
            else ->
                ""
        }

        private fun makeSearchUrl(query: String): String = Uri.parse(YAHOO_SEARCH_URL)
            .buildUpon()
            .appendQueryParameter(YAHOO_SEARCH_QUERY_KEY, query)
            .build()
            .toString()
    }
}
