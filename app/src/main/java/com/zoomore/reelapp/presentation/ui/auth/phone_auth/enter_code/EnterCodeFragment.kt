package com.zoomore.reelapp.presentation.ui.auth.phone_auth.enter_code

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentEnterCodeBinding
import com.zoomore.reelapp.userVerificationId
import com.zoomore.reelapp.utils.KeyboardUtils
import com.zoomore.reelapp.utils.ResUtils
import com.zoomore.reelapp.utils.architecture.BaseFragment
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import timber.log.Timber

class EnterCodeFragment : BaseFragment(R.layout.fragment_enter_code) {

    private lateinit var binding: FragmentEnterCodeBinding

    private val args by navArgs<EnterCodeFragmentArgs>()
    override val viewModel by viewModels<EnterCodeViewModel>()

    override fun setUpLayout() {
        binding = FragmentEnterCodeBinding.bind(requireView())
    }

    override fun setUpClickListeners() {
        super.setUpClickListeners()
        binding.signUpBtn.setOnClickListener {
            val codeView = binding.inputCode

            if (codeView.code.length != codeView.codeLength) {
                ResUtils.showSnackBar(requireView(), R.string.code_invalid)
                codeView.clearCode()
            } else {
                KeyboardUtils.hide(requireView())
                getUserCredential(codeView.code)
            }
        }
    }

    override fun setUpLiveData() {
        super.setUpLiveData()
        viewModel.navigateToMyProfile.observe(viewLifecycleOwner) { navigateToMyProfile ->
            if (navigateToMyProfile == true) {
                findNavController().navigate(
                    R.id.meFragment,
                    null,
                    navOptions { popUpTo = R.id.homeFragment }
                )
                viewModel.resetNavigateToMyProfile()
            }
        }
    }

    private fun getUserCredential(code: String) {
        Timber.d("verificationId is $userVerificationId and code is $code")
        val credential =
            PhoneAuthProvider.getCredential(userVerificationId.toString(), code) as AuthCredential

        if (args.isLogIn)
            viewModel.logInWithCredential(credential)
        else
            navigateToCreateUsername(credential)

    }

    private fun navigateToCreateUsername(credential: AuthCredential) {
        findNavController()
            .navigate(
                EnterCodeFragmentDirections
                    .actionEnterCodeFragmentToCreateUsernameFragment(credential, null, null)
            )
    }

}