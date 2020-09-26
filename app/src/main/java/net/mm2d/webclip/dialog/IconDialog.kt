/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.webclip.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mm2d.touchicon.Icon
import net.mm2d.touchicon.PageIcon
import net.mm2d.touchicon.WebAppIcon
import net.mm2d.webclip.ExtractorHolder
import net.mm2d.webclip.R

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class IconDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        val arguments = requireArguments()
        val title = arguments.getString(KEY_TITLE)!!
        val siteUrl = arguments.getString(KEY_SITE_URL)!!
        val useExtension = arguments.getBoolean(KEY_USE_EXTENSION)
        val view = activity.layoutInflater.inflate(
            R.layout.dialog_icon,
            activity.window.decorView as ViewGroup,
            false
        )
        val extractor =
            if (useExtension) ExtractorHolder.library
            else ExtractorHolder.local
        view.findViewById<TextView>(R.id.site_url).text = siteUrl
        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
        val adapter = IconListAdapter(activity, view.findViewById(R.id.transparent_switch))
        recyclerView.adapter = adapter
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                extractor.fromPage(siteUrl, true)
            }.let { adapter.add(it) }
            progressBar.visibility = View.GONE
        }
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                extractor.listFromDomain(siteUrl, true, listOf("120x120"))
            }.let { adapter.add(it) }
            progressBar.visibility = View.GONE
        }
        return AlertDialog.Builder(activity)
            .setTitle(title)
            .setView(view)
            .create()
    }

    private inner class IconListAdapter(
        private val context: Context,
        private val transparentSwitch: CompoundButton
    ) : RecyclerView.Adapter<IconViewHolder>() {
        private val list: MutableList<Icon> = mutableListOf()

        init {
            transparentSwitch.setOnCheckedChangeListener { _, _ ->
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, type: Int): IconViewHolder =
            when (type) {
                0 -> PageIconViewHolder.create(context, parent)
                1 -> WebAppIconViewHolder.create(context, parent)
                else -> DomainIconViewHolder.create(context, parent)
            }

        fun add(icons: List<Icon>) {
            val positionStart = list.size
            list.addAll(icons)
            notifyItemRangeInserted(positionStart, icons.size)
        }

        override fun getItemViewType(position: Int): Int = when (list[position]) {
            is PageIcon -> 0
            is WebAppIcon -> 1
            else -> 2
        }

        override fun getItemCount(): Int = list.size

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
            holder.apply(list[position], transparentSwitch.isChecked)
        }
    }

    companion object {
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SITE_URL = "KEY_SITE_URL"
        private const val KEY_USE_EXTENSION = "KEY_USE_EXTENSION"

        fun show(
            activity: FragmentActivity,
            title: String,
            siteUrl: String,
            useExtension: Boolean
        ) {
            IconDialog().also {
                it.arguments =
                    makeArgument(title, siteUrl, useExtension)
            }.show(activity.supportFragmentManager, "")
        }

        private fun makeArgument(title: String, siteUrl: String, useExtension: Boolean): Bundle =
            Bundle().also {
                it.putString(KEY_TITLE, title)
                it.putString(KEY_SITE_URL, siteUrl)
                it.putBoolean(KEY_USE_EXTENSION, useExtension)
            }
    }
}
