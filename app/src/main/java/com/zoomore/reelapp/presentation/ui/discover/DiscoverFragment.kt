package com.zoomore.reelapp.presentation.ui.discover

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentDiscoverBinding
import com.zoomore.reelapp.utils.SystemBarColors
import com.zoomore.reelapp.utils.ViewUtils
import com.zoomore.reelapp.utils.architecture.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import timber.log.Timber

class DiscoverFragment : BaseFragment(R.layout.fragment_discover) {

    private lateinit var binding: FragmentDiscoverBinding

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
//    override val viewModel by viewModels<DiscoverViewModel>()

    override fun setUpLayout() {
        binding = FragmentDiscoverBinding.bind(requireView())

        binding.searchRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter
        }
    }

    override fun setUpLiveData() {
       /* viewModel.listOfPopularTags.observe(viewLifecycleOwner) { listOfPopularTags ->
            listOfPopularTags?.forEach { popularTag ->
                val tagGroup = DiscoverGroup(
                    coroutineScope = lifecycleScope,
                    tag = popularTag,
                    getVideoThumbnail = { viewModel.getVideoThumbnail(requireContext(), it) },
                    fetchVideos = { viewModel.fetchVideos(popularTag) }
                )

                groupAdapter.add(tagGroup)
            }
        }*/
    }

    override fun setUpClickListeners() {
        binding.searchInput.setOnClickListener {
            Timber.d("searchInput.setOnClickListener called")
            findNavController().navigate(
                DiscoverFragmentDirections.actionDiscoverFragmentToSearchPageFragment()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        ViewUtils.changeSystemBars(activity, SystemBarColors.WHITE)
    }
}