package dankimo.smartalarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationBarView
import dankimo.smartalarm.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap

val SHARED_PREFS = "SmartAlarmPrefs"

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private var timesSet : Boolean = false
    private var times : HashMap<String, Int>? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.bottomNav.setOnItemSelectedListener(this)

        val timesExtra : HashMap<String, Int>? = loadData()
        if (timesExtra != null)
        {
            times = timesExtra
            timesSet = true
        }

        // temp code till i actually start storing settings
        if (timesSet) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, times!!["currentHour"]!!)
                set(Calendar.MINUTE, times!!["currentMinute"]!!)
                set(Calendar.SECOND, 0)
            }
            startAlarm(calendar)

            supportFragmentManager.commit {
                replace(R.id.frame_content, newHomeInstance(times!!))
            }
        }
        else {
            val setInitialIntent = Intent(this, ActivitySetInitial::class.java)
            startActivity(setInitialIntent)
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        binding.toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.white))

        setContentView(binding.root)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.frame_content) as NavHostFragment? ?: return

        val navController = host.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.fragment_home)) //  IDs of fragments you want without the ActionBar home/up button

        setupActionBarWithNavController(navController, appBarConfiguration)
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

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.reset_running -> {
//
//            }
//            R.id.reset_cycling -> {
//
//            }
//            R.id.reset_all -> {
//
//            }
//            else -> {
//                return super.onOptionsItemSelected(item)
//            }
//        }
//    }

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
                // delete time settings for testing alarm, should just add reset button for this
                val sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
                val spEditor = sp.edit()
                spEditor.clear()
                spEditor.apply()

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

        cancelAlarm()
        StopAlarmReceiver.stopAlarmSound(this)
    }

    private fun loadData() : HashMap<String, Int>? {
        val sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)

        if (!sp.contains("currentHour") ||
                !sp.contains("currentMinute") ||
                !sp.contains("goalHour") ||
                !sp.contains("goalMinute"))
        {
            return null
        }

        return hashMapOf("currentHour" to sp.getInt("currentHour", 0),
        "currentMinute" to sp.getInt("currentMinute", 0),
        "goalHour" to sp.getInt("goalHour", 0),
        "goalMinute" to sp.getInt("goalMinute", 0))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startAlarm(c : Calendar) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(this, AlarmReceiver::class.java)
        var pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
        saveAlarmToDB(c)

        intent = Intent(this, NotificationReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 2, intent, 0)
        c.add(Calendar.SECOND, 5)

        alarmManager.setExact(AlarmManager.RTC, c.timeInMillis, pendingIntent)
    }

    private fun cancelAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)

        alarmManager.cancel(pendingIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveAlarmToDB(alarmTime : Calendar) {
        val dbh = DataBaseHelper(this)

        val alarmModel = AlarmTimeModel(null, calendarToLocalDateTime(alarmTime), null)
        dbh.addOne(alarmModel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calendarToLocalDateTime(calendar: Calendar) : LocalDateTime{
        // Getting the timezone
        val tz = calendar.timeZone

        // Getting zone id
        val zoneID = tz.toZoneId()

        // conversion
        val localDateTime = LocalDateTime.ofInstant(calendar.toInstant(), zoneID)
        return localDateTime
    }
}

