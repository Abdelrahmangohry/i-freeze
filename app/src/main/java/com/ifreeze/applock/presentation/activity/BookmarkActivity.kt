package com.ifreeze.applock.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.browser.adapter.BookmarkAdapter
import com.ifreeze.applock.databinding.ActivityBookmarkBinding

class BookmarkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout for this activity using ViewBinding.
        val binding = ActivityBookmarkBinding.inflate(layoutInflater)

        // Set the content view to the root of the binding layout.
        setContentView(binding.root)

        // Set the cache size for the RecyclerView to improve performance by holding a limited number of view holders.
        binding.rvBookmarks.setItemViewCacheSize(5)

        // Indicate that the size of the RecyclerView's content does not change dynamically.
        binding.rvBookmarks.hasFixedSize()

        // Set the layout manager for the RecyclerView to LinearLayoutManager for a vertical list of items.
        binding.rvBookmarks.layoutManager = LinearLayoutManager(this)

        // Set the adapter for the RecyclerView, providing the activity context and specifying that it is an activity.
        binding.rvBookmarks.adapter = BookmarkAdapter(this, isActivity = true)
    }
}