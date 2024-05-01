package com.example.autempsdonne

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class UserAdapter(val context: Context, var lusers: MutableList<User>) : BaseAdapter() {

    override fun getCount(): Int {
        return this.lusers.size
    }

    override fun getItem(position: Int): Any {
        return this.lusers[position]
    }

    override fun getItemId(position: Int): Long {
        return this.lusers[position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = convertView ?: LayoutInflater.from(this.context).inflate(R.layout.row_user, null)

        val currentUser = getItem(position) as User

        v.findViewById<TextView>(R.id.tv_user_username).text = currentUser.username
        v.findViewById<TextView>(R.id.tv_user_email).text = currentUser.email

        return v
    }
}