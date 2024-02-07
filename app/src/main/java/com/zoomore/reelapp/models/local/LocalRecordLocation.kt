package com.zoomore.reelapp.models.local

import android.net.Uri
import java.io.FileDescriptor

/**
 * This is a class that will be returned when starting to record a video.
 *
 * @param filePath A file representing the file that will enable us to access the files location
 * @param fileDescriptor A fileDescriptor that will be passed to the [com.otaliastudios.cameraview.CameraView.takeVideo]
 */
// TODO: Find a better name for this
data class LocalRecordLocation(val contentUri: Uri, val filePath: String, val fileDescriptor: FileDescriptor)