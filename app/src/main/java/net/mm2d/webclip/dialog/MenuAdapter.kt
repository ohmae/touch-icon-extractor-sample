package net.mm2d.webclip.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import net.mm2d.webclip.databinding.ItemMenuBinding

class MenuAdapter(context: Context) : BaseAdapter() {
    private val inflater = LayoutInflater.from(context)
    private val list: MutableList<Pair<Int, Int>> = mutableListOf()

    fun add(item: Pair<Int, Int>) {
        list.add(item)
    }

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Pair<Int, Int> = list[position]

    override fun getItemId(position: Int): Long = list[position].second.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        createViewFromResource(inflater, position, convertView, parent)

    private fun createViewFromResource(
        inflater: LayoutInflater,
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View = (
        convertView?.let { ItemMenuBinding.bind(it) }
            ?: ItemMenuBinding.inflate(inflater, parent, false)
        ).also {
        val item = getItem(position)
        it.icon.setImageResource(item.first)
        it.text.setText(item.second)
    }.root
}
