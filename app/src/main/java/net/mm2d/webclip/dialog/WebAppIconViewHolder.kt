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
import com.bumptech.glide.request.target.Target
import net.mm2d.touchicon.Icon
import net.mm2d.touchicon.WebAppIcon
import net.mm2d.webclip.GlideApp
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
        GlideApp.with(itemView)
            .load(icon.url)
            .override(Target.SIZE_ORIGINAL)
            .addListener(bitmapHook {
                binding.imageSize.text = "${it.width}x${it.height} $inferSize"
            })
            .into(binding.icon)
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
