package com.example.scoreforclimate

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.scoreforclimate.preferences.PreconfigViewModel
import com.example.scoreforclimate.preferences.ScorePreferenceFragment
import com.example.scoreforclimate.roomDB.History
import com.example.scoreforclimate.roomDB.HistoryFragment
import com.example.scoreforclimate.roomDB.Score
import com.example.scoreforclimate.roomDB.ScoreDatabase
import kotlinx.android.synthetic.main.fragment_main.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

class MainFragment : Fragment(R.layout.fragment_main) {

    private var currentPointsConnection: CurrentPointsConnection? = null

    private val scoresDb by lazy {
        ScoreDatabase.getScoreDatabase(requireContext().applicationContext)
    }
    private val preconfigPreferencesViewModel : PreconfigViewModel by activityViewModels()

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindService()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonScorePoints.setOnClickListener { scoreSomePoints() }
        buttonSetPreconfig.setOnClickListener { openPreferences() }
        buttonShowHistory.setOnClickListener { getHistoryFragment() }
        preconfigPreferencesViewModel.setScorePrefValue()
        calculateScore()
        setScoreOnDisplay()
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
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_host, ScoreSomePointsFragment.newInstance())
            .addToBackStack("ScoreSomePoints").commit()
    }

    private fun setScoreOnDisplay() {
        val points = getScoreFromDB()
        if (points != 0) {
            textViewCurrentScore.text = "Dein Punktestand ist " + points
        } else {
            textViewCurrentScore.text = "Du hast 0 Punkte"
        }
    }

    private fun openPreferences() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_host, ScorePreferenceFragment.newInstance())
            .addToBackStack("pref").commit()
    }

    private fun getHistoryFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_host, HistoryFragment())
            .addToBackStack("hist").commit()
    }

    //-------------------methods-----------------------------

    private fun getScoreFromDB(): Int {
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

    //Preferences -----------------------------------------------------------------------------------




    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateScore() {
        var preconfigScore = 0
        val preferencesValue = preconfigPreferencesViewModel.setScorePrefValue()
        System.out.println(preferencesValue)
        if(preferencesValue["checkBoxPreferenceVegetarian"] == true  && preferencesValue["checkBoxPreferenceVegan"] == false){
            preconfigScore += 10
        }
        if(preferencesValue["checkBoxPreferenceVegan"] == true && preferencesValue["checkBoxPreferenceVegetarian"] == false){
            preconfigScore += 15
        }
        if(preferencesValue["checkBoxPreferencePet"] == true){
            preconfigScore += 2
        }
        if(preferencesValue["checkBoxPreferenceAlu"] == true){
            preconfigScore += 2
        }
        if(preferencesValue["checkBoxPreferenceCardboard"] == true){
            preconfigScore += 2
        }
        if(preferencesValue["checkBoxPreferenceCardboard"] == false  && preferencesValue["checkBoxPreferenceAlu"] == false && preferencesValue["checkBoxPreferencePet"]== false){
            Toast.makeText(activity, "Hallo du recycelst also noch nichts? Fang jetzt damit an und sammle Punkte :)",
                LENGTH_LONG).show()
        }
        if( preferencesValue["listPreference"] == 1){
            preconfigScore += 5
        }
        if(preferencesValue["checkBoxPreferenceVegan"]== true && preferencesValue["checkBoxPreferenceVegetarian"]== true){
            Toast.makeText(activity, "Du kannst nicht Vegetarier und Veganer sein. Bitte entscheide dich ;)",LENGTH_LONG).show()
        }

        savePointsToDatabase(preconfigScore)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun savePointsToDatabase(score: Int){
        var newScorePreConfig = score
        val timestampNow: LocalDate = LocalDate.now()
        var latestDate: Date?
        try {
            latestDate = scoresDb.historyDao().getLatestHistory().modificationDate
        } catch (n: java.lang.NullPointerException) {
            latestDate = Date()
            Log.i("noPreconfig", "There is no preconfig, first Start")
        }

        val latestDateLocalDate: LocalDate? = latestDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        val difference: Long = ChronoUnit.DAYS.between(latestDateLocalDate,timestampNow)
        if(difference == 0L){
            Log.i("preConfig","Seit dem letzten Login ist noch kein Tag vergangen. Es werden keine neuen Punkte der preconfig in die DB geschrieben")
        }
        else{
            System.out.println(difference)
            newScorePreConfig *= difference.toInt()
            val newScore = Score()
            newScore.value = newScorePreConfig
            try{
                if (scoresDb.scoreDao().getScoreById(1).value != null) {
                    newScore.value = newScore.value!! + scoresDb.scoreDao().getScoreById(1).value!!
                    scoresDb.scoreDao().updateScore(1, newScore.value)
                    savePrecofigHistory(difference, newScorePreConfig)
                }
            }catch(e: NullPointerException){
                scoresDb.scoreDao().insertScore(newScore)
            }
            Toast.makeText(activity, "Cool! Du erhältst für deine gesetzen Preconfig $newScorePreConfig neue Punkte :)",LENGTH_LONG).show()
        }

    }

    fun savePrecofigHistory(difference: Long, newScore : Int ){
        val listActions: MutableList<String?> = ArrayList<String?>()
        listActions.add("In den letzten $difference Tagen hast du mit preconfig $newScore Punkte erzielt")
        var historyId = scoresDb.historyDao().getLatestId().historyId
        historyId++
        val date = java.sql.Date(System.currentTimeMillis())
        val history = History(historyId,1, date, listActions)
        scoresDb.historyDao().insertHistory(history)
    }

}