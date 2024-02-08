package com.zoomore.reelapp.presentation.ui.settings

import androidx.fragment.app.viewModels
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentSettingsBinding
import com.zoomore.reelapp.utils.architecture.BaseFragment

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    override val viewModel by viewModels<SettingsViewModel>()

    override fun setUpLayout() {
        binding = FragmentSettingsBinding.bind(requireView())
    }


}