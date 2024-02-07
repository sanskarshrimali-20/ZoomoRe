package com.zoomore.reelapp.presentation.ui.selected_tag

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentSelectedTagBinding

class SelectedTagFragment: Fragment(R.layout.fragment_selected_tag) {

    private lateinit var binding: FragmentSelectedTagBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpLayout()
    }

    private fun setUpLayout() {
        binding = FragmentSelectedTagBinding.bind(requireView())
    }

}