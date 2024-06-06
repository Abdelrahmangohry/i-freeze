package com.ifreeze.applock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.example.browser.adapter.BookmarkAdapter

import com.ifreeze.applock.R
import com.ifreeze.applock.databinding.FragmentHomeBinding
import com.ifreeze.applock.presentation.activity.MainWebActivity
import com.ifreeze.applock.presentation.activity.changeTab
import com.ifreeze.applock.presentation.activity.checkForInternet


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.bind(view)

        return view
    }

    override fun onResume() {
        super.onResume()

        val mainActivityRef = requireActivity() as MainWebActivity

        MainWebActivity.tabsBtn.text = MainWebActivity.tabsList.size.toString()
        MainWebActivity.tabsList[MainWebActivity.myPager.currentItem].name = "Home"

        mainActivityRef.binding.topSearchBar.setText("")
        binding.searchView.setQuery("",false)
//        mainActivityRef.binding.webIcon.setImageResource(R.drawable.ic_search)

//        mainActivityRef.binding.refreshBtn.visibility = View.GONE

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(result: String?): Boolean {
                if(checkForInternet(requireContext()))
                    changeTab(result!!, BrowseFragment(result))
                else
                    Snackbar.make(binding.root, "Internet Not Connected\uD83D\uDE03", 3000).show()
                return true
            }
            override fun onQueryTextChange(p0: String?): Boolean = false
        })
//        mainActivityRef.binding.goBtn.setOnClickListener {
//            if(checkForInternet(requireContext()))
//                changeTab(mainActivityRef.binding.topSearchBar.text.toString(),
//                    BrowseFragment(mainActivityRef.binding.topSearchBar.text.toString())
//                )
//            else
//                Snackbar.make(binding.root, "Internet Not Connected\uD83D\uDE03", 3000).show()
//        }

        mainActivityRef.binding.topSearchBar.setOnClickListener {
            if(checkForInternet(requireContext()))
                changeTab(mainActivityRef.binding.topSearchBar.text.toString(),
                    BrowseFragment(mainActivityRef.binding.topSearchBar.text.toString())
                )
            else
                Snackbar.make(binding.root, "Internet Not Connected\uD83D\uDE03", 3000).show()
        }

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setItemViewCacheSize(5)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 5)
        binding.recyclerView.adapter = BookmarkAdapter(requireContext())

    }
}