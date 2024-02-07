package com.zoomore.reelapp.presentation.ui.upload.post_video

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zoomore.reelapp.repo.local.space.DefaultLocalSpaceRepo
import com.zoomore.reelapp.repo.network.storage.StorageRepo
import com.zoomore.reelapp.R
import com.zoomore.reelapp.models.local.LocalVideo
import com.zoomore.reelapp.models.succeeded
import com.zoomore.reelapp.models.upload.Progress
import com.zoomore.reelapp.repo.network.videos.DefaultVideosRepo
import com.zoomore.reelapp.utils.architecture.BaseViewModel
import kotlinx.coroutines.launch
import java.io.File

class PostVideoViewModel : BaseViewModel() {

    private val localSpaceRepo = DefaultLocalSpaceRepo()
    private val storageRepo = StorageRepo()
    private val videosRepo = DefaultVideosRepo()

    val liveDescription = MutableLiveData("")

    private val _uploadStatus = MutableLiveData(Progress.IDLE)
    val uploadStatus: LiveData<Progress> = _uploadStatus

    fun postVideo(context: Context, localVideo: LocalVideo) {
        val descriptionText = liveDescription.value ?: run {
            showMessage(R.string.video_description_needed)
            return
        }

        _uploadStatus.value = Progress.ACTIVE
        val tags = processTags()

        viewModelScope.launch {
            val newFilePath =
                localSpaceRepo.compressVideo(context, localVideo.filePath ?: return@launch)
            val localUri = File(newFilePath ?: return@launch).toUri()

            val result = storageRepo.uploadVideo(localUri)
            if (!result.succeeded) {
                _uploadStatus.value = Progress.FAILED
                showMessage(R.string.error_occurred_during_video_upload)
                return@launch
            }

            val downloadUrl = result.tryData()?.toString() ?: return@launch

            videosRepo.saveVideoToFireDB(
                isPrivate = false,
                videoUrl = downloadUrl,
                descriptionText = descriptionText,
                tags = tags,
                duration = localVideo.duration,
                onComplete = { succeeded ->
                    _uploadStatus.value = if (succeeded) Progress.DONE else Progress.FAILED
                }
            )
        }
    }

    private fun processTags(): Map<String, String> {
        val descriptionList = liveDescription.value?.split(" ") ?: listOf()
        return descriptionList.filter { it.startsWith("#") }.associateBy { it.replace("#", "") }
    }
}