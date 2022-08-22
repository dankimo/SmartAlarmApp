package dankimo.smartalarm

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dankimo.smartalarm.controllers.DB
import dankimo.smartalarm.databinding.FragmentStatsBinding
import dankimo.smartalarm.models.Alarm
import java.time.LocalDateTime
import java.time.ZoneId

class StatsFragment : Fragment() {
    private lateinit var binding: FragmentStatsBinding

    private var alarms : List<Alarm>? = null

    private var timesSetEntries : List<Entry>? = null
    private var timesStoppedEntries : List<Entry>? = null

    private var timesSetDataSet : LineDataSet? = null
    private var timesStoppedDataSet : LineDataSet? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentStatsBinding.inflate(layoutInflater)

        val title : TextView = activity!!.findViewById(R.id.toolbar_title)
        title.text = "Stats"

        alarms = DB?.getAllAlarms()
        createDataSets()

        val chart = binding.chart
        val lineData = LineData()
        chart.data = lineData
        chart.xAxis.valueFormatter = ChartValueFormatter()

        lineData.addDataSet(timesSetDataSet)
        lineData.addDataSet(timesStoppedDataSet)

        setupChart(chart)

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

    // create the dataset objects for the chart from the alarm time entries
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDataSets()
    {
        // Create Times Set / Stopped Data Sets
        createDataSets(alarms)

        timesSetDataSet = LineDataSet(timesSetEntries, "Time Set")
        timesSetDataSet!!.color = Color.RED;
        timesSetDataSet!!.setDrawValues(false);
        timesStoppedDataSet = LineDataSet(timesStoppedEntries, "Time Stopped")
        timesStoppedDataSet!!.color = Color.BLUE;
        timesStoppedDataSet!!.setDrawValues(false);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createGoalTimeLimit() : LimitLine {
        val sp = this.activity?.getSharedPreferences(SHARED_PREFS, AppCompatActivity.MODE_PRIVATE)
        val goalHour = sp?.getInt("goalHour", 0)
        val goalMinute = sp?.getInt("goalMinute", 0)

        val goalTimeLimit = LimitLine((goalHour!! * 100 + goalMinute!!).toFloat())
        goalTimeLimit.label = "Goal Time"
        goalTimeLimit.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        return goalTimeLimit
    }

    private fun createDataSets(alarms: List<Alarm>?) {
        if (alarms != null && alarms?.isEmpty()) {
            return;
        }

        timesSetEntries = alarms!!.map { alarm ->
            val date = convertDateToFloat(alarm.TimeSet)
            val time = convertTimeToFloat(alarm.TimeSet)
            Entry(date, time)
        }

        val tempList = alarms.map { entry ->
            if (entry.TimeStopped != null) {
                val date = convertDateToFloat(entry.TimeStopped!!)
                val time = convertTimeToFloat(entry.TimeStopped!!)
                Entry(date, time)
            }
            else {
                null
            }
        }
        timesStoppedEntries = tempList.filterNotNull()
    }

    // convert a day/month/year to a float e.g. 12/01/2022 = 12012022
    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertDateToFloat(date: LocalDateTime) : Float {
        val offSet = ZoneId.systemDefault().rules.getOffset(date)
        return date.toInstant(offSet).epochSecond.toFloat()
    }

    // convert an hour/minute value to a float e.g. 07:53 = 0753
    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertTimeToFloat(date: LocalDateTime) : Float {
        val hour = date.hour * 100
        val minute = date.minute

        return (hour + minute).toFloat()
    }

    private fun setupChart(chart: LineChart) {
        chart.setDrawGridBackground(false)
        chart.description.text = ""
        chart.xAxis.setDrawGridLines(false)
        chart.axisRight.setDrawGridLines(false);
        chart.axisLeft.setDrawGridLines(false);
        chart.axisRight.setLabelCount(0,true)
        chart.axisLeft.addLimitLine(createGoalTimeLimit())
        chart.xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.xAxis.labelCount = 5;
    }
}