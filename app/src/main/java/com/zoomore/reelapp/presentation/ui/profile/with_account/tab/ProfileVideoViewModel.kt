package com.zoomore.reelapp.presentation.ui.profile.with_account.tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zoomore.reelapp.models.video.RemoteVideo
import com.zoomore.reelapp.models.video.VideoType
import com.zoomore.reelapp.repo.network.user.DefaultUserRepo
import com.zoomore.reelapp.repo.network.user.UserRepo
import com.zoomore.reelapp.utils.architecture.BaseViewModel
import kotlinx.coroutines.launch

class ProfileVideoViewModel(
    private val userRepo: UserRepo = DefaultUserRepo()
): BaseViewModel() {

    private val _listOfRemoteVideo = MutableLiveData<List<RemoteVideo>>()
    val listOfRemoteVideo: LiveData<List<RemoteVideo>> = _listOfRemoteVideo

    fun fetchVideos(profileUid: String, videoType: VideoType) {
        viewModelScope.launch {
            val result = userRepo.getUserVideos(profileUid, videoType)
            _listOfRemoteVideo.value = result.tryData()?.filterNotNull()
        }
    }

}