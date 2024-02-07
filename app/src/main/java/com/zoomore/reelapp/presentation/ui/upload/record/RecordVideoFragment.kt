package com.zoomore.reelapp.presentation.ui.upload.record

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentRecordVideoBinding
import com.zoomore.reelapp.utils.BottomNavViewUtils.hideBottomNavBar
import com.zoomore.reelapp.utils.BottomNavViewUtils.showBottomNavBar
import com.zoomore.reelapp.utils.PermissionUtils
import com.zoomore.reelapp.utils.PermissionUtils.arePermissionsGranted
import com.zoomore.reelapp.utils.PermissionUtils.recordVideoPermissions
import com.zoomore.reelapp.utils.SystemBarColors
import com.zoomore.reelapp.utils.ViewUtils.changeSystemBars
import com.zoomore.reelapp.utils.ViewUtils.hideStatusAndNavBar
import com.zoomore.reelapp.utils.ViewUtils.showStatusAndNavBar
import com.zoomore.reelapp.utils.architecture.BaseFragment
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import kotlinx.coroutines.launch
import timber.log.Timber

class RecordVideoFragment : BaseFragment(R.layout.fragment_record_video) {

    private lateinit var binding: FragmentRecordVideoBinding
    private lateinit var cameraView: CameraView

    override val viewModel by viewModels<RecordVideoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraView = binding.cameraView
        initCameraView()
        changeUIBasedOnPermissions()
    }

    private fun initCameraView() {
        cameraView.apply {
            facing = Facing.BACK
            audio = Audio.ON
            mode = Mode.VIDEO

            addCameraListener(viewModel.getCameraListener(requireContext()))

            mapGesture(Gesture.PINCH, GestureAction.ZOOM) // Pinch to zoom!
            mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS) // Tap to focus!
        }
    }

    override fun setUpLayout() {
        binding = FragmentRecordVideoBinding.bind(requireView()).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
        }
    }

    override fun setUpLiveData() {
        super.setUpLiveData()
        viewModel.showLittleSpace.observe(viewLifecycleOwner) { hasLittleSpace ->
            binding.littleSpaceLayout.root.visibility =
                if (hasLittleSpace) View.VISIBLE else View.GONE
        }

        viewModel.localVideo.observe(viewLifecycleOwner) { localVideo ->
            localVideo?.let {
                findNavController().navigate(
                    RecordVideoFragmentDirections
                        .actionRecordVideoFragmentToPreviewVideoFragment(localVideo)
                )
                viewModel.resetLocalVideo()
            }
        }

    }

    override fun setUpClickListeners() {
        setUpCameraSettings()
        setUpPermissionClickListeners()
        setLittleSpaceClickListeners()

        binding.closeBtn.setOnClickListener { findNavController().popBackStack() }
        binding.uploadImageBtn.setOnClickListener {
            findNavController().navigate(R.id.action_recordVideoFragment_to_createVideoFragment)
        }

        binding.startRecordingBtn.setOnClickListener {
            // Resume recording
            if (viewModel.hasRecordingStarted.value == true)
                viewModel.resumeVideo()
            // Start recording
            else {
                lifecycleScope.launch {
                    val fileDescriptor =
                        viewModel.startVideo(requireContext()) ?: return@launch
                    cameraView.takeVideo(fileDescriptor)
                }
            }
        }

        binding.pauseRecordingBtn.setOnClickListener {
            cameraView.stopVideo()
            viewModel.pauseVideo()
        }

        binding.finishRecordingBtn.setOnClickListener {
            if (viewModel.hasRecordingStarted.value == true) {
                cameraView.stopVideo()
                viewModel.stopVideo(requireContext())
            }
        }
    }

    private fun setLittleSpaceClickListeners() {
        binding.littleSpaceLayout.littleSpaceBtn.setOnClickListener {
            viewModel.resetShowLittleLayout()
        }
    }

    private fun setUpPermissionClickListeners() {
        binding.permissionsLayout.grantPermissionsBtn.setOnClickListener { requestPermissions() }
    }

    private fun turnOnCameraPreview() {
        Timber.d("turnOnCameraPreview called")
        cameraView.open()
    }

    private fun setUpCameraSettings() {
        binding.flipCameraBtn.setOnClickListener {
            cameraView.facing = if (cameraView.facing == Facing.FRONT) Facing.BACK else Facing.FRONT
        }
        binding.flashBtn.setOnClickListener {
            val isFlashOff = cameraView.flash == Flash.OFF

            cameraView.flash = if (isFlashOff) Flash.ON else Flash.OFF
            binding.flashBtn.setImageResource(
                if (isFlashOff) R.drawable.ic_round_flash_on else R.drawable.ic_round_flash_off
            )
        }
    }

    private val dialogMultiplePermissionsListener by lazy {
        PermissionUtils.DialogMultiplePermissionsListener(
            view = requireView(),
            onPermissionsGranted = {
                binding.permissionsLayout.root.visibility = View.GONE
                turnOnCameraPreview()
            },
            onPermissionsDenied = { binding.permissionsLayout.root.visibility = View.VISIBLE }
        )
    }

    private fun requestPermissions() {
        PermissionUtils.requestPermissions(
            requireContext(),
            dialogMultiplePermissionsListener,
            recordVideoPermissions
        )
    }

    private fun changeUIBasedOnPermissions() {
        if (arePermissionsGranted(requireContext(), recordVideoPermissions)) {
            turnOnCameraPreview()
            changeSystemBars(requireActivity(), SystemBarColors.DARK)
            hideBottomNavBar(requireActivity())
            hideStatusAndNavBar(requireActivity())
            binding.permissionsLayout.root.visibility = View.GONE
        }
        else {
            showBottomNavBar(requireActivity())
            showStatusAndNavBar(requireActivity())
            binding.permissionsLayout.root.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        changeUIBasedOnPermissions()
    }

    override fun onStop() {
        super.onStop()
        if (arePermissionsGranted(requireContext(), recordVideoPermissions)) {
            showStatusAndNavBar(requireActivity())
            cameraView.close()
            viewModel.stopVideo(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraView.destroy()
    }
}