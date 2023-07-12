/*
 * Copyright (c) 2021 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.webclip.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.imageLoader
import coil.request.ImageRequest
import net.mm2d.touchicon.Icon
import net.mm2d.touchicon.WebAppIcon
import net.mm2d.webclip.databinding.ItemWebAppIconBinding

@SuppressLint("SetTextI18n")
class WebAppIconViewHolder(
    private val binding: ItemWebAppIconBinding,
    private val onMoreClick: (View, Icon) -> Unit,
) : IconViewHolder(binding.root) {
    override fun apply(icon: Icon, transparent: Boolean) {
        binding.icon.setBackgroundResource(
            selectBackground(transparent)
        )
        binding.root.tag = icon
        binding.sizes.text = icon.sizes
        binding.type.text = icon.mimeType
        binding.url.text = icon.url
        binding.iconMore.setOnClickListener {
            onMoreClick(it, icon)
        }
        if (icon is WebAppIcon) {
            binding.density.text = icon.density
        }
        val size = icon.inferSize()
        val inferSize = if (size.width > 0 && size.height > 0) {
            "(${size.width}x${size.height})"
        } else {
            "(uncertain)"
        }
        binding.imageSize.text = inferSize

        val context = binding.root.context
        val request = ImageRequest.Builder(context)
            .data(icon.url)
            .target {
                binding.imageSize.text = "${it.intrinsicWidth}x${it.intrinsicHeight}"
                binding.icon.setImageDrawable(it)
            }
            .build()
        context.imageLoader.enqueue(request)
    }

    companion object {
        fun create(
            context: Context,
            parent: ViewGroup,
            onMoreClick: (View, Icon) -> Unit
        ): WebAppIconViewHolder =
            WebAppIconViewHolder(
                ItemWebAppIconBinding.inflate(LayoutInflater.from(context), parent, false),
                onMoreClick,
            )
    }
}
