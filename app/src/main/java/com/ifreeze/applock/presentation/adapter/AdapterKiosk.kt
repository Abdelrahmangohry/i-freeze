package com.ifreeze.applock.presentation.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ifreeze.applock.R
import com.ifreeze.applock.helper.getAppIconByPackageName

class AdapterKiosk(private val context: Context, private val packageNames: List<String>) :
    RecyclerView.Adapter<AdapterKiosk.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image1)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val packageName = packageNames[position]
                    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                    intent?.let {
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.kiosk_applications, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val packageName = packageNames[position]
        val icon: Drawable? = context.getAppIconByPackageName(packageName)
        holder.imageView.setImageDrawable(icon)
    }

    override fun getItemCount(): Int {
        return packageNames.size
    }
}