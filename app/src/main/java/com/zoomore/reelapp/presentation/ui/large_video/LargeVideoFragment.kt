package com.zoomore.reelapp.presentation.ui.large_video

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.LargeVideoLayoutBinding
import com.zoomore.reelapp.models.video.RemoteVideo
import com.zoomore.reelapp.presentation.ui.components.video.MainLargeVideo
import com.zoomore.reelapp.repo.network.user.DefaultUserRepo
import com.zoomore.reelapp.repo.network.videos.DefaultVideosRepo
import com.zoomore.reelapp.utils.BottomNavViewUtils.hideBottomNavBar
import com.zoomore.reelapp.utils.BottomNavViewUtils.showBottomNavBar
import com.zoomore.reelapp.utils.SystemBarColors
import com.zoomore.reelapp.utils.ViewUtils
import com.zoomore.reelapp.utils.architecture.BaseFragment

class LargeVideoFragment : BaseFragment(R.layout.large_video_layout) {

    private lateinit var binding: LargeVideoLayoutBinding
    private lateinit var remoteVideo: RemoteVideo
    private val args by navArgs<LargeVideoFragmentArgs>()

    private val mainLargeVideo by lazy {
        MainLargeVideo(
            scope = lifecycleScope,
            lifecycle = viewLifecycleOwner.lifecycle,
            binding = binding,
            userRepo = DefaultUserRepo(),
            videosRepo = DefaultVideosRepo(),
            onPersonIconClicked = {
                findNavController().navigate(
                    LargeVideoFragmentDirections
                        .actionLargeVideoFragmentToProfileWithAccountFragment(remoteVideo.authorUid)
                )
            },
            onVideoEnded = { player ->
                // TODO: Change this to scroll to the next video. Thinking of using a LinkedList(the little DSA I know)
                player.restartPlayer()
            },
            onCommentVisibilityChanged = { isVisible ->
                if (isVisible) hideBottomNavBar(activity)
                else showBottomNavBar(activity)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // This is not included in setUpLayout() since mainLargeVideo requires that binding to initialized
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            isFollowingAuthor = mainLargeVideo.isFollowingAuthor
            isVideoLiked = mainLargeVideo.isVideoLiked
            liveComment = mainLargeVideo.liveUserComment
        }
        remoteVideo = args.remoteVideo
        mainLargeVideo.init(remoteVideo)
    }

    override fun setUpLayout() {
        binding = LargeVideoLayoutBinding.bind(requireView()).also {
            it.bottomAddCommentBtn.visibility = View.VISIBLE
        }
    }

    // TODO: Confirm this
    override fun onResume() {
        super.onResume()
        hideBottomNavBar(activity)
        ViewUtils.changeSystemBars(activity, SystemBarColors.DARK)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }


}