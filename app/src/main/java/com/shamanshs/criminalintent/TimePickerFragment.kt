package com.shamanshs.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.time.Clock
import java.util.Date


private const val ARG_TIME = "time"
private const val REQUEST_DATE = "dialog_time"
private const val ARG_REQUEST_CODE = "requestCode"
private const val RESULT_TIME_KEY = "timeKey"

class TimePickerFragment : DialogFragment() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_TIME, Date::class.java)!!
        } else {
            arguments?.getSerializable(ARG_TIME) as Date
        }

        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val initialHour = calendar.get(Calendar.HOUR)
        val initialMinute = calendar.get(Calendar.MINUTE)

        val timeListener = TimePickerDialog.OnTimeSetListener {
                _: TimePicker, hour: Int, minute: Int ->
            val resultTime: Date = GregorianCalendar(year, month, day, hour, minute).time

            val result = Bundle().apply {
                putSerializable(RESULT_TIME_KEY, resultTime)
            }

            val resultRequestCode = requireArguments().getString(ARG_REQUEST_CODE, "")
            setFragmentResult(resultRequestCode, result)
        }

        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHour,
            initialMinute,
            true)
    }


    companion object{
        fun newInstance(time: Date, requestCode: String): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TIME, time)
                putString(ARG_REQUEST_CODE, requestCode)
            }
            return TimePickerFragment().apply {
                arguments = args
            }
        }

        fun getSelectedTime(result: Bundle) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            result.getSerializable(RESULT_TIME_KEY, Date::class.java)
        }
        else {
            result.getSerializable(RESULT_TIME_KEY) as Date
        }
    }
}