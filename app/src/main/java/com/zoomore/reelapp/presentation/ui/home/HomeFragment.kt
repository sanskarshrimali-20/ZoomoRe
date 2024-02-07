package com.zoomore.reelapp.presentation.ui.home

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.zoomore.reelapp.models.video.RemoteVideo
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentHomeBinding
import com.zoomore.reelapp.presentation.ui.home.large_video_group.LargeVideoGroup
import com.zoomore.reelapp.utils.BottomNavViewUtils
import com.zoomore.reelapp.utils.BottomNavViewUtils.changeNavBarColor
import com.zoomore.reelapp.utils.BottomNavViewUtils.showBottomNavBar
import com.zoomore.reelapp.utils.SystemBarColors
import com.zoomore.reelapp.utils.ViewUtils.changeSystemBars
import com.zoomore.reelapp.utils.ViewUtils.hideStatusBar
import com.zoomore.reelapp.utils.architecture.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import timber.log.Timber

class HomeFragment : BaseFragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override val viewModel by viewModels<HomeViewModel>()
    private val snapHelper = PagerSnapHelper()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    override fun setUpLayout() {
        binding = FragmentHomeBinding.bind(requireView())
    }

    override fun setUpLiveData() {
        viewModel.listOfRemoteVideo.observe(viewLifecycleOwner) { listOfRemoteVideo ->
            Timber.d("listOfRemoteVideo is List<RemoteVideo> is ${listOfRemoteVideo is List<RemoteVideo>}")
            val listOfGroup = listOfRemoteVideo?.map { remoteVideo ->
                val largeVideoGroup = LargeVideoGroup(
                    scope = lifecycleScope,
                    lifecycleOwner = viewLifecycleOwner,
                    userRepo = viewModel.userRepo,
                    commentRepo = viewModel.commentRepo,
                    videosRepo = viewModel.videosRepo,
                    remoteVideo = remoteVideo,
                    onPersonIconClicked = { uid ->
                        findNavController().navigate(
                            HomeFragmentDirections
                                .actionHomeFragmentToProfileWithAccountFragment(uid)
                        )
                    },
                    onVideoEnded = {
                        scrollDownToNextVideo(groupAdapter.getAdapterPosition(it))
                    },
                    onCommentVisibilityChanged = { isVisible ->
                        if (isVisible) BottomNavViewUtils.hideBottomNavBar(activity)
                        else showBottomNavBar(activity)
                    }
                )

                largeVideoGroup
            } ?: listOf()

            groupAdapter.addAll(listOfGroup)
            Timber.d("groupAdapter.size is ${groupAdapter.itemCount}")
        }
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = groupAdapter
            it.addOnScrollListener(homeScrollListener)
            snapHelper.attachToRecyclerView(it)
        }
    }

    private fun scrollDownToNextVideo(currentPosition: Int) {
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        layoutManager.scrollToPosition(currentPosition + 1)
    }

    private val homeScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                val llm = (recyclerView.layoutManager as LinearLayoutManager)
                val lastVisiblePosition = llm.findLastVisibleItemPosition()

                // Checks if we are reaching towards the end of the list
                if (groupAdapter.itemCount - lastVisiblePosition <= 5) {
                    viewModel.fetchVideos()
                    Timber.d("groupAdapter.itemCount is ${groupAdapter.itemCount} and lastVisiblePosition is $lastVisiblePosition")
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        Timber.d("Lifecycle Callbacks: onStart() called")
    }

    // TODO: And this too
    override fun onResume() {
        super.onResume()
        showBottomNavBar(activity)
        changeNavBarColor(activity, SystemBarColors.DARK)
        changeSystemBars(activity, SystemBarColors.DARK)
        hideStatusBar(requireActivity())
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        Timber.d("Lifecycle Callbacks: onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("Lifecycle Callbacks: onPause() called")
    }
    override fun onStop() {
        super.onStop()
        Timber.d("Lifecycle Callbacks: onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Lifecycle Callbacks: onDestroy() called")
    }
}