package com.example.scoreforclimate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.scoreforclimate.roomDB.Score
import com.example.scoreforclimate.roomDB.ScoreDatabase
import kotlinx.android.synthetic.main.fragment_main.*
import java.text.DateFormat

class MainFragment : Fragment(R.layout.fragment_main){

    private val scoresDb by lazy {
        ScoreDatabase.getScoreDatabase(requireContext().applicationContext)
    }

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment();
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonScorePoints.setOnClickListener { scoreSomePoints() }
        buttonLoadScore.setOnClickListener(){getScoreFromDB()}
    }

    private fun scoreSomePoints() {
        parentFragmentManager.beginTransaction().replace(R.id.fragment_host, ScoreSomePointsFragment.newInstance()).addToBackStack("ScoreSomePoints").commit()
    }

    private fun getScoreFromDB() {

        val scoreDao = scoresDb.scoreDao()
        val score = scoreDao.getScoreById(1)
        //System.out.println(score.value)
        if(score.value != null){
            textViewCurrentScore.text = "You current score is" +" "+ score.value
        }

    }
}