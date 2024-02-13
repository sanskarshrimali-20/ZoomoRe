package com.zoomore.reelapp.presentation.ui.profile.with_account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.zoomore.reelapp.models.user.User
import com.zoomore.reelapp.repo.network.auth.AuthRepo
import com.zoomore.reelapp.repo.network.user.DefaultUserRepo
import com.zoomore.reelapp.utils.architecture.BaseViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileWithAccountViewModel: BaseViewModel() {
    private val userRepo = DefaultUserRepo()
    private val authRepo = AuthRepo()


    private val _profileUser = MutableLiveData<User>()
    val profileUser: LiveData<User> = _profileUser

    fun fetchUser(profileUid: String) {
        viewModelScope.launch {
            val user = userRepo.getUserProfile(profileUid).tryData() ?: return@launch
            _profileUser.value = user
        }
    }

    fun editUserProfile(userDescription: String,
                        username: String,
                        profilePicture: String?) {

        viewModelScope.launch {
            val result = userRepo.editUserToDatabase(userDescription,username,profilePicture).tryData() ?: return@launch

            if (result){
                Timber.d("Update Successfully")
            }

        }
    }

}