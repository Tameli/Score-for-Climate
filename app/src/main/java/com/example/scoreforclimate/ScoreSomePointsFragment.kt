package com.example.scoreforclimate

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_scorepoints.*
import androidx.lifecycle.Observer

class ScoreSomePointsFragment : Fragment(R.layout.fragment_scorepoints) {

    companion object {
        fun newInstance(): ScoreSomePointsFragment {
            return ScoreSomePointsFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpButtons()
        setUpViewModelObservers()
    }

    private val cleanUpViewModel: CleanUpsViewModel by activityViewModels()


    fun setUpButtons(){
        requestData.setOnClickListener(){
            cleanUpViewModel.requestAllCleanUps()
            Toast.makeText(context, "Termine wurden geladen", Toast.LENGTH_LONG).show()
        }
        showCities.setOnClickListener(){
         showAllCitites().show()
        }
    }
    //REST-API

    fun showAllCitites(): Dialog {
        val builder = AlertDialog.Builder(context)
        if (cleanUpViewModel.cities.value != null) {
            builder.setTitle("Welche Städte?")
            val cleanUps = cleanUpViewModel.cities.value
            if (cleanUps != null) {
                val items: Array<String> = cleanUps.map { cityCode -> cityCode.code }.toTypedArray()
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

    @SuppressLint("SetTextI18n")
    private fun setUpViewModelObservers(){

        cleanUpViewModel.cleanUpInfo.observe(viewLifecycleOwner, Observer { cleanUpInfo ->
            showCleanUpInfo.text = cleanUpInfo?.title ?: ""
            if(cleanUpInfo != null){
                showCleanUpInfo.text = cleanUpInfo.title + ", Datum: " + ", Uhrzeit: "  +", Meetingpoint: "
            }else {
                showCleanUpInfo.text = ""
            }
        })

    }


}