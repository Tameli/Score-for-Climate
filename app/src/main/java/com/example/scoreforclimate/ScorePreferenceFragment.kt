package com.example.scoreforclimate

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

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