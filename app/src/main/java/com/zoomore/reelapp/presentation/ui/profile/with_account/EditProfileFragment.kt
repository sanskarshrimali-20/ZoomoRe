package com.zoomore.reelapp.presentation.ui.profile.with_account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentEditProfileBinding
import com.zoomore.reelapp.utils.ImageUtils
import com.zoomore.reelapp.utils.architecture.BaseFragment

class EditProfileFragment :BaseFragment(R.layout.fragment_edit_profile) {


    private lateinit var binding: FragmentEditProfileBinding

//    private val args by navArgs<ProfileWithAccountFragmentArgs>()
    override val viewModel by viewModels<ProfileWithAccountViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // This is not included in setUpLayout() since mainLargeVideo requires that binding to initialized

        binding.idTvSave.setOnClickListener {
            viewModel.editUserProfile(binding.userName.text.toString(),binding.userDescription.text.toString(),"")
        }
    }

    override fun setUpLayout() {
        viewModel.profileUser.observe(viewLifecycleOwner) { profileUser ->
            profileUser?.let {
                binding.userName.setText(profileUser.username)
                binding.userDescription.setText(profileUser.userDescription)


                // Since the user can chose to stay without a profile picture, lets use the person icon
                // as a default.
                if (profileUser.profilePictureUrl == null)
                    ImageUtils.loadGlideImage(binding.userPhoto, R.drawable.white_person_icon)
                else
                    ImageUtils.loadGlideImage(binding.userPhoto, profileUser.profilePictureUrl)
            }
        }
    }




}