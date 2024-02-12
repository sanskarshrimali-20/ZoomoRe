package com.zoomore.reelapp.models.local

import android.net.Uri
import java.io.FileDescriptor


// TODO: Find a better name for this
data class LocalRecordLocation(val contentUri: Uri, val filePath: String, val fileDescriptor: FileDescriptor)