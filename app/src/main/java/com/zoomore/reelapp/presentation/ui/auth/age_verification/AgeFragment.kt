package com.zoomore.reelapp.presentation.ui.auth.age_verification

import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.FragmentAgeBinding
import com.zoomore.reelapp.models.CustomDate
import com.zoomore.reelapp.utils.ResUtils.showSnackBar
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AgeFragment : Fragment(R.layout.fragment_age) {

    lateinit var binding: FragmentAgeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAgeBinding.bind(view)

        binding.backBtn.setOnClickListener { findNavController().popBackStack() }

        binding.datePicker2.init(2018, 1, 23, dateChangedLambda)
        binding.datePicker2.maxDate = System.currentTimeMillis()

        val year = Calendar.getInstance(Locale.getDefault()).let {
            it.time = Date()
            it.get(Calendar.YEAR)
        }

        binding.signUpBtn.setOnClickListener {
            when {
                date.year > year - 9 -> showSnackBar(requireView(), R.string.user_too_young)
                date.year < year - 150 -> showSnackBar(requireView(), R.string.enter_valid_year)
                else -> findNavController()
                    .navigate(AgeFragmentDirections.actionAgeFragmentToSelectBasicSignUpFragment())
            }
        }
    }

    private val date = CustomDate(23, 1, 2018)

    private val dateChangedLambda =
        DatePicker.OnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            date.date = dayOfMonth
            date.month = monthOfYear
            date.year = year
        }
}