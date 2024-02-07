package com.zoomore.reelapp.presentation.ui.profile.without_account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentProfileWithoutAccountBinding
import com.zoomore.reelapp.utils.SystemBarColors
import com.zoomore.reelapp.utils.ViewUtils

class ProfileWithoutAccountFragment : Fragment(R.layout.fragment_profile_without_account) {

    lateinit var binding: FragmentProfileWithoutAccountBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileWithoutAccountBinding.bind(requireView())
        binding.signUpBtn.setOnClickListener { findNavController().navigate(R.id.action_meFragment_to_signUpFragment) }
    }

    override fun onResume() {
        super.onResume()
        ViewUtils.changeSystemBars(activity, SystemBarColors.WHITE)
    }
}