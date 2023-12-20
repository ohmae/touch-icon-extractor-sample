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
import net.mm2d.webclip.databinding.ItemDomainIconBinding

@SuppressLint("SetTextI18n")
class DomainIconViewHolder(
    private val binding: ItemDomainIconBinding,
    private val onMoreClick: (View, Icon) -> Unit,
) : IconViewHolder(binding.root) {
    override fun apply(icon: Icon, transparent: Boolean) {
        binding.icon.setBackgroundResource(
            selectBackground(transparent),
        )
        binding.root.tag = icon
        binding.sizes.text = icon.sizes
        binding.length.text = icon.length.toString()
        binding.type.text = icon.mimeType
        binding.url.text = icon.url
        binding.iconMore.setOnClickListener {
            onMoreClick(it, icon)
        }
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
            onMoreClick: (View, Icon) -> Unit,
        ): DomainIconViewHolder =
            DomainIconViewHolder(
                ItemDomainIconBinding.inflate(LayoutInflater.from(context), parent, false),
                onMoreClick,
            )
    }
}
