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
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import net.mm2d.webclip.databinding.ActivityMainBinding
import net.mm2d.webclip.dialog.IconDialog

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                binding.webView.goBack()
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?,
    ) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        setUpWebView()
        binding.fab.setOnClickListener {
            IconDialog.show(
                activity = this@MainActivity,
                title = binding.webView.title ?: "",
                siteUrl = binding.webView.url ?: "",
            )
        }
        if (savedInstanceState == null) {
            val url = extractUrlToLoad(intent)
            if (url.isNotEmpty()) {
                binding.webView.loadUrl(url)
            } else {
                binding.webView.loadUrl(DEFAULT_URL)
            }
        } else {
            binding.webView.restoreState(savedInstanceState)
        }
        binding.backButton.setOnClickListener {
            binding.webView.goBack()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.forwardButton.setOnClickListener {
            binding.webView.goForward()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.reloadButton.setOnClickListener {
            binding.webView.reload()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.goButton.setOnClickListener {
            val text = binding.editUrl.text.toString()
            if (text.isEmpty()) return@setOnClickListener
            if (URLUtil.isNetworkUrl(text)) {
                binding.webView.loadUrl(text)
            } else {
                binding.webView.loadUrl(makeSearchUrl(text))
            }
            binding.editUrl.setText("")
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onNewIntent(
        intent: Intent,
    ) {
        super.onNewIntent(intent)
        setIntent(intent)
        val url = extractUrlToLoad(intent)
        if (url.isNotEmpty()) {
            binding.webView.loadUrl(url)
        }
    }

    override fun onSaveInstanceState(
        outState: Bundle,
    ) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        binding.drawerLayout.openDrawer(GravityCompat.START)
        return true
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
        }
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(
                view: WebView?,
                newProgress: Int,
            ) {
                binding.progressBar.progress = newProgress
            }

            override fun onReceivedTitle(
                view: WebView?,
                title: String?,
            ) {
                binding.siteTitle.text = title
                supportActionBar?.title = title
            }
        }
        binding.webView.webViewClient = object : WebViewClient() {
            override fun doUpdateVisitedHistory(
                view: WebView,
                url: String,
                isReload: Boolean,
            ) {
                onBackPressedCallback.isEnabled = view.canGoBack()
            }

            override fun onPageStarted(
                view: WebView?,
                url: String?,
                favicon: Bitmap?,
            ) {
                binding.progressBar.progress = 0
                binding.progressBar.visibility = View.VISIBLE
                binding.siteUrl.text = url
                supportActionBar?.subtitle = url
            }

            override fun onPageFinished(
                view: WebView?,
                url: String?,
            ) {
                binding.progressBar.visibility = View.INVISIBLE
                binding.siteUrl.text = url
                binding.siteTitle.text = view?.title
                supportActionBar?.let {
                    it.title = view?.title
                    it.subtitle = url
                }
            }
        }
        ExtractorHolder.local.userAgent = binding.webView.settings.userAgentString
        ExtractorHolder.library.userAgent = binding.webView.settings.userAgentString
    }

    companion object {
        private const val DEFAULT_URL = "https://www.bing.com/"
        private const val SEARCH_URL = "https://www.bing.com/search"
        private const val SEARCH_QUERY_KEY = "q"

        private fun extractUrlToLoad(
            intent: Intent,
        ): String =
            when (intent.action) {
                Intent.ACTION_VIEW ->
                    intent.data?.toString() ?: ""

                Intent.ACTION_SEARCH, Intent.ACTION_WEB_SEARCH ->
                    makeSearchUrl(
                        intent.getStringExtra(
                            SearchManager.QUERY,
                        ) ?: "",
                    )

                else ->
                    ""
            }

        private fun makeSearchUrl(
            query: String,
        ): String =
            SEARCH_URL.toUri()
                .buildUpon()
                .appendQueryParameter(SEARCH_QUERY_KEY, query)
                .build()
                .toString()
    }
}
