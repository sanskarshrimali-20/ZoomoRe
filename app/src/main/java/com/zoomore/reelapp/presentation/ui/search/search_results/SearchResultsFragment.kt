package com.zoomore.reelapp.presentation.ui.search.search_results

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentSearchResultsBinding

class SearchResultsFragment : Fragment(R.layout.fragment_search_results) {

    private lateinit var binding: FragmentSearchResultsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchResultsBinding.bind(view)


    }

}