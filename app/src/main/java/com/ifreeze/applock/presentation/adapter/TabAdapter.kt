package com.ifreeze.applock.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ifreeze.applock.databinding.TabBinding

import com.ifreeze.applock.presentation.activity.MainWebActivity

/**
 * TabAdapter is a RecyclerView.Adapter that handles the display and interaction
 * with a list of tabs within an AlertDialog.
 *
 * @param context The context in which the adapter is used.
 * @param dialog The AlertDialog that contains the RecyclerView.
 */
class TabAdapter(private val context: Context, private val dialog: AlertDialog): RecyclerView.Adapter<TabAdapter.MyHolder>() {

    /**
     * MyHolder is a RecyclerView.ViewHolder that holds the views for a single tab item.
     *
     * @param binding The binding object for the tab item layout.
     */
    class MyHolder(binding: TabBinding) :RecyclerView.ViewHolder(binding.root) {
        // Button to cancel or remove the tab
        val cancelBtn = binding.cancelBtn
        // TextView to display the tab's name
        val name = binding.tabName
        // Root view of the tab item layout
        val root = binding.root
    }


    /**
     * Called when RecyclerView needs a new ViewHolder to represent an item.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new view (unused here).
     * @return A new MyHolder that holds the view for each tab item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        // Inflate the tab item layout and return a new MyHolder
        return MyHolder(TabBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    /**
     * Binds the data (tab details) to the ViewHolder for a specific position.
     *
     * @param holder The MyHolder in which the data should be bound.
     * @param position The position of the item in the RecyclerView.
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        // Set the tab name in the TextView
        holder.name.text = MainWebActivity.tabsList[position].name
        holder.root.setOnClickListener {
            MainWebActivity.myPager.currentItem = position
            dialog.dismiss() // Close the dialog when a tab is selected
        }
// Set an OnClickListener on the cancel button to remove the tab
        holder.cancelBtn.setOnClickListener {
            // Show a Snackbar if there's only one tab left or if trying to remove the currently selected tab
            if(MainWebActivity.tabsList.size == 1 || position == MainWebActivity.myPager.currentItem)
                Snackbar.make(MainWebActivity.myPager, "Can't Remove this tab", 3000).show()
            else{
                // Remove the tab from the list and notify the adapter of the change
                MainWebActivity.tabsList.removeAt(position)
                notifyDataSetChanged()
                // Notify the pager adapter that the item was removed
                MainWebActivity.myPager.adapter?.notifyItemRemoved(position)
            }


        }
    }

    /**
     * Returns the total number of items in the RecyclerView.
     *
     * @return The size of the tabs list.
     */
    override fun getItemCount(): Int {
        return MainWebActivity.tabsList.size
    }
}