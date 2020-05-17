package com.example.scoreforclimate

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.scoreforclimate.preferences.ScorePreferenceFragment
import com.example.scoreforclimate.preferences.PreconfigViewModel
import com.example.scoreforclimate.roomDB.History
import com.example.scoreforclimate.roomDB.HistoryFragment
import com.example.scoreforclimate.roomDB.Score
import com.example.scoreforclimate.roomDB.ScoreDatabase
import kotlinx.android.synthetic.main.fragment_main.*
import java.text.DateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.DAYS

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
        setScoreOnDisplay()
        buttonShowHistory.setOnClickListener { getHistoryFragment() }
        preconfigPreferencesViewModel.setScorePrefValue()
        calculateScore()

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
        textViewCurrentScore.text = "Your current score is ${getScoreFromDB()}"
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
        var preferencesValue : Map<kotlin.String, *> = preconfigPreferencesViewModel.setScorePrefValue()
        if(preferencesValue.get("checkBoxPreferenceVegetarian") != null && preferencesValue.get("checkBoxPreferenceVegetarian") as Boolean){
            preconfigScore += 10
        }
        if(preferencesValue.get("checkBoxPreferenceVegan") != null && preferencesValue.get("checkBoxPreferenceVegan") as Boolean){
            preconfigScore += 15
        }
        if(preferencesValue.get("checkBoxPreferencePet") != null && preferencesValue.get("checkBoxPreferencePet") as Boolean){
            preconfigScore += 2
        }
        if(preferencesValue.get("checkBoxPreferenceAlu") != null && preferencesValue.get("checkBoxPreferenceAlu") as Boolean){
            preconfigScore += 2
        }
        if(preferencesValue.get("checkBoxPreferenceCardboard") != null && preferencesValue.get("checkBoxPreferenceCardboard") as Boolean){
            preconfigScore += 2
        }
        if(preferencesValue.get("checkBoxPreferenceNothing") != null && preferencesValue.get("checkBoxPreferenceNothing") as Boolean){
            Toast.makeText(activity, "Hallo du recycelst also noch nichts? Cool, dass du dir diese App geholt hast! Frohes Punkte Sammeln! :)",LENGTH_LONG).show()
        }
        if(preferencesValue.get("listPreference") != null && preferencesValue.get("listPreference") == 1){
            preconfigScore += 5
        }
        System.out.println(preconfigScore)
        System.out.println(preferencesValue)
        savePointsToDatabase(preconfigScore)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun savePointsToDatabase(score: Int){
        var newScorePreConfig = score
        var timestampNow: LocalDate = LocalDate.now()
        val df = DateFormat.getDateInstance()

        var latestDate: Date? = scoresDb.historyDao().getLatestHistory().modificationDate
        var latestDateLocalDate: LocalDate? = latestDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate();
        var difference: Long = ChronoUnit.DAYS.between(timestampNow,latestDateLocalDate)
        //var difference : Long = 2
        if(difference == 0L){
            Log.i("preConfig","Seit dem letzten Login ist noch kein Tag vergangen. Es werden keine neuen Punkte der preconfig in die DB geschrieben")
        }
        else{
            newScorePreConfig *= difference.toInt()
            var newScore = Score()
            newScore.value = newScorePreConfig
            try{
                if (scoresDb.scoreDao().getScoreById(1).value != null) {
                    newScore.value = newScore.value!! + scoresDb.scoreDao().getScoreById(1).value!!
                    scoresDb.scoreDao().updateScore(1, newScore.value)
                }
            }catch(e: NullPointerException){
                scoresDb.scoreDao().insertScore(newScore)
            }
            Toast.makeText(activity, "Cool! Du erhältst für deine gesetzen Preconfig $newScorePreConfig neue Punkte :)",LENGTH_LONG).show()
        }

    }

}