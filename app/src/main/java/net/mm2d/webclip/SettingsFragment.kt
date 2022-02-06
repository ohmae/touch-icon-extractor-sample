/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.webclip

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import net.mm2d.webclip.settings.SettingsRepository
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findPreference<Preference>("VERSION_NUMBER")?.summary = BuildConfig.VERSION_NAME
        findPreference<SwitchPreferenceCompat>("USE_EXTENSION")?.let { switch ->
            viewLifecycleOwner.lifecycleScope.launch {
                settingsRepository.userSettingsRepository.flow.take(1).collectLatest {
                    switch.isChecked = it.useExtension
                }
            }
            switch.setOnPreferenceChangeListener { _, newValue ->
                viewLifecycleOwner.lifecycleScope.launch {
                    settingsRepository.userSettingsRepository.updateUseExtension(newValue as Boolean)
                }
                true
            }
        }
    }
}
