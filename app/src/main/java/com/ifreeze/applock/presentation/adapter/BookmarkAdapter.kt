package com.example.browser.adapter

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

class BookmarkAdapter(private val context: Context, private val isActivity: Boolean = false): RecyclerView.Adapter<BookmarkAdapter.MyHolder>() {

    private val colors = context.resources.getIntArray(R.array.myColors)

    class MyHolder(binding: BookmarkViewBinding? = null, bindingL: LongBookmarkViewBinding? = null)
        :RecyclerView.ViewHolder((binding?.root ?: bindingL?.root)!!) {
        val image = (binding?.bookmarkIcon ?: bindingL?.bookmarkIcon)!!
        val name = (binding?.bookmarkName ?: bindingL?.bookmarkName)!!
        val root = (binding?.root ?: bindingL?.root)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        if(isActivity)
            return MyHolder(bindingL = LongBookmarkViewBinding.inflate(LayoutInflater.from(context), parent, false))
        return MyHolder(binding = BookmarkViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        try {
            val icon = BitmapFactory.decodeByteArray(MainWebActivity.bookmarkList[position].image, 0,
                MainWebActivity.bookmarkList[position].image!!.size)
            holder.image.background = icon.toDrawable(context.resources)
        }catch (e: Exception){
            holder.image.setBackgroundColor(colors[(colors.indices).random()])
            holder.image.text = MainWebActivity.bookmarkList[position].name[0].toString()
        }
        holder.name.text = MainWebActivity.bookmarkList[position].name

        holder.root.setOnClickListener{
            when{
                checkForInternet(context) -> {
                    changeTab(MainWebActivity.bookmarkList[position].name,
                BrowseFragment(urlNew = MainWebActivity.bookmarkList[position].url)
                    )
                    if(isActivity) (context as Activity).finish()
                }
                else -> Snackbar.make(holder.root, "Internet Not Connected\uD83D\uDE03", 3000).show()
            }

        }
    }

    override fun getItemCount(): Int {
        return MainWebActivity.bookmarkList.size
    }
}