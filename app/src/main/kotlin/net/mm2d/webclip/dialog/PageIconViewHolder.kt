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
import coil3.asDrawable
import coil3.imageLoader
import coil3.request.ImageRequest
import net.mm2d.touchicon.Icon
import net.mm2d.webclip.databinding.ItemPageIconBinding

@SuppressLint("SetTextI18n")
class PageIconViewHolder(
    private val binding: ItemPageIconBinding,
    private val onMoreClick: (View, Icon) -> Unit,
) : IconViewHolder(binding.root) {
    override fun apply(
        icon: Icon,
        transparent: Boolean,
    ) {
        binding.icon.setBackgroundResource(
            selectBackground(transparent),
        )
        itemView.tag = icon
        binding.sizes.text = icon.sizes
        binding.rel.text = icon.rel.value
        binding.type.text = icon.mimeType
        binding.url.text = icon.url
        binding.iconMore.setOnClickListener {
            onMoreClick(it, icon)
        }
        val size = icon.inferSize()
        val inferSize = if (size.isValid()) {
            "(${size.width}x${size.height})"
        } else {
            "(uncertain)"
        }
        binding.imageSize.text = inferSize

        val context = binding.root.context
        val request = ImageRequest.Builder(context)
            .data(icon.url)
            .target {
                binding.imageSize.text = "${it.width}x${it.height}"
                binding.icon.setImageDrawable(it.asDrawable(context.resources))
            }
            .build()
        context.imageLoader.enqueue(request)
    }

    companion object {
        fun create(
            context: Context,
            parent: ViewGroup,
            onMoreClick: (View, Icon) -> Unit,
        ): PageIconViewHolder =
            PageIconViewHolder(
                ItemPageIconBinding.inflate(LayoutInflater.from(context), parent, false),
                onMoreClick,
            )
    }
}
