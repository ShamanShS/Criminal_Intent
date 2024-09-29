package com.shamanshs.criminalintent.crimeviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CrimeDetailViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor().newInstance()
    }
}