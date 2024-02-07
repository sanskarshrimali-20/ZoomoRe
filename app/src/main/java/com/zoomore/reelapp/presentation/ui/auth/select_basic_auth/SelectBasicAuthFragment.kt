package com.zoomore.reelapp.presentation.ui.auth.select_basic_auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentSelectBasicSignUpBinding
import com.zoomore.reelapp.presentation.ui.auth.email_auth.enter_email.BasicEmailFragment
import com.zoomore.reelapp.presentation.ui.auth.phone_auth.enter_phone_number.BasicPhoneFragment
import com.google.android.material.tabs.TabLayoutMediator
import timber.log.Timber

class SelectBasicAuthFragment : Fragment(R.layout.fragment_select_basic_sign_up) {

    lateinit var binding: FragmentSelectBasicSignUpBinding

    private val args by navArgs<SelectBasicAuthFragmentArgs>()

    private val onPageChangedCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            Timber.d("Callback position is $position")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSelectBasicSignUpBinding.bind(view)

        binding.basicViewpager.also {
            it.adapter = BasicFragmentStateAdapter(this)
            it.registerOnPageChangeCallback(onPageChangedCallback)
        }

        val tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            Timber.d("tab is $tab and tab position is $position")
            tab.text = getString(if (position == 0) R.string.phone else R.string.email)
        }
        TabLayoutMediator(binding.tabLayout, binding.basicViewpager, tabConfigurationStrategy).attach()
    }

    inner class BasicFragmentStateAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            if (position == 0) BasicPhoneFragment.getInstance(args.isLogIn)
            else BasicEmailFragment.getInstance(args.isLogIn)
    }
}