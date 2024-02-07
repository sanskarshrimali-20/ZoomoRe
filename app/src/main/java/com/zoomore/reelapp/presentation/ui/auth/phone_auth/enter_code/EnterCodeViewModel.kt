package com.zoomore.reelapp.presentation.ui.auth.phone_auth.enter_code

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zoomore.reelapp.R
import com.zoomore.reelapp.models.succeeded
import com.zoomore.reelapp.repo.network.auth.AuthRepo
import com.zoomore.reelapp.utils.architecture.BaseViewModel
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class EnterCodeViewModel : BaseViewModel() {

    private val authRepo = AuthRepo()

    private val _navigateToMyProfile = MutableLiveData(false)
    val navigateToMyProfile: LiveData<Boolean> = _navigateToMyProfile

    fun logInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            val result = authRepo.signInWithCredential(credential)
            if (result.succeeded && result.tryData() != null)
                // User is logged in so let's navigate to their profile.
                _navigateToMyProfile.value = true
            else
                handleLoginError(result.error() ?: return@launch)
        }

    }

    private fun handleLoginError(exception: Exception) {
        Timber.e(exception, "handleLoginError:")
        when (exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                // The code that has been entered is invalid. Show a snackBar.
                showMessage(R.string.code_invalid)
            }
            is FirebaseTooManyRequestsException -> {
                // Too many requests from the same device have been sent to Firebase. Let's request the user to try again latter
                showMessage(R.string.too_many_requests)
            }
            is IOException -> {
                // No network connection
                showMessage(R.string.no_network_connection)
            }
        }
    }

    fun resetNavigateToMyProfile() {
        _navigateToMyProfile.value = false
    }
}