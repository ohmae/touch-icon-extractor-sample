package net.mm2d.webclip.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.li_domain_icon.view.*
import net.mm2d.touchicon.Icon
import net.mm2d.webclip.GlideApp
import net.mm2d.webclip.R

@SuppressLint("SetTextI18n")
class DomainIconViewHolder(itemView: View) : IconViewHolder(itemView) {
    private val iconImage: ImageView = itemView.icon
    private val imageSizes: TextView = itemView.image_size
    private val sizes: TextView = itemView.sizes
    private val length: TextView = itemView.length
    private val type: TextView = itemView.type
    private val url: TextView = itemView.url

    override fun apply(icon: Icon, transparent: Boolean) {
        iconImage.setBackgroundResource(
            selectBackground(
                transparent
            )
        )
        itemView.tag = icon
        sizes.text = icon.sizes
        length.text = icon.length.toString()
        type.text = icon.mimeType
        url.text = icon.url
        GlideApp.with(itemView)
            .load(icon.url)
            .override(Target.SIZE_ORIGINAL)
            .addListener(bitmapHook {
                imageSizes.text = "${it.width}x${it.height}"
            })
            .into(iconImage)
    }

    companion object {
        fun create(context: Context, parent: ViewGroup): DomainIconViewHolder =
            DomainIconViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.li_domain_icon, parent, false)
            )
    }
}
