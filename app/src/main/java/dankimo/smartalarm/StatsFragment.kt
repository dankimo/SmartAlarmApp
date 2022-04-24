package dankimo.smartalarm

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dankimo.smartalarm.databinding.FragmentStatsBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class StatsFragment : Fragment() {
    private lateinit var binding: FragmentStatsBinding
    private val timesSet : MutableList<LocalDateTime>? = null
    private val timesStopped : MutableList<LocalDateTime>? = null
    private var timesSetDataSet : LineDataSet? = null
    private var timesStoppedDataSet : LineDataSet? = null
    private var goalTimeDataSet : LineDataSet? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentStatsBinding.inflate(layoutInflater)

        var title : TextView = activity!!.findViewById(R.id.toolbar_title)
        title.text = "Stats"

        createDataSets()
        val chart = binding.chart
        val lineData = LineData()
        lineData.addDataSet(timesSetDataSet)
        lineData.addDataSet(timesStoppedDataSet)
        lineData.addDataSet(goalTimeDataSet)

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun populateChart()
    {
        val chart = binding.chart;


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createDataSets()
    {
        // Create Goal Time DataSet
        val goalTimeEntries : List<Entry>? = createGoalTimeEntries()
        // Create Times Set Data Set
        val timesSetEntries : List<Entry>? = createTimeDataSet(
            timesSet as List<LocalDateTime>)
        // Create Times Stopped Data Set
        val timesStoppedEntries : List<Entry>? = createTimeDataSet(
            timesStopped as List<LocalDateTime>)

        timesSetDataSet = LineDataSet(timesSetEntries, "Time Set")
        timesStoppedDataSet = LineDataSet(timesStoppedEntries, "Time Stopped")
        goalTimeDataSet = LineDataSet(goalTimeEntries, "Goal Time")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createGoalTimeEntries() : List<Entry>? {
        // create a dataset that has a single entry at the specified goal time
        val sp = this.activity?.getSharedPreferences(SHARED_PREFS, AppCompatActivity.MODE_PRIVATE)
        val goalHour = sp?.getInt("goalHour", 0)
        val goalMinute = sp?.getInt("goalMinute", 0)
        // get the range of dates alarms have been set for
        // then map the goal time for each date
        val goalTimeData = timesSet?.map { entry ->
            val date = convertDateToFloat(entry)
            Entry(date, (goalHour!! * 100 + goalMinute!!) as Float)
        }

        return goalTimeData
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createTimeDataSet(times : List<LocalDateTime>) : List<Entry> {
        val output = times.map { entry ->
            val date = convertDateToFloat(entry)
            val time = convertTimeToFloat(entry)
            Entry(date, time)
        }
        return output
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDateToFloat(date: LocalDateTime) : Float {
        val day = date.dayOfMonth * 1000000;
        val month = date.monthValue * 1000;
        val year = date.year;

        return (day + month + year) as Float
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertTimeToFloat(date: LocalDateTime) : Float {
        val hour = date.hour * 100
        val minute = date.minute

        return (hour + minute) as Float
    }
}