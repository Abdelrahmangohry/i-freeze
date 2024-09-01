package com.ifreeze.applock.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ifreeze.applock.R

/**
 * AffectedFiles is a RecyclerView.Adapter that displays a list of strings.
 * It is typically used to display a list of affected files or links.
 *
 * @param stringList The list of strings to be displayed in the RecyclerView.
 */
class AffectedFiles (private val stringList: List<String>) :
    RecyclerView.Adapter<AffectedFiles.StringViewHolder>() {

    /**
     * Called when RecyclerView needs a new ViewHolder to represent an item.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new view (unused here).
     * @return A new StringViewHolder that holds the view for each item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StringViewHolder {
        // Inflate the layout for a single item in the RecyclerView
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.affected_files_recycler, parent, false)
        return StringViewHolder(view)
    }

    /**
     * Binds the data (a string from the list) to the ViewHolder for a specific position.
     *
     * @param holder The StringViewHolder in which the data should be bound.
     * @param position The position of the item in the RecyclerView.
     */
    override fun onBindViewHolder(holder: StringViewHolder, position: Int) {
        // Bind the string data to the ViewHolder
        holder.bind(stringList[position])
    }

    override fun getItemCount(): Int {
        return stringList.size
    }

    /**
     * StringViewHolder is a RecyclerView.ViewHolder that holds the view for a single string item.
     *
     * @param itemView The view representing a single item in the RecyclerView.
     */
    class StringViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TextView to display the string item
        private val textView: TextView = itemView.findViewById(R.id.textLinks)

        /**
         * Binds the string data to the TextView.
         *
         * @param item The string data to be displayed in the TextView.
         */
        fun bind(item: String) {
            textView.text = item
        }
    }
}