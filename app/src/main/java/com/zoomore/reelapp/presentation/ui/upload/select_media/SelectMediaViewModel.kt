package com.zoomore.reelapp.presentation.ui.upload.select_media

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.zoomore.reelapp.repo.local.media.DefaultLocalMediaRepo
import com.zoomore.reelapp.utils.architecture.BaseViewModel
import kotlinx.coroutines.launch

class SelectMediaViewModel : BaseViewModel() {

    val localMediaRepo by lazy { DefaultLocalMediaRepo() }

    fun initViewModel(context: Context) {
        viewModelScope.launch {
            localMediaRepo.getAllImages(context)
            localMediaRepo.getAllVideos(context)
        }
    }

}