package com.zoomore.reelapp.presentation.ui.search.search_page

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zoomore.reelapp.presentation.ui.search.search_page.group.SmallSuggestionGroup
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentSearchPageBinding
import com.zoomore.reelapp.utils.KeyboardUtils
import com.zoomore.reelapp.utils.architecture.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class SearchPageFragment : BaseFragment(R.layout.fragment_search_page) {

    private lateinit var binding: FragmentSearchPageBinding

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    override val viewModel by viewModels<SearchPageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        KeyboardUtils.show(binding.searchInput)
    }

    override fun setUpLayout() {
        binding = FragmentSearchPageBinding.bind(requireView()).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
        }
        binding.suggestionsRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    override fun setUpLiveData() {
        viewModel.liveSuggestions.observe(viewLifecycleOwner) { liveSuggestions ->
            groupAdapter.clear()

            liveSuggestions?.forEach { suggestion ->
                val smallSuggestionGroup = SmallSuggestionGroup(suggestion) { searchNameToInsert ->
                    binding.searchInput.setText(searchNameToInsert)
                }
                groupAdapter.add(smallSuggestionGroup)
            }
        }
    }
}