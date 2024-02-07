package com.zoomore.reelapp.repo.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zoomore.reelapp.models.succeeded
import com.zoomore.reelapp.models.video.RemoteVideo
import com.zoomore.reelapp.repo.network.comment.CommentRepo
import com.zoomore.reelapp.repo.network.comment.DefaultCommentRepo
import com.zoomore.reelapp.repo.network.user.DefaultUserRepo
import com.zoomore.reelapp.repo.network.user.UserRepo
import com.zoomore.reelapp.repo.network.videos.DefaultVideosRepo
import com.zoomore.reelapp.repo.network.videos.VideosRepo
import com.zoomore.reelapp.utils.architecture.BaseViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel (
    val userRepo: UserRepo = DefaultUserRepo(),
    val commentRepo: CommentRepo = DefaultCommentRepo(),
    val videosRepo: VideosRepo = DefaultVideosRepo()
): BaseViewModel() {

    private var _listOfRemoteVideo = MutableLiveData<List<RemoteVideo>>()
    val listOfRemoteVideo: LiveData<List<RemoteVideo>> = _listOfRemoteVideo

    private var isFetching = false

    init {
        fetchVideos()
    }

    fun fetchVideos() {
        if (!isFetching) {
            isFetching = true
            viewModelScope.launch {
                val result = videosRepo.fetchRandomVideos()
                Timber.d("result.data.size is ${result.tryData()?.size}")

                if (result.succeeded)
                    _listOfRemoteVideo.value = result.tryData() ?: listOf()
                isFetching = false
            }
        }
    }

}