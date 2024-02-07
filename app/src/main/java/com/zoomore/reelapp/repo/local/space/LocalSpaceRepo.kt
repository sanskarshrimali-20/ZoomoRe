package com.zoomore.reelapp.repo.local.space

import android.content.Context

interface LocalSpaceRepo {
    /**
     * Checks if the device has enough storage to accommodate the new video
     */
    @Suppress("DEPRECATION")
    fun hasEnoughSpace(): Boolean

    /**
     * Compresses the video referenced by the originalFilePath
     *
     * @return a path to the new video
     */
    suspend fun compressVideo(context: Context, originalFilePath: String): String?
}