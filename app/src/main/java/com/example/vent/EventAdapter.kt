package com.example.vent

import Model
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.BaseAdapter

class EventAdapter(private val context: Context, private val events: List<Model>) : BaseAdapter() {

    override fun getCount(): Int = events.size

    override fun getItem(position: Int): Any = events[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val programName = view.findViewById<TextView>(R.id.programName)
        val programType = view.findViewById<TextView>(R.id.programType)
        val startDate = view.findViewById<TextView>(R.id.startDate)

        val event = events[position]

        programName.text = event.name
        programType.text = event.type
        startDate.text = event.startDate

        return view
    }
}
