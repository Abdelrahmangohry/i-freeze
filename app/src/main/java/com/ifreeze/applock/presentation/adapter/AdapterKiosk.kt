package com.ifreeze.applock.presentation.adapter

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.wrappers.Wrappers.packageManager
import com.ifreeze.applock.R
import com.ifreeze.applock.helper.getAppIconByPackageName

/**
 * AdapterKiosk is a RecyclerView.Adapter that displays a list of applications based on their package names.
 * It allows users to launch applications by clicking on the corresponding item in the list.
 *
 * @param context The context in which the adapter is used.
 * @param packageNames The list of package names representing the applications to be displayed.
 */
class AdapterKiosk(private val context: Context, private val packageNames: List<String>) :
    RecyclerView.Adapter<AdapterKiosk.ViewHolder>() {

    /**
     * ViewHolder class represents the UI elements for each item in the RecyclerView.
     *
     * @param view The view representing a single item in the RecyclerView.
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // ImageView to display the application icon
        val imageView: ImageView = view.findViewById(R.id.image1)
        // TextView to display the application name
        val textView: TextView = view.findViewById(R.id.textview)

        init {
            // Set an OnClickListener to launch the application when the item is clicked
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

    /**
     * Creates a new ViewHolder to represent a single item in the RecyclerView.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new view (unused here).
     * @return A new ViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for a single item in the RecyclerView
        val view = LayoutInflater.from(parent.context).inflate(R.layout.kiosk_applications, parent, false)
        return ViewHolder(view)
    }

    /**
     * Binds the data (application name and icon) to the ViewHolder for a specific position.
     *
     * @param holder The ViewHolder in which the data should be bound.
     * @param position The position of the item in the RecyclerView.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val packageName = packageNames[position]
        // Retrieve and set the application name
        val appName = getAppName(packageName)
                .toString()
        // Retrieve and set the application icon
        val icon: Drawable? = context.getAppIconByPackageName(packageName)
        holder.imageView.setImageDrawable(icon)
        holder.textView.text = appName
    }

    /**
     * Retrieves the application name based on the package name.
     *
     * @param packageName The package name of the application.
     * @return The application name or a default text if the name cannot be retrieved.
     */
    private fun getAppName(packageName: String): CharSequence {
        val packageManager = context.packageManager
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            // Handle exception (e.g., return default text)
            "App Name Unknown"
        }
    }

    /**
     * Returns the total number of items in the RecyclerView.
     *
     * @return The size of the packageNames list.
     */
    override fun getItemCount(): Int {
        return packageNames.size
    }
}