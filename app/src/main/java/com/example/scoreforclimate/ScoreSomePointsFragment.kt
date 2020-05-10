package com.example.scoreforclimate

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.scoreforclimate.roomDB.Score
import com.example.scoreforclimate.roomDB.ScoreDatabase
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.android.synthetic.main.fragment_scorepoints.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ScoreSomePointsFragment : Fragment(R.layout.fragment_scorepoints) {

    private val cleanUpViewModel: CleanUpsViewModel by viewModels()
    private val scoresDb by lazy {
        ScoreDatabase.getScoreDatabase(requireContext().applicationContext)
    }


    companion object {
        fun newInstance(): ScoreSomePointsFragment {
            val fragment = ScoreSomePointsFragment()
            return fragment
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpButtons()
        setUpViewModelObserver()
    }

    private fun setUpButtons(){
        requestData.setOnClickListener{
            cleanUpViewModel.requestAllCleanUps()
            Toast.makeText(context, "Termine wurden geladen", Toast.LENGTH_LONG).show()
        }
        showCities.setOnClickListener{
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
        }
        else{
            builder.setMessage("Bitte laden Sie zuerst die Termine")
        }
        return builder.create()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpViewModelObserver(){
        val coordinatorLayout = view?.findViewById(R.id.linearLayoutFragmentScorepoints) as LinearLayout
        val allCheckboxes: ArrayList<CheckBox> = arrayListOf()

        cleanUpViewModel.cleanUpInfo.observe(viewLifecycleOwner, Observer{ cleanUpInfo ->
            println("Vorher " +allCheckboxes.size)
        for(i: Int in 0 until allCheckboxes.size){
            coordinatorLayout.removeView(allCheckboxes[i])
        }
        allCheckboxes.clear()
        for (i in cleanUpInfo.indices) {

            val checkParams: CoordinatorLayout.LayoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT
            )
            checkParams.setMargins(0, 25 , 0, 0)
            val checkBox = MaterialCheckBox(context)
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyy")
            val date = LocalDate.parse(cleanUpInfo[i].date, formatter)
            if(date > LocalDate.now()) {
                checkBox.text = cleanUpInfo[i].title + "\n Datum: " + date + "\n Zeit: " + cleanUpInfo[i].time + "\n Treffpunkt: " + cleanUpInfo[i].meetingpoint
                allCheckboxes.add(checkBox)
                println(allCheckboxes.size)
                coordinatorLayout.addView(checkBox, checkParams)
            }
        }
    })

    }
    //Room ----------------------------------------------------------------------------------------------


    //TO DO: For Loop für 5 und 10 Punkte Checkboxen
    private fun onClickSaveScore(){
        var newScore = 0
        val  checkBox2 : List<CheckBox?> = listOf<CheckBox?>(firstButtonValue2, secondButtonValue2)

        for(element in checkBox2){
            if(element?.isChecked!!){
                newScore =+2
            }
        }
        val score = Score()

        score.value = newScore
        //score.timestamp = Timestamp(System.currentTimeMillis())
        scoresDb.scoreDao().insertScore(score)
        parentFragmentManager.popBackStack()

    }


}



