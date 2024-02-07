package com.zoomore.reelapp.presentation.ui.inbox

import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentInboxBinding
import com.zoomore.reelapp.utils.SystemBarColors
import com.zoomore.reelapp.utils.ViewUtils
import com.zoomore.reelapp.utils.architecture.BaseFragment

class InboxFragment : BaseFragment(R.layout.fragment_inbox) {

    private lateinit var binding: FragmentInboxBinding

    override fun setUpLayout() {
        binding = FragmentInboxBinding.bind(requireView())
    }

    override fun onStart() {
        super.onStart()
        ViewUtils.changeSystemBars(activity, SystemBarColors.WHITE)
    }
}