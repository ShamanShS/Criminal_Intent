package com.shamanshs.criminalintent.crimelistviewmodel

import androidx.lifecycle.ViewModel
import com.shamanshs.criminalintent.Crime
import com.shamanshs.criminalintent.CrimeRepository

class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()

    fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }

}