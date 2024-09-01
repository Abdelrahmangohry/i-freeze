package com.ifreeze.applock.presentation.adapter

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ifreeze.applock.fragment.BrowseFragment
import com.ifreeze.applock.R
import com.ifreeze.applock.databinding.BookmarkViewBinding
import com.ifreeze.applock.databinding.LongBookmarkViewBinding
import com.ifreeze.applock.presentation.activity.MainWebActivity
import com.ifreeze.applock.presentation.activity.changeTab
import com.ifreeze.applock.presentation.activity.checkForInternet

/**
 * BookmarkAdapter is a RecyclerView.Adapter that displays a list of bookmarks.
 * It supports two types of views: a regular view and a long view, depending on the context.
 *
 * @param context The context in which the adapter is used.
 * @param isActivity A boolean flag indicating whether the adapter is used in an activity.
 *                   If true, it inflates a different layout for the items.
 */
class BookmarkAdapter(private val context: Context, private val isActivity: Boolean = false): RecyclerView.Adapter<BookmarkAdapter.MyHolder>() {
    // Array of colors fetched from the resources, used to set a random background color if an icon is not available

    private val colors = context.resources.getIntArray(R.array.myColors)

    /**
     * MyHolder is a RecyclerView.ViewHolder that holds the views for a single bookmark item.
     *
     * @param binding Optional binding for the regular bookmark view.
     * @param bindingL Optional binding for the long bookmark view.
     */
    class MyHolder(binding: BookmarkViewBinding? = null, bindingL: LongBookmarkViewBinding? = null)
        :RecyclerView.ViewHolder((binding?.root ?: bindingL?.root)!!) {
        // ImageView to display the bookmark icon
        val image = (binding?.bookmarkIcon ?: bindingL?.bookmarkIcon)!!
        // TextView to display the bookmark name
        val name = (binding?.bookmarkName ?: bindingL?.bookmarkName)!!
        // Root view of the bookmark item layout
        val root = (binding?.root ?: bindingL?.root)!!
    }

    /**
     * Called when RecyclerView needs a new ViewHolder to represent an item.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new view (unused here).
     * @return A new MyHolder that holds the view for each bookmark item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        // Inflate the appropriate layout based on whether the adapter is used in an activity
        if(isActivity)
            return MyHolder(bindingL = LongBookmarkViewBinding.inflate(LayoutInflater.from(context), parent, false))
        return MyHolder(binding = BookmarkViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    /**
     * Binds the data (bookmark details) to the ViewHolder for a specific position.
     *
     * @param holder The MyHolder in which the data should be bound.
     * @param position The position of the item in the RecyclerView.
     */
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        try {
            // Decode the bookmark image and set it as the background of the image view
            val icon = BitmapFactory.decodeByteArray(MainWebActivity.bookmarkList[position].image, 0,
                MainWebActivity.bookmarkList[position].image!!.size)
            holder.image.background = icon.toDrawable(context.resources)
        }catch (e: Exception){
            // If there's an error, set a random background color and use the first letter of the bookmark name as text
            holder.image.setBackgroundColor(colors[(colors.indices).random()])
            holder.image.text = MainWebActivity.bookmarkList[position].name[0].toString()
        }
        // Set the bookmark name in the TextView
        holder.name.text = MainWebActivity.bookmarkList[position].name
        // Set an OnClickListener on the root view to handle bookmark clicks
        holder.root.setOnClickListener{
            when{
                checkForInternet(context) -> {
                    // If the internet is connected, open the bookmark's URL in a new tab
                    changeTab(MainWebActivity.bookmarkList[position].name,
                BrowseFragment.newInstance(MainWebActivity.bookmarkList[position].url)
                    )
                    // If the adapter is used in an activity, finish the activity after opening the bookmark

                    if(isActivity) (context as Activity).finish()
                }
                // Show a Snackbar message if the internet is not connected
                else -> Snackbar.make(holder.root, "Internet Not Connected\uD83D\uDE03", 3000).show()
            }

        }
    }

    /**
     * Returns the total number of items in the RecyclerView.
     *
     * @return The size of the bookmark list.
     */
    override fun getItemCount(): Int {
        return MainWebActivity.bookmarkList.size
    }
}