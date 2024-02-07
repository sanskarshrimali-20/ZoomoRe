package com.zoomore.reelapp.presentation.ui.upload.post_video

import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.zoomore.reelapp.MainActivity
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentPostVideoBinding
import com.zoomore.reelapp.models.local.LocalVideo
import com.zoomore.reelapp.models.upload.Progress
import com.zoomore.reelapp.utils.ImageUtils
import com.zoomore.reelapp.utils.ResUtils
import com.zoomore.reelapp.utils.SystemBarColors
import com.zoomore.reelapp.utils.ViewUtils
import com.zoomore.reelapp.utils.architecture.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostVideoFragment : BaseFragment(R.layout.fragment_post_video) {

    lateinit var binding: FragmentPostVideoBinding

    private val args by navArgs<PostVideoFragmentArgs>()
    private val localVideo: LocalVideo by lazy { args.localVideo }
    override val viewModel by viewModels<PostVideoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch { loadVideoThumbnail() }

        binding.postBtn.setOnClickListener {
            viewModel.postVideo(requireContext(), localVideo)
        }
    }

    private suspend fun loadVideoThumbnail(): Unit = withContext(Dispatchers.IO) {
        val mediaMetadataRetriever = MediaMetadataRetriever().apply {
            setDataSource(requireContext(), localVideo.filePath?.toUri())
        }

        val bitmap = mediaMetadataRetriever.frameAtTime
        mediaMetadataRetriever.release()

        withContext(Dispatchers.Main) {
            val requestOptions = RequestOptions()
            Glide.with(requireContext())
                .applyDefaultRequestOptions(requestOptions)
                .addDefaultRequestListener(ImageUtils.getRequestListener(binding.loadingBar) {  })
                .load(bitmap)
                .into(binding.videoThumbnail)
        }
    }

    override fun setUpLayout() {
        binding = FragmentPostVideoBinding.bind(requireView()).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
            it.loadingLayout.uploadStatus = viewModel.uploadStatus
        }
    }

    override fun setUpLiveData() {
        super.setUpLiveData()
        viewModel.uploadStatus.observe(viewLifecycleOwner) { uploadStatus ->
            if (uploadStatus == Progress.DONE) // Set the navItem to home
                (activity as MainActivity).binding.navView.selectedItemId = R.id.homeFragment
            else if (uploadStatus == Progress.FAILED)
                ResUtils.showSnackBar(requireView(), R.string.error_occurred_during_video_upload)
        }
    }

    override fun onResume() {
        super.onResume()
        ViewUtils.changeSystemBars(activity, SystemBarColors.WHITE)
    }
}