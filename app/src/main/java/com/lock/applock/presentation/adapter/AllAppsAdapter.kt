package com.lock.applock.presentation.adapter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lock.applock.R
import com.lock.applock.databinding.AllAdapterDesignBinding
import com.lock.data.model.AppsModel

class AllAppsAdapter : RecyclerView.Adapter<AllAppsAdapter.ViewHolder>() {
    private val list = ArrayList<AppsModel>()
    var lockedApps = ArrayList<String>()
    var onItemClicked: ((ArrayList<String>) -> Unit)? = null


    inner class ViewHolder(private val binding: AllAdapterDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AppsModel) {
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            val filter = ColorMatrixColorFilter(matrix)
            binding.apply {
//                appicon.setImageDrawable(data.icon)
                appname.text = data.appName
//                appstatus.setImageResource(if (data.status == 0) R.drawable.locked_icon else 0)
//                appicon.setOnClickListener {
//                    if (data.status == 0) {
//                        data.status=1
//                        appstatus.setImageResource(R.drawable.locked_icon)
//                        appicon.setColorFilter(filter)
//                        appicon.clearColorFilter()
//                    } else {
//                        data.status = 0
//                        appstatus.setImageResource(0)
//                        appicon.setColorFilter(filter)
//                    }
//                    lockedApps.add(data.appName)
//                    Log.d("islam", "bind :  ${lockedApps}")
//                    onItemClicked?.invoke(lockedApps)
//                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AllAdapterDesignBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list.get(position)?.let { holder.bind(it) }
    }

    fun addAll(data: List<AppsModel>) {
        list.apply {
            clear()
            addAll(data)

        }
        notifyDataSetChanged()
    }
}