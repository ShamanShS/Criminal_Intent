package com.shamanshs.criminalintent.crimelistviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CrimeListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor().newInstance()
    }
}