package dankimo.smartalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationBarView
import dankimo.smartalarm.controllers.DB
import dankimo.smartalarm.controllers.DataBaseController
import dankimo.smartalarm.databinding.ActivityMainBinding
import dankimo.smartalarm.models.Alarm
import dankimo.smartalarm.receivers.AlarmReceiver
import dankimo.smartalarm.receivers.NotificationReceiver
import dankimo.smartalarm.receivers.StopAlarmReceiver
import java.time.LocalDateTime
import java.util.*

val SHARED_PREFS = "SmartAlarmPrefs"

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private var timesSet : Boolean = false
    private var times : HashMap<String, Int>? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent
    private lateinit var navController: NavController

    private val broadCastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateToLatestAlarm()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateToLatestAlarm()
        registerReceiver(broadCastReceiver, IntentFilter("UPDATE_ALARM"))

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.bottomNav.setOnItemSelectedListener(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.white))

        setContentView(binding.root)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.frame_content) as NavHostFragment? ?: return

        navController = host.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.fragment_home)) //  IDs of fragments you want without the ActionBar home/up button

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadCastReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)

        return true
    }

    // todo: make home fragment read times from stored file/db instead of just passing values
    private fun newHomeInstance(times : HashMap<String, Int>): Fragment {
        val f = HomeFragment()
        val args = Bundle()
        args.putSerializable("Times", times)
        f.setArguments(args)
        return f
    }

    private fun onHomeClicked() : Boolean {
        supportFragmentManager.commit {
            replace(R.id.frame_content, newHomeInstance(times!!))
        }
        return true
    }

    private fun onStatsClicked() : Boolean {
        supportFragmentManager.commit {
            replace(R.id.frame_content, StatsFragment())
        }
        return true
    }

    private fun onSettingsClicked() : Boolean {
        supportFragmentManager.commit {
            replace(R.id.frame_content, SettingsFragment())
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_home -> onHomeClicked()
            R.id.nav_stats -> onStatsClicked()
            R.id.nav_settings -> onSettingsClicked()
            else -> {
                false
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reset_alarms -> {
                val sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
                val spEditor = sp.edit()
                spEditor.clear()
                spEditor.apply()

                cancelAlarms()

                DB?.resetDB(this)
                // reconnect to db after deleting
                DB = DataBaseController(this)

                val setInitialIntent = Intent(this, ActivitySetInitial::class.java)
                startActivity(setInitialIntent)

                return true
            }
            R.id.reset_records -> {
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_home)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onStop() {
        super.onStop()

        StopAlarmReceiver.stopAlarmSound(this)
    }

    fun updateToLatestAlarm() {
        val timesExtra : HashMap<String, Int>? = loadData()
        if (timesExtra != null)
        {
            times = timesExtra
            timesSet = true
        }

        if (timesSet) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_YEAR, times!!["currentDayOfYear"]!!)
                set(Calendar.HOUR_OF_DAY, times!!["currentHour"]!!)
                set(Calendar.MINUTE, times!!["currentMinute"]!!)
                set(Calendar.SECOND, 0)
            }

            if (intent.getBooleanExtra("setNewAlarm", false)) {
                startAlarm(calendar)
            }

            supportFragmentManager.commit(allowStateLoss = true) {
                replace(R.id.frame_content, newHomeInstance(times!!))
            }
        }
        else {
            val setInitialIntent = Intent(this, ActivitySetInitial::class.java)
            startActivity(setInitialIntent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData() : HashMap<String, Int>? {
        val sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)

        if (!sp.contains("goalHour") || !sp.contains("goalMinute"))
        {
            return null
        }

        val alarm = DB?.getLatestTimeSet() ?: return null
        goalHour = sp.getInt("goalHour", 0)
        goalMinute = sp.getInt("goalMinute", 0)

        return hashMapOf("currentDayOfYear" to alarm.TimeSet.dayOfYear,
            "currentHour" to alarm.TimeSet.hour, "currentMinute" to alarm.TimeSet.minute,
            "goalHour" to goalHour, "goalMinute" to goalMinute)
        //return hashMapOf("currentHour" to 12, "currentMinute" to 12, "goalHour" to 12, "goalMinute" to 12)
    }

    private fun cancelAlarms() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(this, AlarmReceiver::class.java)
        var pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)

        intent = Intent(this, NotificationReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 2, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startAlarm(c : Calendar) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)

        val notificationIntent = Intent(this, NotificationReceiver::class.java)
        val pendingNotificationIntent = PendingIntent.getBroadcast(
            this, 2, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        c.add(Calendar.MINUTE, 1)

        alarmManager.setExact(AlarmManager.RTC, c.timeInMillis, pendingNotificationIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveNewAlarmToDB(alarmTime : Calendar) {
        val alarmModel = Alarm(null, calendarToLocalDateTime(alarmTime), null)
        DB?.addAlarm(alarmModel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calendarToLocalDateTime(calendar: Calendar) : LocalDateTime{
        // Getting the timezone
        val tz = calendar.timeZone

        // Getting zone Id
        val zoneID = tz.toZoneId()

        // conversion
        val localDateTime = LocalDateTime.ofInstant(calendar.toInstant(), zoneID)
        return localDateTime
    }

    companion object {
        var goalHour: Int = 0
        var goalMinute: Int = 0
    }
}

