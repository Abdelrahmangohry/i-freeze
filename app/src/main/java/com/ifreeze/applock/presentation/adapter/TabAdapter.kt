package com.example.browser.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ifreeze.applock.databinding.TabBinding

import com.ifreeze.applock.presentation.activity.MainWebActivity

class TabAdapter(private val context: Context, private val dialog: AlertDialog): RecyclerView.Adapter<TabAdapter.MyHolder>() {

    class MyHolder(binding: TabBinding) :RecyclerView.ViewHolder(binding.root) {
        val cancelBtn = binding.cancelBtn
        val name = binding.tabName
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(TabBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = MainWebActivity.tabsList[position].name
        holder.root.setOnClickListener {
            MainWebActivity.myPager.currentItem = position
            dialog.dismiss()
        }

        holder.cancelBtn.setOnClickListener {
            if(MainWebActivity.tabsList.size == 1 || position == MainWebActivity.myPager.currentItem)
                Snackbar.make(MainWebActivity.myPager, "Can't Remove this tab", 3000).show()
            else{
                MainWebActivity.tabsList.removeAt(position)
                notifyDataSetChanged()
                MainWebActivity.myPager.adapter?.notifyItemRemoved(position)
            }


        }
    }

    override fun getItemCount(): Int {
        return MainWebActivity.tabsList.size
    }
}