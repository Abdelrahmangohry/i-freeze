package com.ifreeze.applock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ifreeze.applock.presentation.adapter.BookmarkAdapter
import com.ifreeze.applock.R
import com.ifreeze.applock.databinding.FragmentHomeBinding
import com.ifreeze.applock.presentation.activity.MainWebActivity
import com.ifreeze.applock.presentation.activity.changeTab
import com.ifreeze.applock.presentation.activity.checkForInternet


class HomeFragment : Fragment() {
    // Binding variable to access views in the fragment
    private lateinit var binding: FragmentHomeBinding

    // Method to create and return the view hierarchy associated with the fragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // Bind the views to the binding variable
        binding = FragmentHomeBinding.bind(view)
        // Return the root view
        return view
    }

    // Method called when the fragment becomes visible to the user
    override fun onResume() {
        super.onResume()
        // Reference to the activity containing this fragment
        val mainActivityRef = requireActivity() as MainWebActivity
        // Update the tabs button text with the number of open tabs
        MainWebActivity.tabsBtn.text = MainWebActivity.tabsList.size.toString()
        // Set the name of the current tab to "Home"
        MainWebActivity.tabsList[MainWebActivity.myPager.currentItem].name = "Home"
        // Clear the text in the top search bar in the main activity
        mainActivityRef.binding.topSearchBar.setText("")
        // Clear the query in the SearchView within this fragment
        binding.searchView.setQuery("",false)

        // Set a listener for query text submission in the SearchView
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            // Handle search submission
            override fun onQueryTextSubmit(result: String?): Boolean {
                // If the internet is connected, open the search result in a new tab
                if(checkForInternet(requireContext()))
                    changeTab(result!!, BrowseFragment.newInstance(result))
                else
                // Show a Snackbar if the internet is not connected
                    Snackbar.make(binding.root, "Internet Not Connected\uD83D\uDE03", 3000).show()
                return true
            }
            // Handle query text change (not used here)
            override fun onQueryTextChange(p0: String?): Boolean = false
        })
// Set a click listener for the top search bar in the main activity
        mainActivityRef.binding.topSearchBar.setOnClickListener {
            // If the internet is connected, open the search result in a new tab
            if(checkForInternet(requireContext()))
                changeTab(mainActivityRef.binding.topSearchBar.text.toString(),
                    BrowseFragment.newInstance(mainActivityRef.binding.topSearchBar.text.toString())
                )
            else
            // Show a Snackbar if the internet is not connected
                Snackbar.make(binding.root, "Internet Not Connected\uD83D\uDE03", 3000).show()
        }
        // Configure the RecyclerView for displaying bookmarks
        binding.recyclerView.setHasFixedSize(true) // Set fixed size for performance optimization
        binding.recyclerView.setItemViewCacheSize(5)  // Cache the last 5 views
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 5) // Set grid layout with 5 columns
        binding.recyclerView.adapter = BookmarkAdapter(requireContext()) // Set the adapter for the RecyclerView

    }
}