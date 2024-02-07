package com.zoomore.reelapp.repo.network.utils

import com.zoomore.reelapp.models.TheResult.Companion.theError
import com.zoomore.reelapp.models.TheResult.Companion.theSuccess
import timber.log.Timber

/**
 * A super duper epic wrapper that logs and handles network calls to Firebase
 *
 * @param firebaseLambda A suspend lambda that makes a network call and returns T
 * @return A custom result containing T
 */
suspend fun <T> safeAccess(firebaseLambda: suspend () -> T) = try {
    theSuccess(firebaseLambda())
} catch (e: Exception) {
    Timber.e(e)
    theError(e)
}