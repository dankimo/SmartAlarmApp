package dankimo.smartalarm

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import dankimo.smartalarm.models.Alarm
import dankimo.smartalarm.models.dateFormatter
import java.time.LocalDateTime


val ALARM_TABLE_NAME = "AlarmTimes"
val COLUMN_TIMESET = "Time_Set"
val COLUMN_TIMESTOPPED = "Time_Stopped"
val COLUMN_ALARMID = "Alarm_Id"
var DB_HELPER : DataBaseHelper? = null

class DataBaseHelper (
    context: Context?,
    name: String? = "smartalarm.db",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1,
) : SQLiteOpenHelper(context, name, factory, version) {
    private var db : SQLiteDatabase = writableDatabase

    override fun onCreate(db: SQLiteDatabase) {
        val createAlarmTableStatement = "CREATE TABLE IF NOT EXISTS $ALARM_TABLE_NAME " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TIMESET TEXT," +
                "$COLUMN_TIMESTOPPED TEXT," +
                "$COLUMN_TIMESTOPPED TEXT);"

        db.execSQL(createAlarmTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addAlarm(time: Alarm) : Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_TIMESET, time.timeSet_toString())
        cv.put(COLUMN_TIMESTOPPED, time.timeStopped_toString())

        val insert = db.insert(ALARM_TABLE_NAME, null, cv)
        return insert != Integer.toUnsignedLong(-1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllAlarms(tableName : String) : List<Alarm> {
        val returnList: MutableList<Alarm> = mutableListOf()

        val query = "SELECT * FROM $tableName"
        val db = this.readableDatabase

        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val alarmID = cursor.getInt(0)
                val timeSet = convertFromStringToDate(cursor.getString(1))
                val timeStopped = convertFromStringToDate(cursor.getString(2))

                val alarm = Alarm(alarmID, timeSet, timeStopped)
                returnList.add(alarm)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return returnList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAlarm(alarmId : Int) : Alarm? {
        var alarm : Alarm? = null
        val query =
            "SELECT $COLUMN_ALARMID, $COLUMN_TIMESET FROM $ALARM_TABLE_NAME WHERE $COLUMN_ALARMID = $alarmId"
        val db = this.readableDatabase

        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            val timeSet = convertFromStringToDate(cursor.getString(1))
            val timeStopped = convertFromStringToDate(cursor.getString(2))
            alarm = Alarm(id, timeSet, timeStopped)
        }

        return alarm
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLatestTimeSet() : Alarm? {
        var alarm : Alarm? = null
        val query =
            "SELECT $COLUMN_TIMESET FROM $ALARM_TABLE_NAME ORDER BY id DESC LIMIT 1"
        val db = this.readableDatabase

        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val timeSet = convertFromStringToDate(cursor.getString(1))
            val timeStopped = convertFromStringToDate(cursor.getString(2))
            alarm = Alarm(null, timeSet, timeStopped)
        }

        return alarm
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addStoppedTime(timeStopped : Alarm) {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_TIMESTOPPED, timeStopped.timeStopped_toString())
        db.update(ALARM_TABLE_NAME, cv, "$COLUMN_TIMESTOPPED= ?", arrayOf(timeStopped.Id.toString()))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addCollection(alarms: List<Alarm>) {
        alarms.forEach { alarm ->
            addAlarm(alarm)
        }
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun convertFromStringToDate(date : String) : LocalDateTime {
            return LocalDateTime.parse(date, dateFormatter)
        }
    }
}
