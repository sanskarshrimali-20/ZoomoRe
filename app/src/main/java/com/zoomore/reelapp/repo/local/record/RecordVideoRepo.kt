package com.zoomore.reelapp.repo.local.record

import android.content.Context
import com.zoomore.reelapp.models.local.LocalRecordLocation

interface RecordVideoRepo {
    suspend fun initVideo(context: Context, timeCreated: Long): LocalRecordLocation?

    suspend fun stopVideo(context: Context)
}