package com.example.autempsdonne

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class VolunteerAdapter(private val context: Context, private var lvolunteers: MutableList<Volunteer>) : BaseAdapter() {

    override fun getCount(): Int {
        return this.lvolunteers.size
    }

    override fun getItem(position: Int): Any {
        return this.lvolunteers[position]
    }

    override fun getItemId(position: Int): Long {
        return this.lvolunteers[position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = convertView ?: LayoutInflater.from(this.context).inflate(R.layout.row_user, null)

        val currentVolunteer = getItem(position) as Volunteer

        v.findViewById<TextView>(R.id.tv_user_username).text = currentVolunteer.username
        v.findViewById<TextView>(R.id.tv_user_email).text = currentVolunteer.email

        return v
    }
}