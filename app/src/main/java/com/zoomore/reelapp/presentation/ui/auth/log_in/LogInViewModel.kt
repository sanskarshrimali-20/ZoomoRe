package com.zoomore.reelapp.presentation.ui.auth.log_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.zoomore.reelapp.R
import com.zoomore.reelapp.models.succeeded
import com.zoomore.reelapp.repo.network.auth.AuthRepo
import com.zoomore.reelapp.utils.architecture.BaseViewModel
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.launch

class LogInViewModel : BaseViewModel() {

    private val authRepo = AuthRepo()
//    val googleAuthRepo = authRepo.GoogleAuthRepo()
//    val twitterAuthRepo = authRepo.TwitterAuthRepo()
//    val facebookAuthRepo = authRepo.FacebookAuthRepo()

    private val _navigateToMyProfile = MutableLiveData(false)
    val navigateToMyProfile: LiveData<Boolean> = _navigateToMyProfile.distinctUntilChanged()

    private val liveCredentialObserver: (AuthCredential?) -> Unit = { credential ->
        credential?.let {
            viewModelScope.launch {
                val result = authRepo.signInWithCredential(credential)
                if (!result.succeeded)
                    showMessage(R.string.error_occurred_during_log_in)

                _navigateToMyProfile.value = result.succeeded
            }
        }
    }

//    val twitterCallback = object : Callback<TwitterSession>() {
//        override fun success(result: Result<TwitterSession>?) {
//            Timber.d("Twitter sign in successful")
//            showMessage(R.string.successfully_signed_up)
//        }
//
//        override fun failure(exception: TwitterException?) {
//            Timber.e(exception)
//            showMessage(R.string.error_occurred_during_sign_up)
//        }
//    }

    init {
        authRepo.liveCredential.observeForever(liveCredentialObserver)
    }

    fun resetNavigateToMyProfile() {
        _navigateToMyProfile.value = false
    }

    override fun onCleared() {
        super.onCleared()
        authRepo.liveCredential.removeObserver(liveCredentialObserver)
    }
}