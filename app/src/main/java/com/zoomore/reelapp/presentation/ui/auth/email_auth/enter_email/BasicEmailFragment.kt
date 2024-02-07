package com.zoomore.reelapp.presentation.ui.auth.email_auth.enter_email

import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentBasicEmailBinding
import com.zoomore.reelapp.presentation.ui.auth.select_basic_auth.SelectBasicAuthFragmentDirections
import com.zoomore.reelapp.utils.architecture.BaseFragment
import kotlin.properties.Delegates

class BasicEmailFragment : BaseFragment(R.layout.fragment_basic_email) {

    private lateinit var binding: FragmentBasicEmailBinding
    override val viewModel by viewModels<BasicEmailViewModel>()

    var isLogin by Delegates.notNull<Boolean>()

    override fun setUpLayout() {
        binding = FragmentBasicEmailBinding.bind(requireView()).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
        }
    }

    override fun setUpLiveData() {
        viewModel.shouldNavigate.observe(viewLifecycleOwner) { shouldNavigate ->
            val email = viewModel.liveEmail.value ?: return@observe
            if (shouldNavigate) {
                findNavController()
                    .navigate(
                        SelectBasicAuthFragmentDirections.actionSelectBasicSignUpFragmentToCreatePasswordFragment(email,isLogin)
                    )
                viewModel.resetShouldNavigate()
            }
        }


        // Change the background tint in code because the textColor is automatically changed in the layout through DataBinding.
        viewModel.isValid.observe(viewLifecycleOwner) { isValid ->
            binding.signUpBtn.backgroundTintList = ResourcesCompat.getColorStateList(
                resources,
                if (isValid) R.color.pinkBtnBackground else R.color.grey_button_background,
                null
            )
        }
    }

    override fun setUpClickListeners() {
        binding.addGmailBtn.setOnClickListener { viewModel.appendEmailExtension(getString(R.string.gmail_com)) }
        binding.addHotmailBtn.setOnClickListener { viewModel.appendEmailExtension(getString(R.string.hotmail_com)) }
        binding.addOutlookBtn.setOnClickListener { viewModel.appendEmailExtension(getString(R.string.outlook_com)) }
    }

    companion object {
        fun getInstance(isLogin: Boolean) = BasicEmailFragment().also {
            it.isLogin = isLogin
        }
    }
}