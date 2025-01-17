package com.example.scoreforclimate

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.scoreforclimate.rest.CleanUpsViewModel
import com.example.scoreforclimate.roomDB.History
import com.example.scoreforclimate.roomDB.Score
import com.example.scoreforclimate.roomDB.ScoreDatabase
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.android.synthetic.main.fragment_scorepoints.*
import java.sql.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ScoreSomePointsFragment : Fragment(R.layout.fragment_scorepoints) {

    private val cleanUpViewModel: CleanUpsViewModel by viewModels()
    private val scoresDb by lazy {
        ScoreDatabase.getScoreDatabase(requireContext().applicationContext)
    }
    private var newScore = 0
    private var historyId : Long = 0


    companion object {
        fun newInstance(): ScoreSomePointsFragment {
            return ScoreSomePointsFragment()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpButtons()
        setUpViewModelObserver()
        cleanUpViewModel.requestAllCleanUps()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpButtons() {
        showCities.setOnClickListener {
            showAllCitites().show()
        }
        saveScore.setOnClickListener {
            onClickSaveScore()
        }
    }

    //REST-API --------------------------------------------------------------------------------------------------------------------


    private fun showAllCitites(): Dialog {
        val builder = AlertDialog.Builder(context)
        if (cleanUpViewModel.cities.value != null) {
            builder.setTitle("Welche Städte?")
            val cleanUps = cleanUpViewModel.cities.value
            if (cleanUps != null) {
                val items: Array<String> = cleanUps.map { cityCode -> cityCode.name }.toTypedArray()
                builder.setItems(items) { _, cleanUpWahl -> //der zweite Parameter zeigt an, auf was man klicken kann
                    cleanUpViewModel.requestCleanUpInfo(cleanUps[cleanUpWahl].code)
                }
            }
        } else {
            builder.setMessage("Bitte laden Sie zuerst die Termine")
        }
        return builder.create()
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpViewModelObserver(): ArrayList<CheckBox> {
        val coordinatorLayout =
            view?.findViewById(R.id.linearLayoutFragmentScorepoints) as LinearLayout
        val allCheckboxes: ArrayList<CheckBox> = arrayListOf()

        cleanUpViewModel.cleanUpInfo.observe(viewLifecycleOwner, Observer { cleanUpInfo ->
            for (i: Int in 0 until allCheckboxes.size) {
                coordinatorLayout.removeView(allCheckboxes[i])
            }
            allCheckboxes.clear()

            var counter = 0
            for (i in cleanUpInfo.indices) {

                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyy")
                val date = LocalDate.parse(cleanUpInfo[i].date, formatter)
                if (date > LocalDate.now()) {
                    val checkParams: CoordinatorLayout.LayoutParams = CoordinatorLayout.LayoutParams(
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT
                    )
                checkParams.setMargins(0, 25, 0, 0)
                val checkBox = MaterialCheckBox(context)
                checkBox.id = counter

                    checkBox.text =
                        cleanUpInfo[counter].title + "\n Datum: " + date + "\n Zeit: " + cleanUpInfo[counter].time + "\n Treffpunkt: " + cleanUpInfo[counter].meetingpoint
                    allCheckboxes.add(checkBox)
                    coordinatorLayout.addView(checkBox, checkParams)
                    counter++
                }
            }

        })
        return allCheckboxes
    }
    //Room ----------------------------------------------------------------------------------------------


    //TO DO: For Loop für 5 und 10 Punkte Checkboxen
    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClickSaveScore() {
        val checkBox2: List<CheckBox?> = listOf<CheckBox?>(firstButtonValue2, secondButtonValue2)
        val checkBox5: List<CheckBox?> = listOf<CheckBox?>(firstButtonValue5, secondButtonValue5)
        val checkBox10: List<CheckBox?> = listOf<CheckBox?>(firstButtonValue10, secondButtonValue10)
        val checkBox50: List<CheckBox?> = listOf<CheckBox?>(firstButtonValue50)
        val listActions: MutableList<String?> = ArrayList()
        val range: Int = linearLayoutFragmentScorepoints.size

        //10 points section

        for (i: Int in 0 until range) {
            var onlyTitleCleanUp: String?
            val checkBox: CheckBox? = view?.findViewById(i)
            if(checkBox?.isChecked!!){
                newScore +=10
                val splittedTextPart0: List<String?> = checkBox.text.toString().split("\n")
                onlyTitleCleanUp = splittedTextPart0[0]
                listActions.add(onlyTitleCleanUp)
            }
        }

        for (i: Int in checkBox10.indices) {
            if (checkBox10[i]?.isChecked!!) {
                newScore += 10
                if (checkBox10[i] == firstButtonValue10) {
                    listActions.add("Velo:Einkaufen, zur Arbeit...")
                }
                if (checkBox10[i] == secondButtonValue10) {
                    listActions.add("Zu Fuss:Einkaufen, zur Arbeit...")
                }
            }
        }

        //2 points section

        for (i: Int in checkBox2.indices) {
            if (checkBox2[i]?.isChecked!!) {
                newScore += 2
                if (checkBox2[i] == firstButtonValue2) {
                    listActions.add("Einkaufen mit eigener Einkaufstasche")
                }
                if (checkBox2[i] == secondButtonValue2) {
                    listActions.add("Gemüse ohne Plastikverpackung kaufen")
                }
            }
        }

        //5 points section

        for (i: Int in checkBox5.indices) {
            if (checkBox5[i]?.isChecked!!) {
                newScore += 5
                if (checkBox5[i] == firstButtonValue5) {
                    listActions.add("Ausnahmsweise für einen Tag vegetarisch ernähren")
                }
                if (checkBox5[i] == secondButtonValue5) {
                    listActions.add("Mit ÖV gereist")
                }
            }
        }

        //50 points section

        for (i: Int in checkBox50.indices) {
            if (checkBox50[i]?.isChecked!!) {
                newScore += 50
                if (checkBox50[i] == firstButtonValue50) {
                    listActions.add("Mit dem Zug reisen anstelle des Flugzeuges")
                }
            }
        }

        //save to database

        val score = Score()
        score.value = newScore
        try{
            if (scoresDb.scoreDao().getScoreById(1).value != null) {
                score.value = newScore + scoresDb.scoreDao().getScoreById(1).value!!
                scoresDb.scoreDao().updateScore(1, score.value)
            }
        }catch(e: NullPointerException){
            scoresDb.scoreDao().insertScore(score)
        }
        if(newScore!=0) {
            Toast.makeText(activity, "Cool! Du hast soeben $newScore Punkte gesammelt.",
            Toast.LENGTH_LONG).show()
        }
        saveHistory(listActions)
        parentFragmentManager.popBackStack()
    }

    private fun saveHistory(listActions: MutableList<String?>){
        if (listActions.isNotEmpty()) {
            val date = Date(System.currentTimeMillis())
            val history = History(historyId, 1, date, listActions)
            scoresDb.historyDao().insertHistory(history)
            historyId++
            parentFragmentManager.popBackStack()
        }
    }

}





