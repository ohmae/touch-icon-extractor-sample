/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.webclip.dialog

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mm2d.touchicon.Icon
import net.mm2d.webclip.ExtractorHolder
import net.mm2d.webclip.R
import net.mm2d.webclip.databinding.DialogIconBinding
import net.mm2d.webclip.settings.SettingsRepository
import net.mm2d.webclip.settings.UserSettings
import net.mm2d.webclip.util.Downloader
import net.mm2d.webclip.util.Toaster
import net.mm2d.webclip.util.registerForActivityResultWrapper
import javax.inject.Inject

@AndroidEntryPoint
class IconDialog : DialogFragment() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val launcher =
        registerForActivityResultWrapper(RequestPermission(), PERMISSION, ::onPermissionResult)

    override fun onCreateDialog(
        savedInstanceState: Bundle?,
    ): Dialog {
        val activity = requireActivity()
        val arguments = requireArguments()
        val title = arguments.getString(KEY_TITLE)!!
        val binding = DialogIconBinding.inflate(layoutInflater)
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL,
            ),
        )
        lifecycleScope.launch {
            settingsRepository.userSettingsRepository.flow.take(1).collectLatest {
                onUserSetting(binding, it)
            }
        }
        return AlertDialog.Builder(activity)
            .setTitle(title)
            .setView(binding.root)
            .create()
    }

    private fun onUserSetting(
        binding: DialogIconBinding,
        userSettings: UserSettings,
    ) {
        val activity = requireActivity()
        binding.transparentSwitch.isChecked = userSettings.showTransparentGrid
        val extractor =
            if (userSettings.useExtension) {
                ExtractorHolder.library
            } else {
                ExtractorHolder.local
            }

        val adapter = IconListAdapter(activity, userSettings.showTransparentGrid, ::onMoreClick)
        binding.transparentSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingsRepository.userSettingsRepository.updateTrans(isChecked)
            }
            adapter.showTransparentGrid = isChecked
        }
        binding.recyclerView.adapter = adapter

        val siteUrl = requireArguments().getString(KEY_SITE_URL)!!
        binding.siteUrl.text = siteUrl
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                extractor.fromPage(siteUrl, true)
            }.let { adapter.add(it) }
            binding.progressBar.visibility = View.GONE
        }
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                extractor.listFromDomain(siteUrl, true, listOf("120x120"))
            }.let { adapter.add(it) }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun onMoreClick(
        view: View,
        icon: Icon,
    ) {
        val context = requireContext()
        ListPopupWindow(context).also {
            it.setDropDownGravity(Gravity.END)
            it.anchorView = view
            it.width = resources.getDimensionPixelSize(R.dimen.menu_width)
            it.verticalOffset = -view.height
            it.horizontalOffset = resources.getDimensionPixelSize(R.dimen.menu_horizontal_offset)
            val adapter = MenuAdapter(context)
            adapter.add(R.drawable.ic_download to R.string.download)
            adapter.add(R.drawable.ic_open_in_browser to R.string.open_in_other_app)
            it.setAdapter(adapter)
            it.setOnItemClickListener { _, _, _, id ->
                when (id.toInt()) {
                    R.string.download -> {
                        download(icon)
                    }

                    R.string.open_in_other_app -> {
                        open(icon)
                    }
                }
                it.dismiss()
            }
        }.show()
    }

    private fun onPermissionResult(
        granted: Boolean,
    ) {
        val activity = requireActivity()
        when {
            granted -> {
                Toaster.show(activity, R.string.toast_success_to_grant_storage_permission)
            }

            !ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION) -> {
                PermissionDialog.show(activity)
            }

            else -> {
                Toaster.show(activity, R.string.toast_fail_to_grant_storage_permission)
            }
        }
    }

    private fun download(
        icon: Icon,
    ) {
        val context = requireContext()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (PermissionChecker.checkSelfPermission(context, PERMISSION) !=
                PermissionChecker.PERMISSION_GRANTED
            ) {
                launcher.launch()
            }
        }
        lifecycleScope.launch(
            CoroutineExceptionHandler { _, _ ->
                Toaster.show(context, R.string.toast_download_failed)
            },
        ) {
            withContext(Dispatchers.IO) {
                Downloader.download(context, icon)
            }.let {
                Toaster.show(
                    context,
                    if (it) R.string.toast_download_saved else R.string.toast_download_failed,
                )
            }
        }
    }

    private fun open(
        icon: Icon,
    ) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, icon.url.toUri()))
        } catch (e: Exception) {
            val context = requireContext()
            Toaster.show(context, R.string.toast_failed_to_open)
        }
    }

    companion object {
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SITE_URL = "KEY_SITE_URL"
        private const val PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

        fun show(
            activity: FragmentActivity,
            title: String,
            siteUrl: String,
        ) {
            IconDialog().also {
                it.arguments = bundleOf(
                    KEY_TITLE to title,
                    KEY_SITE_URL to siteUrl,
                )
            }.show(activity.supportFragmentManager, "")
        }
    }
}
