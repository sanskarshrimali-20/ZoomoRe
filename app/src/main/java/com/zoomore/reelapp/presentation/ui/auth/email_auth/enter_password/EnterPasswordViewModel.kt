package com.zoomore.reelapp.presentation.ui.auth.email_auth.enter_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.zoomore.reelapp.R
import com.zoomore.reelapp.models.sign_up.EmailBody
import com.zoomore.reelapp.repo.network.auth.AuthRepo
import com.zoomore.reelapp.utils.architecture.BaseViewModel
import kotlinx.coroutines.launch

class EnterPasswordViewModel : BaseViewModel() {

    private val authRepo = AuthRepo()

    val passwordInput = MutableLiveData("")
    val livePasswordStatus = MutableLiveData<Int>()

    val isValid = Transformations.map(livePasswordStatus) { status ->
        status == R.string.valid_password
    }

    private var _navigate = MutableLiveData<Boolean>()
    val navigate: LiveData<Boolean> = _navigate

    private val passwordInputObserver = { password: String ->
        val hasDigit = password.any { it.isDigit() }
        val hasCharacter = password.any { it.isLetter() }

        livePasswordStatus.value = getErrorFromPassword(password, hasCharacter, hasDigit)
    }

    init {
        passwordInput.observeForever(passwordInputObserver)
    }

    /**
     * Verify that the password the user is creating during sign up is valid
     */
    fun verifyPassword() {
        val password = passwordInput.value ?: ""

        val hasDigit = password.any { it.isDigit() }
        val hasCharacter = password.any { it.isLetter() }

        if (password.length > 8 && hasCharacter && hasDigit) // Password meets our requirements
            _navigate.value = true
        else
            showMessage(R.string.invalid_password)
    }

    fun logInWithEmailBody(emailBody: EmailBody) {
        viewModelScope.launch {
            val authResult = authRepo.logInWithEmailBody(emailBody)

            if (authResult.tryData() != null) // Log in successful
                _navigate.value = true
            else
                showMessage(R.string.error_occurred_during_log_in)
        }
    }

    private fun getErrorFromPassword(
        password: String,
        hasCharacter: Boolean,
        hasDigit: Boolean
    ) = when {
        // Complete password
        password.length >= 8 && hasCharacter && hasDigit -> R.string.valid_password
        // Lacks digit
        hasCharacter && !hasDigit -> R.string.needs_one_digit
        // Lacks character
        !hasCharacter && hasDigit -> R.string.needs_one_character
        // Lacks both
        else -> R.string.needs_both_digit_and_character
    }

    fun resetLiveNavigate() {
        _navigate.value = false
    }

    override fun onCleared() {
        super.onCleared()
        passwordInput.removeObserver(passwordInputObserver)
    }
}