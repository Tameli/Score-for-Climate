package com.example.scoreforclimate

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ListView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_scorepoints.*
import androidx.lifecycle.observe
import com.example.scoreforclimate.roomDB.Score
import com.example.scoreforclimate.roomDB.ScoreDatabase
import java.sql.Timestamp
import java.util.*


class ScoreSomePointsFragment : Fragment(R.layout.fragment_scorepoints) {

    private val cleanUpViewModel: CleanUpsViewModel by viewModels()
    private val scoresDb by lazy {
        ScoreDatabase.getScoreDatabase(requireContext().applicationContext)
    }


    companion object {
        fun newInstance(): ScoreSomePointsFragment {
            return ScoreSomePointsFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpButtons()
        setUpViewModelObservers()
        requireActivity().startService(Intent(context, CurrentPointService::class.java))

    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().stopService(Intent(context, CurrentPointService::class.java))
    }


    fun setUpButtons(){
        requestData.setOnClickListener(){
            cleanUpViewModel.requestAllCleanUps()
            Toast.makeText(context, "Termine wurden geladen", Toast.LENGTH_LONG).show()
        }
        showCities.setOnClickListener(){
         showAllCitites().show()
        }
        saveScore.setOnClickListener {
            onClickSaveScore()
        }
    }

    //REST-API --------------------------------------------------------------------------------------------------------------------


    fun showAllCitites(): Dialog {
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

    //TO DO: Liste auslesen und in TextView oder ListView einfügen
    private fun setUpViewModelObservers(){
        cleanUpViewModel.cleanUpInfo.observe(viewLifecycleOwner, Observer{ cleanUpInfo ->
            System.out.println(cleanUpInfo.size)
            when(cleanUpInfo.size){
                null -> setInvisible(4)
                1 -> { setInvisible(3)
                    firstCleanup.text = getCleanupInfoAsString(0, cleanUpInfo)
                }
                2 -> { setInvisible(2)
                    firstCleanup.text = getCleanupInfoAsString(0, cleanUpInfo)
                    secondCleanup.text = getCleanupInfoAsString(1, cleanUpInfo)
                }
                3 ->{ setInvisible(1)
                    firstCleanup.text = getCleanupInfoAsString(0, cleanUpInfo)
                    secondCleanup.text = getCleanupInfoAsString(1, cleanUpInfo)
                    thirdCleanup.text = getCleanupInfoAsString(2, cleanUpInfo)
                }
                4 -> {
                    firstCleanup.text = getCleanupInfoAsString(0, cleanUpInfo)
                    secondCleanup.text = getCleanupInfoAsString(1, cleanUpInfo)
                    thirdCleanup.text = getCleanupInfoAsString(2, cleanUpInfo)
                    forthCleanup.text = getCleanupInfoAsString(3, cleanUpInfo)
                }

            }
        })

    }

    private fun getCleanupInfoAsString (i: Int, cleanUpInfo: List<CleanUpInfo>) : String {
        return cleanUpInfo[i].title +"\n Datum: "+ cleanUpInfo[i].date+"\n Zeit: "+ cleanUpInfo[i].time +"\n Treffpunkt: "+ cleanUpInfo[i].meetingpoint
    }

    private fun setInvisible(i: Int){
        when(i){
            1 -> { forthCleanup.text = ""
                forthCleanup.setVisibility(View.GONE)
                firstCleanup.setVisibility(View.VISIBLE)
                secondCleanup.setVisibility(View.VISIBLE)
                thirdCleanup.setVisibility(View.VISIBLE)
            }
            2 -> { thirdCleanup.text = ""
                forthCleanup.text=""
                forthCleanup.setVisibility(View.GONE)
                thirdCleanup.setVisibility(View.GONE)
                firstCleanup.setVisibility(View.VISIBLE)
                secondCleanup.setVisibility(View.VISIBLE)
            }
            3 -> {
                secondCleanup.text = ""
                thirdCleanup.text = ""
                forthCleanup.text=""
                secondCleanup.setVisibility(View.GONE)
                forthCleanup.setVisibility(View.GONE)
                thirdCleanup.setVisibility(View.GONE)
                firstCleanup.setVisibility(View.VISIBLE)
            }
            4 -> {
                firstCleanup.text = ""
                secondCleanup.text = ""
                thirdCleanup.text = ""
                forthCleanup.text = ""
                firstCleanup.setVisibility(View.GONE)
                secondCleanup.setVisibility(View.GONE)
                forthCleanup.setVisibility(View.GONE)
                thirdCleanup.setVisibility(View.GONE)
            }
        }


    }

    //Room ----------------------------------------------------------------------------------------------


    //TO DO: For Loop für 5 und 10 Punkte Checkboxen
    fun onClickSaveScore(){
        var newScore : Int = 0
        val  checkBox2 : List<CheckBox?> = listOf<CheckBox?>(firstButtonValue2, secondButtonValue2)

        for(x in 0 ..(checkBox2.size-1)){
            if(checkBox2[x]?.isChecked!!){
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


