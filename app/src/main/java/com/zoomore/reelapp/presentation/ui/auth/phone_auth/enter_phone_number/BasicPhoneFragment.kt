package com.zoomore.reelapp.presentation.ui.auth.phone_auth.enter_phone_number

import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentBasicPhoneBinding
import com.zoomore.reelapp.presentation.ui.auth.select_basic_auth.SelectBasicAuthFragmentDirections
import com.zoomore.reelapp.utils.architecture.BaseFragment
import timber.log.Timber
import kotlin.properties.Delegates

class BasicPhoneFragment : BaseFragment(R.layout.fragment_basic_phone) {

    lateinit var binding: FragmentBasicPhoneBinding

    override val viewModel by viewModels<BasicPhoneViewModel>()

    var isLogin by Delegates.notNull<Boolean>()

    override fun setUpLayout() {
        binding = FragmentBasicPhoneBinding.bind(requireView()).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
        }
    }

    override fun setUpClickListeners() {
        binding.sendCodeBtn.setOnClickListener {
            viewModel.sendCode(
                binding.countryCodePicker.selectedCountryCodeWithPlus,
                requireActivity(),
                onComplete
            )
        }
    }

    override fun setUpLiveData() {
        viewModel.liveCredential.observe(viewLifecycleOwner) { liveCredential ->
            liveCredential?.let {
                findNavController().navigate(
                    SelectBasicAuthFragmentDirections.actionSelectBasicSignUpFragmentToCreateUsernameFragment(
                        liveCredential,
                        null,
                        null
                    )

                )

                viewModel.resetLiveCredential()
            }
        }

        // Change the background tint in code because the textColor is automatically changed in the layout through DataBinding.
        viewModel.isValid.observe(viewLifecycleOwner) { isValid ->
            binding.sendCodeBtn.backgroundTintList = ResourcesCompat.getColorStateList(
                resources,
                if (isValid) R.color.pinkBtnBackground else R.color.grey_button_background,
                null
            )
        }
    }


    private val onComplete = { phoneNumber: String? ->
        phoneNumber?.let {
            try {
                findNavController().navigate(
                    SelectBasicAuthFragmentDirections
                        .actionSelectBasicSignUpFragmentToEnterCodeFragment(it, isLogin)
                )
            } catch (e: Exception) {
                Timber.e(e)
            }
        } ?: run {
            Timber.d("phoneNumber is null")
        }
    }

    companion object {
        fun getInstance(isLogin: Boolean) = BasicPhoneFragment().also {
            it.isLogin = isLogin
        }
    }
}