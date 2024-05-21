package com.ifreeze.applock.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ifreeze.applock.R

class AffectedFiles (private val stringList: List<String>) :
    RecyclerView.Adapter<AffectedFiles.StringViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StringViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.affected_files_recycler, parent, false)
        return StringViewHolder(view)
    }

    override fun onBindViewHolder(holder: StringViewHolder, position: Int) {
        holder.bind(stringList[position])
    }

    override fun getItemCount(): Int {
        return stringList.size
    }

    class StringViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textLinks)

        fun bind(item: String) {
            textView.text = item
        }
    }
}