package com.example.scoreforclimate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main){

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment();
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonScorePoints.setOnClickListener { scoreSomePoints() }
    }

    private fun scoreSomePoints() {
        parentFragmentManager.beginTransaction().replace(R.id.fragment_host, ScoreSomePointsFragment.newInstance()).addToBackStack("ScoreSomePoints").commit()
    }
}