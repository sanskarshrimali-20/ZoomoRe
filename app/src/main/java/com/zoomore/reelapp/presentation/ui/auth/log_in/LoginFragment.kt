package com.zoomore.reelapp.presentation.ui.auth.log_in

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentLogInBinding
import com.zoomore.reelapp.utils.BottomNavViewUtils
import com.zoomore.reelapp.utils.architecture.BaseFragment

class LoginFragment : BaseFragment(R.layout.fragment_log_in) {

    private lateinit var binding: FragmentLogInBinding
    override val viewModel by viewModels<LogInViewModel>()

    // ActivityResultLauncher for Google Sign Up
//    private val launcher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            viewModel.googleAuthRepo.handleGoogleOnResult(result?.data)
//        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BottomNavViewUtils.hideBottomNavBar(activity)

//        binding.logInTwitterBtn.callback = viewModel.twitterCallback
    }

    override fun setUpLayout() {
        binding = FragmentLogInBinding.bind(requireView())
    }

    override fun setUpClickListeners() {
        binding.logInCancelBtn.setOnClickListener { findNavController().popBackStack() }
        binding.useSignUpBtn.setOnClickListener { findNavController().popBackStack() } // Return to SignUpFragment

        binding.logInUsePhoneBtn.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToSelectBasicSignUpFragment(isLogIn = true)
            )
        }
       /* binding.logInFacebookBtn.setOnClickListener {
            viewModel.facebookAuthRepo.doFacebookLogin(requireActivity())
        }
        binding.logInGoogleBtn.setOnClickListener {
            viewModel.googleAuthRepo.doGoogleAuth(requireContext(), launcher)
        }
        binding.logInTwitterBtn.setTheClickListener {
            lifecycleScope.launch { viewModel.twitterAuthRepo.doTwitterSignUp(requireActivity()) }
        }*/
    }

    override fun setUpLiveData() {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        binding.logInTwitterBtn.onActivityResult(requestCode, resultCode, data)
//        viewModel.facebookAuthRepo.handleFacebookOnResult(requestCode, resultCode, data)
    }
}