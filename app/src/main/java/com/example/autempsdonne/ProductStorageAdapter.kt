package com.example.autempsdonne

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ProductStorageAdapter (
    private val context: Context,
    private var lstorages: MutableList<ProductStorage>
) : BaseAdapter() {
    override fun getCount(): Int {
        return this.lstorages.size
    }

    override fun getItem(position: Int): Any {
        return this.lstorages[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = convertView ?: LayoutInflater.from(this.context).inflate(R.layout.row_storage, null)

        val currentStorage = getItem(position) as ProductStorage

        val reference = "Reference : " + currentStorage.reference
        val quantity = context.getString(R.string.QuantityDots) + currentStorage.quantity.toString()

        v.findViewById<TextView>(R.id.tv_storage_ref).text = reference
        v.findViewById<TextView>(R.id.tv_storage_quantity).text = quantity

        return v
    }

}