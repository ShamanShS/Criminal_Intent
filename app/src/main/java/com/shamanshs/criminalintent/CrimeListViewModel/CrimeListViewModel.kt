package com.shamanshs.criminalintent.CrimeListViewModel

import androidx.lifecycle.ViewModel
import com.shamanshs.criminalintent.Crime

class CrimeListViewModel : ViewModel() {
    val crimes = mutableListOf<Crime>()

    init {
        for (i in 0 until 100) {
            val crime = Crime()
            crime.title = "Crime#$i"
            crime.isSolved = i % 2 == 0
            crime.requiresPolice = i % 4 == 0
            crimes += crime

        }
    }
}