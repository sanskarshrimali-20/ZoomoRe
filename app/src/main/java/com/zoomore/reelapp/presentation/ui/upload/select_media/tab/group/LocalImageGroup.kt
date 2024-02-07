package com.zoomore.reelapp.presentation.ui.upload.select_media.tab.group

import android.view.View
import com.bumptech.glide.Glide
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.LocalImageGroupLayoutBinding
import com.zoomore.reelapp.models.local.LocalImage
import com.xwray.groupie.viewbinding.BindableItem

class LocalImageGroup(
    private val localImage: LocalImage,
    private val onClickListener: () -> Unit
) : BindableItem<LocalImageGroupLayoutBinding>() {

    override fun bind(binding: LocalImageGroupLayoutBinding, position: Int) {
        binding.root.setOnClickListener { onClickListener() }
        Glide.with(binding.localImage).load(localImage.url).centerCrop().into(binding.localImage)
    }

    override fun getLayout(): Int = R.layout.local_image_group_layout
    override fun initializeViewBinding(view: View) =
        LocalImageGroupLayoutBinding.bind(view)
}