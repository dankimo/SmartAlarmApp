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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dankimo.smartalarm.databinding.FragmentStatsBinding
import dankimo.smartalarm.models.Alarm
import java.time.LocalDateTime

class StatsFragment : Fragment() {
    private lateinit var binding: FragmentStatsBinding
    private var timesSet : List<Alarm>? = null
    private var timesStopped : List<Alarm>? = null
    private var timesSetDataSet : LineDataSet? = null
    private var timesStoppedDataSet : LineDataSet? = null
    private var goalTimeDataSet : LineDataSet? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentStatsBinding.inflate(layoutInflater)

        val title : TextView = activity!!.findViewById(R.id.toolbar_title)
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
        val chart = binding.chart
    }

    // get the alarm time data from the db
    @RequiresApi(Build.VERSION_CODES.O)
    fun getData() {
        timesSet = DB_HELPER?.getAll(ALARM_TABLE_NAME)
        timesStopped = DB_HELPER?.getAll(NOTIFICATION_TABLE_NAME)
    }

    // create the dataset objects for the chart from the alarm time entries
    @RequiresApi(Build.VERSION_CODES.O)
    fun createDataSets()
    {
        // Create Goal Time DataSet
        val goalTimeEntries : List<Entry>? = createGoalTimeEntries()
        // Create Times Set Data Set
        val timesSetEntries : List<Entry>? = createTimeDataSet(timesSet)
        // Create Times Stopped Data Set
        val timesStoppedEntries : List<Entry>? = createTimeDataSet(timesStopped)

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
            val date = convertDateToFloat(entry.time)
            Entry(date, (goalHour!! * 100 + goalMinute!!) as Float)
        }

        return goalTimeData
    }

    // map an individual list of times to a dataset object
    @RequiresApi(Build.VERSION_CODES.O)
    fun createTimeDataSet(times : List<Alarm>?) : List<Entry>? {
        val output = times?.map { entry ->
            val date = convertDateToFloat(entry.time)
            val time = convertTimeToFloat(entry.time)
            Entry(date, time)
        }
        return output
    }

    // convert a day/month/year to a float e.g. 12/01/2022 = 12012022
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDateToFloat(date: LocalDateTime) : Float {
        val day = date.dayOfMonth * 1000000
        val month = date.monthValue * 1000
        val year = date.year

        return (day + month + year) as Float
    }

    // convert an hour/minute value to a float e.g. 07:53 = 0753
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertTimeToFloat(date: LocalDateTime) : Float {
        val hour = date.hour * 100
        val minute = date.minute

        return (hour + minute) as Float
    }
}