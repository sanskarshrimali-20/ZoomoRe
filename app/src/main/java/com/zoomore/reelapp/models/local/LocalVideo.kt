package com.zoomore.reelapp.models.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalVideo(
    var filePath: String?,
    val duration: Long?,
    val dateCreated: String?
): Parcelable