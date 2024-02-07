package com.zoomore.reelapp.presentation.ui.profile.with_account.tab

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.zoomore.reelapp.presentation.ui.components.video.SmallVideoGroup
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.ProfileVideoTabBinding
import com.zoomore.reelapp.models.video.RemoteVideo
import com.zoomore.reelapp.models.video.VideoType
import com.zoomore.reelapp.presentation.ui.profile.MeFragmentDirections
import com.zoomore.reelapp.presentation.ui.profile.with_account.ProfileWithAccountFragmentDirections
import com.zoomore.reelapp.utils.architecture.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class ProfileVideoTab : BaseFragment(R.layout.profile_video_tab) {

    lateinit var binding: ProfileVideoTabBinding
    lateinit var profileUid: String
    lateinit var videoType: VideoType

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    override val viewModel by viewModels<ProfileVideoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchVideos(profileUid, videoType)
    }

    override fun setUpLayout() {
        binding = ProfileVideoTabBinding.bind(requireView())
        binding.publicVideosRecyclerview.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = groupAdapter
        }
    }

    override fun setUpLiveData() {
        viewModel.listOfRemoteVideo.observe(viewLifecycleOwner) { listOfRemoteVideo ->
            listOfRemoteVideo?.forEach { remoteVideo ->
                val smallVideoGroup = SmallVideoGroup(
                    remoteVideo = remoteVideo,
                    onClickListener = { navigateToLargeVideo(remoteVideo) },
                    { removeGroup(it) }
                )
                groupAdapter.add(smallVideoGroup)
            }
        }
    }

    // If the user clicked on the Me icon in the Bottom Nav Bar, we need to use the MeFragmentDirections otherwise,
    // use the ProfileWithAccountFragmentDirections.
    private fun navigateToLargeVideo(remoteVideo: RemoteVideo) {
        findNavController().navigate(
            if (findNavController().currentDestination?.id == R.id.meFragment)
                MeFragmentDirections.actionMeFragmentToLargeVideoFragment(remoteVideo)

            else
                ProfileWithAccountFragmentDirections.actionProfileWithAccountFragmentToLargeVideoFragment(remoteVideo)

        )
    }

    private fun removeGroup(smallVideoGroup: SmallVideoGroup) {
        groupAdapter.remove(smallVideoGroup)
    }

    companion object {
        fun getInstance(uid: String, videoType: VideoType): ProfileVideoTab {
            val profileVideosFragment = ProfileVideoTab()
            profileVideosFragment.profileUid = uid
            profileVideosFragment.videoType = videoType
            return profileVideosFragment
        }
    }
}