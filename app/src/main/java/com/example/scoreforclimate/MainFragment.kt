package com.example.scoreforclimate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.scoreforclimate.roomDB.ScoreDatabase
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main){

    private var currentPointsConnection: CurrentPointsConnection? = null

    private val scoresDb by lazy {
        ScoreDatabase.getScoreDatabase(requireContext().applicationContext)
    }

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindService()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonScorePoints.setOnClickListener { scoreSomePoints() }
        buttonLoadScore.setOnClickListener { setScoreOnDisplay()}
        buttonSetPreconfig.setOnClickListener { openPreferences() }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService()
    }

    //----------------------foreground service-----------------

    private fun bindService() {
        val demoService = Intent(context, CurrentPointService::class.java)
        currentPointsConnection = CurrentPointsConnection()
        currentPointsConnection?.let { it ->
            requireActivity().bindService(demoService, it, Context.BIND_AUTO_CREATE)
        }
    }

    private fun unbindService() {
        requireActivity().unbindService(currentPointsConnection as CurrentPointsConnection)
        currentPointsConnection = null
    }

    //--------------------button clicks----------------------

    private fun scoreSomePoints() {
        parentFragmentManager.beginTransaction().replace(R.id.fragment_host, ScoreSomePointsFragment.newInstance()).addToBackStack("ScoreSomePoints").commit()
    }

    private fun setScoreOnDisplay() {
        textViewCurrentScore.text = "Your current score is ${getScoreFromDB()}"
    }

    private fun openPreferences() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_host, ScorePreferenceFragment.newInstance()).addToBackStack("pref").commit()
    }

    //-------------------methods-----------------------------

    private fun getScoreFromDB() :Int {
        var points = -1
        try {
            val scoreDao = scoresDb.scoreDao()
            val score = scoreDao.getScoreById(1)
            println(score.value)
            if (score.value != null) {
                points = score.value!!
                currentPointsConnection?.getCurrentPointsApi()?.showPoints(points)
            }
        } catch (e: NullPointerException) {
            points = 0
        }
        return points
    }
}