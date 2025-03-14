package com.shamanshs.criminalintent

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.UUID

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), CrimeListFragment.Callback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.)
//            != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CONTACTS), 1)
//        }

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null){
            val fragment = CrimeListFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }

    override fun onCrimeSelected(crimeId: UUID) {
        val fragment = CrimeFragment.newInstance(crimeId)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }
}