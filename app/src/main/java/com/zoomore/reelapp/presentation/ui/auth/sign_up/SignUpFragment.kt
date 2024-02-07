package com.zoomore.reelapp.presentation.ui.auth.sign_up

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.SignUpPageBinding
import com.zoomore.reelapp.utils.BottomNavViewUtils
import com.zoomore.reelapp.utils.architecture.BaseFragment

class SignUpFragment : BaseFragment(R.layout.sign_up_page) {

    lateinit var binding: SignUpPageBinding

//    override val viewModel by viewModels<SignUpViewModel>()

    // ActivityResultLauncher for Google Sign Up
  /*  private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.googleAuthRepo.handleGoogleOnResult(result?.data)
        }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BottomNavViewUtils.hideBottomNavBar(activity)

    }

    override fun setUpLayout() {
        binding = SignUpPageBinding.bind(requireView())
    }

    override fun setUpClickListeners() {
        binding.signUpCancelBtn.setOnClickListener { findNavController().popBackStack() }
        binding.useLogInBtn.setOnClickListener { findNavController().navigate(R.id.action_signUpFragment_to_loginFragment) }

        binding.signUpUsePhoneBtn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_ageFragment)
        }
        binding.signUpFacebookBtn.setOnClickListener {
//            viewModel.facebookAuthRepo.doFacebookLogin(requireActivity())
        }
        binding.signUpGoogleBtn.setOnClickListener {
//            viewModel.googleAuthRepo.doGoogleAuth(requireContext(), launcher)
        }

    }

    override fun setUpLiveData() {
//        viewModel.liveCredential.observe(viewLifecycleOwner) { liveCredential ->
//            if (liveCredential == null) return@observe
//
//            // Since the body is used in providing a name and a profile picture, we should not be afraid if the values are null
//            // since we are performing a null check in CreateUserNameViewModel
//            val googleAccount = viewModel.googleAccount
//            val googleBody =
//                GoogleBody(googleAccount?.displayName, googleAccount?.photoUrl?.toString())
//
//            findNavController().navigate(
//                SignUpFragmentDirections.actionSignUpFragmentToCreateUsernameFragment(
//                    liveCredential,
//                    googleBody,
//                    null
//                )
//            )
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
/*        binding.signUpTwitterBtn.onActivityResult(requestCode, resultCode, data)
        viewModel.facebookAuthRepo.handleFacebookOnResult(requestCode, resultCode, data)*/
    }
}