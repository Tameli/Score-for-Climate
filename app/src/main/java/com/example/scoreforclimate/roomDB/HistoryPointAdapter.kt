package com.example.scoreforclimate.roomDB

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.scoreforclimate.R

class HistoryPointAdapter (private val layoutInflater: LayoutInflater,
                           private val historyList: Array<History>,
                           @param:LayoutRes private val rowLayout: Int):
    RecyclerView.Adapter<HistoryPointAdapter.ViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = layoutInflater.inflate(rowLayout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scoreEvent = historyList[position]
        holder.fullName.text = scoreEvent.listActions.toString()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val fullName = view.findViewById<View>(R.id.full_name_tv) as TextView
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

}