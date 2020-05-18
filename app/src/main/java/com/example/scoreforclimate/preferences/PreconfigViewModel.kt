package com.example.scoreforclimate.preferences

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class PreconfigViewModel (application: Application): AndroidViewModel(application){

    private val RECYCLE_ALU = "checkBoxPreferencePet"
    private val RECYCLE_PET = "checkBoxPreferenceAlu"
    private val RECYCLE_CARTON ="checkBoxPreferenceCardboard"
    private val RECYCLE_NOTHING = "checkBoxPreferenceNothing"

    private val COMPOST = "categoryCompost"
    private val VEGETARIAN ="checkBoxPreferenceVegetarian"
    private val VEGAN ="checkBoxPreferenceVegan"

    fun setScorePrefValue() : Map<String,*> {
        val preconfigPref =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(getApplication()).all
        return preconfigPref
    }
}