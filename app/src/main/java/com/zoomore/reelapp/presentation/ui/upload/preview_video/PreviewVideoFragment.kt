package com.zoomore.reelapp.presentation.ui.upload.preview_video

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentPreviewVideoBinding
import com.zoomore.reelapp.presentation.exoplayer.Player
import com.zoomore.reelapp.utils.BottomNavViewUtils.hideBottomNavBar
import com.zoomore.reelapp.utils.SystemBarColors
import com.zoomore.reelapp.utils.ViewUtils
import com.zoomore.reelapp.utils.architecture.BaseFragment


class PreviewVideoFragment : BaseFragment(R.layout.fragment_preview_video) {

    lateinit var binding: FragmentPreviewVideoBinding

    private val args by navArgs<PreviewVideoFragmentArgs>()
    private val localVideo by lazy { args.localVideo }
    private val player by lazy {
        Player(
            simpleExoplayerView = binding.playerView,
            playBtn = binding.playBtn,
            context = requireContext(),
            url = localVideo.filePath,
            onVideoEnded = {
                it.restartPlayer()
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(player)
        player.init()
    }

    override fun setUpLayout() {
        binding = FragmentPreviewVideoBinding.bind(requireView())
    }

    override fun setUpClickListeners() {
        binding.nextBtn.setOnClickListener {
            findNavController().navigate(
                PreviewVideoFragmentDirections
                    .actionPreviewVideoFragmentToPostVideoFragment(localVideo)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        player.resumePlayer()
        ViewUtils.changeSystemBars(activity, SystemBarColors.DARK)
        ViewUtils.changeSystemNavigationBarColor(requireActivity(), R.color.dark_black)
        ViewUtils.hideStatusBar(requireActivity())
        hideBottomNavBar(activity)
    }

    override fun onPause() {
        super.onPause()
        player.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stopPlayer()
    }

}