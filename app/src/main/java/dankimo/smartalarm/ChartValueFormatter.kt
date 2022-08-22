package dankimo.smartalarm;

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import dankimo.smartalarm.controllers.DataBaseController.Companion.convertFromDateToChartLabel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ChartValueFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
        // probably only fit max 5 values on graph, so if more than five need to separate out by increments
        // probably up to one week labels max
        // so to start maybe 1 day, 3 days, then 1 week
        return convertFromDateToChartLabel(
            LocalDateTime.ofInstant(Instant.ofEpochSecond(value.toLong()), ZoneId.systemDefault()))
    }
}