package com.example.scoreforclimate.preferences

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager

class PreconfigViewModel (application: Application): AndroidViewModel(application){

    fun setScorePrefValue() : Map<String,*> {
        return PreferenceManager.getDefaultSharedPreferences(getApplication()).all
    }
}