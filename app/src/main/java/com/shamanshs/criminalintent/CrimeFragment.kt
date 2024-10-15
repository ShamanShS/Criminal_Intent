package com.shamanshs.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.icu.text.DateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shamanshs.criminalintent.crimeviewmodel.CrimeDetailViewModel
import com.shamanshs.criminalintent.crimeviewmodel.CrimeDetailViewModelFactory
import java.util.Date
import java.util.UUID

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val REQUEST_DATE = "dialog_date"
private const val REQUEST_CONTACT = "1"
private const val DATE_FORMAT = "EEE, MMM, dd"

class CrimeFragment : Fragment(), FragmentResultListener {
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var callButton: Button

    private val crimeDetailViewModel : CrimeDetailViewModel by lazy {
        val factory = CrimeDetailViewModelFactory()
        ViewModelProvider(this@CrimeFragment, factory)[CrimeDetailViewModel::class.java]
    }

    private val pickContact = registerForActivityResult(ActivityResultContracts.PickContact()) { contactUri ->
        val queryFields = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME)
        val id: String
        val cursor = contactUri?.let {
            requireActivity().contentResolver.query(it, queryFields, null, null, null)
        }
        cursor?.use {
            if (it.count > 0) {
                it.moveToFirst()
                val suspect = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                id = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                crime.suspect = "$suspect/"

                val cursor2 = requireActivity().contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null)
                if(cursor2!!.moveToNext()){
                        val number =
                            cursor2.getString(cursor2.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        crime.suspect += number
                    }
                crimeDetailViewModel.saveCrime(crime)
                }

            }


//        val numberField = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
//        val cursorNum = contactUri?.let {
//            requireActivity().contentResolver.query(it, numberField, null, null, null)
//        }
//        cursorNum?.use {
//            if (it.count > 0) {
//                it.moveToFirst()
//                val number = it.getString(0)
//                crime.suspect += "/$number"
//                crimeDetailViewModel.saveCrime(crime)
//                callButton.text = number
//            }
//        }
        updateUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById<EditText>(R.id.crime_title)!!
        dateButton = view.findViewById<Button>(R.id.crime_date)!!
        solvedCheckBox = view.findViewById(R.id.crime_solved)
        reportButton = view.findViewById<Button>(R.id.crime_report)!!
        suspectButton = view.findViewById<Button>(R.id.crime_suspect)!!
        callButton = view.findViewById<Button>(R.id.call_button)!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let{
                    this.crime = crime
                    updateUI()
                }
            }
        )
        childFragmentManager.setFragmentResultListener(REQUEST_DATE, viewLifecycleOwner, this)
    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }
            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = sequence.toString()
            }
            override fun afterTextChanged(sequence: Editable?) {

            }
        }
        titleField.addTextChangedListener(titleWatcher)

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date, REQUEST_DATE).show(childFragmentManager, REQUEST_DATE)
        }

        solvedCheckBox.apply {
            setOnCheckedChangeListener{_, isChecked ->
                crime.isSolved = isChecked
            }
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        suspectButton.setOnClickListener {
            pickContact.launch(null)
        }

        callButton.setOnClickListener {
            val num: Uri = Uri.parse("tel:" + crime.suspect.substringAfter("/"))
            Intent(Intent.ACTION_DIAL).apply {
                setData(num)
            }.also { intent: Intent ->
                val chooserIntent = Intent.createChooser(intent, crime.suspect.substringAfter("/"))
                startActivity(chooserIntent)
            }
        }

        val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolvedActivity == null) {
            suspectButton.isEnabled = true
        }

        if (callButton.text.isEmpty()) {
            callButton.isEnabled = false
        }
        else {
            callButton.isEnabled = true
        }
    }


    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }


    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotBlank()) {
            suspectButton.text = crime.suspect.substringBefore("/")
            callButton.text = crime.suspect.substringAfter("/")
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = android.text.format.DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }


    companion object{
        fun newInstance(crimeId: UUID) : CrimeFragment{
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID,crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }


    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when(requestKey) {
            REQUEST_DATE -> {
                Log.d(TAG, "received result for $requestKey")
                crime.date = DatePickerFragment.getSelectedDate(result)!!
                updateUI()
            }
        }
    }
}