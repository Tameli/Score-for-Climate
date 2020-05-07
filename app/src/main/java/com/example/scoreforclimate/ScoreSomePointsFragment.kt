package com.example.scoreforclimate

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.ScrollView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.scoreforclimate.roomDB.Score
import com.example.scoreforclimate.roomDB.ScoreDatabase
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.android.synthetic.main.fragment_scorepoints.*


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
        setUpViewModelOberserver()
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

    private fun setUpViewModelOberserver(){

        var coordinatorLayout = view?.findViewById(R.id.scorePoints) as CoordinatorLayout
        var counter: Int = 0
        val allCheckboxes = arrayListOf<CheckBox>()


        cleanUpViewModel.cleanUpInfo.observe(viewLifecycleOwner, Observer{ cleanUpInfo ->
            System.out.println("Vorher " +allCheckboxes.size)
        for(i: Int in 0 until allCheckboxes.size){
            coordinatorLayout.removeView(allCheckboxes[i])
        }
            allCheckboxes.clear()
            var yCoordinate : Int = 0
        for (i in 0 until cleanUpInfo.size) {
            val sv = ScrollView(context)
            //sv.layoutParams =
                //CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT)
            val checkParams: CoordinatorLayout.LayoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT
            )
            checkParams.setMargins(40, 1100+yCoordinate, 100, 5)
            checkParams.gravity = Gravity.CENTER
            checkParams.height= 400
            val checkBox = MaterialCheckBox(context)
            checkBox.setText(cleanUpInfo[i].title +"\n Datum: "+ cleanUpInfo[i].date +"Zeit: "+ cleanUpInfo[i].time +"\n Treffpunkt: "+ cleanUpInfo[i].meetingpoint)
            allCheckboxes.add(checkBox)
            System.out.println(allCheckboxes.size)
            coordinatorLayout.addView(checkBox, checkParams)
            //coordinatorLayout.addView(sv)
            yCoordinate =+400
        }
    })

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


