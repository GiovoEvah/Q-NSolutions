package com.example.qnsolutions.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.qnsolutions.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatePickerFragment(private val bundleKey: String, private val requestKey: String, private val minDate: Long?, private val maxDate: Long?) : DialogFragment()
{
    private val calendar: Calendar = Calendar.getInstance()
    lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentCalendarBinding.inflate(inflater)
        if (minDate != null)
        {
            binding.dataPrenotazioneCalendar.minDate = minDate
        }

        if (maxDate != null)
        {
            binding.dataPrenotazioneCalendar.maxDate = maxDate
        }
        binding.dataPrenotazioneCalendar.setOnDateChangeListener()
        { _, year, month, day ->

            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val result = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(calendar.time)
            setFragmentResult(requestKey, bundleOf(bundleKey to result))
            dismiss()
        }

        dialog?.setCanceledOnTouchOutside(true)

        return binding.root
    }


}