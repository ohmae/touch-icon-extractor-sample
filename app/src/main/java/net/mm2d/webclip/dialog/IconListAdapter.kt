package net.mm2d.webclip.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import net.mm2d.touchicon.Icon
import net.mm2d.touchicon.PageIcon
import net.mm2d.touchicon.WebAppIcon

@SuppressLint("NotifyDataSetChanged")
class IconListAdapter(
    private val context: Context,
    private val transparentSwitch: CompoundButton,
    private val onMoreClick: (View, Icon) -> Unit,
) : RecyclerView.Adapter<IconViewHolder>() {
    private val list: MutableList<Icon> = mutableListOf()

    init {
        transparentSwitch.setOnCheckedChangeListener { _, _ ->
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): IconViewHolder =
        when (type) {
            0 -> PageIconViewHolder.create(context, parent, onMoreClick)
            1 -> WebAppIconViewHolder.create(context, parent, onMoreClick)
            else -> DomainIconViewHolder.create(context, parent, onMoreClick)
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
