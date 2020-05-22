package com.example.scoreforclimate.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.scoreforclimate.R
import com.example.scoreforclimate.roomDB.ScoreDatabase

class ScorePreferenceFragment : PreferenceFragmentCompat() {

    companion object {
        fun newInstance(): ScorePreferenceFragment {
            return ScorePreferenceFragment()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}