package com.zoomore.reelapp.presentation.ui.components.video

import android.net.Uri
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.SmallVideoLayoutBinding
import com.zoomore.reelapp.models.video.RemoteVideo
import com.zoomore.reelapp.utils.ImageUtils.getRequestListener
import com.zoomore.reelapp.utils.TimeUtils
import com.xwray.groupie.viewbinding.BindableItem
import timber.log.Timber

class SmallVideoGroup(
    private val remoteVideo: RemoteVideo,
    private val onClickListener: () -> Unit,
    private val onLoadFailed: (SmallVideoGroup) -> Unit
) : BindableItem<SmallVideoLayoutBinding>() {

    override fun bind(binding: SmallVideoLayoutBinding, position: Int) {
        Timber.d("Cursor url is ${remoteVideo.url} and Uri parser uri is ${Uri.parse(remoteVideo.url)}")
        binding.root.setOnClickListener { onClickListener() }
        binding.smallVideoDuration.text =
            TimeUtils.convertTimeToDisplayTime(remoteVideo.duration)

        val requestOptions = RequestOptions()
        Glide
            .with(binding.root)
            .applyDefaultRequestOptions(requestOptions)
            .load(remoteVideo.url)
            .addListener(getRequestListener(binding.loadingBar) { onLoadFailed(this) })
            .into(binding.smallVideoThumbnail)
    }

    override fun initializeViewBinding(view: View) =
        SmallVideoLayoutBinding.bind(view)

    override fun getLayout(): Int = R.layout.small_video_layout
}