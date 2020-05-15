package com.example.scoreforclimate.roomDB

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreforclimate.R
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import java.sql.Date

class HistoryFragment: Fragment(R.layout.fragment_history) {

    private val scoresDb by lazy {
        ScoreDatabase.getScoreDatabase(requireContext().applicationContext)
    }

    var history = arrayOf<History>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        history = getHistoryFromDB()

        val recyclerView = view.findViewById<TimeLineRecyclerView>(R.id.recycler_view)

        recyclerView?.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,
            false)

        //Add RecyclerSectionItemDecoration.SectionCallback
        recyclerView?.addItemDecoration(getSectionCallback(history))

        //Set Adapter
        recyclerView?.adapter = HistoryPointAdapter(layoutInflater,
            history,
            R.layout.recycler_row)
    }


    private fun getSectionCallback(historyList: Array<History>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean =
                historyList[position].modificationDate != historyList[position - 1].modificationDate

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo? =
                SectionInfo(historyList[position].modificationDate.toString(), "")
        }
    }

    private fun getHistoryFromDB():Array<History> {
        return scoresDb.historyDao().loadAllHistories()
    }

}