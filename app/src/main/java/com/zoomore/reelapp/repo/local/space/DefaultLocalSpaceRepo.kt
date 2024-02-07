package com.zoomore.reelapp.repo.local.space

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import com.zoomore.reelapp.repo.local.utils.toMB
import com.vincent.videocompressor.VideoCompress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume

class DefaultLocalSpaceRepo : LocalSpaceRepo {

    companion object {
        const val MINIMUM_DEVICE_STORAGE_IN_MB = 100
    }

    @Suppress("DEPRECATION")
    override fun hasEnoughSpace(): Boolean {
        val rootDir: File = Environment.getExternalStorageDirectory()
        val stat = StatFs(rootDir.path)
        val sizeInBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val blockSize = stat.blockSizeLong
            val availableBlocks = stat.availableBlocksLong
            blockSize * availableBlocks
        } else {
            val blockSize = stat.blockSize.toLong()
            val availableBlocks = stat.availableBlocks.toLong()
            blockSize * availableBlocks
        }
        val sizeInMB = sizeInBytes.toMB()
        Timber.d("Size in MB is $sizeInMB")
        return sizeInMB > MINIMUM_DEVICE_STORAGE_IN_MB
    }

    override suspend fun compressVideo(context: Context, originalFilePath: String) =
        withContext(Dispatchers.IO) {
            val videoDirectory = File(originalFilePath).parent
            val destPath = File(videoDirectory, generateFileName()).path

            Timber.d("originalFilePath is $originalFilePath")
            Timber.d("videoDirectory is $videoDirectory")

            compressMedium(originalFilePath, destPath)
            deleteOriginalFile(originalFilePath)

            Timber.d("newFilePath is $destPath")
            return@withContext destPath
        }

    private fun deleteOriginalFile(originalFilePath: String) {
        try {
            val deleteWorked = File(originalFilePath).delete()
            Timber.d("deleteWorked is $deleteWorked")
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun generateFileName() =
        "VIDEO_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.mp4"

    @Suppress("INACCESSIBLE_TYPE")
    private suspend fun compressMedium(srcPath: String, destPath: String) =
        suspendCancellableCoroutine<String> { cont ->
            VideoCompress.compressVideoMedium(
                srcPath,
                destPath,
                object : VideoCompress.CompressListener {
                    override fun onStart() {
                        Timber.d("Compression started")
                    }

                    override fun onSuccess() {
                        cont.resume(destPath)
                    }

                    override fun onFail() {
                        cont.cancel(Exception("Compression failed"))
                    }

                    override fun onProgress(percent: Float) {
                        Timber.v("Compression progessing at: $percent")
                    }
                }
            )
        }
}