package com.zoomore.reelapp.presentation.ui.auth.email_auth.enter_password

import android.graphics.Color
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentCreatePasswordBinding
import com.zoomore.reelapp.models.sign_up.EmailBody
import com.zoomore.reelapp.utils.architecture.BaseFragment

class EnterPasswordFragment : BaseFragment(R.layout.fragment_create_password) {

    lateinit var binding: FragmentCreatePasswordBinding

    private val args by navArgs<EnterPasswordFragmentArgs>()
    override val viewModel by viewModels<EnterPasswordViewModel>()

    private val emailBody: EmailBody
        get() = EmailBody(args.email, viewModel.passwordInput.value ?: "")

    override fun setUpLayout() {
        binding = FragmentCreatePasswordBinding.bind(requireView()).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
        }
        changeLayoutToMatchAuthType()
    }

    // Since the layout serves two purposes (create password during sign up and enter password during log in), change it if args.isLogin == true
    private fun changeLayoutToMatchAuthType() {
        val isLogin = args.isLogIn
        binding.authMethodPlainText.text =
            getString(if (isLogin) R.string.log_in else R.string.sign_up)
        binding.authBtn.text = getString(if (isLogin) R.string.log_in else R.string.sign_up)
        binding.enterPasswordPlainText.text =
            getString(if (isLogin) R.string.enter_password else R.string.create_password)
    }

    override fun setUpLiveData() {
        viewModel.navigate.observe(viewLifecycleOwner) {
            if (it == true) {
                if (args.isLogIn) navigateToMyProfile() else navigateToCreateUsername()
                viewModel.resetLiveNavigate()
            }
        }

        if (!args.isLogIn) {
            setUpLiveDataForSignUpAuth()
        }
    }

    /**
     * Sets up LiveData that shows the user if the password he/she is trying to create matches all our requirments
     */
    private fun setUpLiveDataForSignUpAuth() {
        viewModel.isValid.observe(viewLifecycleOwner) { isValid ->
            binding.liveStatusText.setTextColor(
                Color.parseColor(if (isValid == true) "#19FD00" else "#F43636")
            )
        }

        // We want to give password feedback to the user if he/she is signing up only.
        viewModel.livePasswordStatus.observe(viewLifecycleOwner) { stringResId ->
            if (!args.isLogIn)
                binding.liveStatusText.text = stringResId?.let { getString(it) } ?: ""
        }
    }

    override fun setUpClickListeners() {
        super.setUpClickListeners()
        binding.authBtn.setOnClickListener {
            if (args.isLogIn) viewModel.verifyPassword(emailBody)
            else viewModel.logInWithEmailBody(emailBody)
        }
    }

    private fun navigateToCreateUsername() {
        findNavController().navigate(
            EnterPasswordFragmentDirections
                .actionCreatePasswordFragmentToCreateUsernameFragment(null, null, emailBody)
        )
    }

    private fun navigateToMyProfile() {
        findNavController().navigate(
            R.id.meFragment,
            null,
            navOptions { popUpTo = R.id.homeFragment })
    }
}