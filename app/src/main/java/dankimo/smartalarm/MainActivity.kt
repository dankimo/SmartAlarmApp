package dankimo.smartalarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationBarView
import dankimo.smartalarm.databinding.ActivityMainBinding
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
                replace(R.id.nav_host_fragment, newHomeInstance(times!!))
            }
        }
        else {
            val setInitialIntent = Intent(this, ActivitySetInitial::class.java)
            startActivity(setInitialIntent)
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        setContentView(binding.root)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.fragment_home)) //  IDs of fragments you want without the ActionBar home/up button

        setupActionBarWithNavController(navController, appBarConfiguration)
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
            replace(R.id.nav_host_fragment, newHomeInstance(times!!))
        }
        return true
    }

    private fun onStatsClicked() : Boolean {
        supportFragmentManager.commit {
            replace(R.id.nav_host_fragment, StatsFragment())
        }
        return true
    }

    private fun onSettingsClicked() : Boolean {
        supportFragmentManager.commit {
            replace(R.id.nav_host_fragment, SettingsFragment())
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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_home)
        return navController.navigateUp() || super.onSupportNavigateUp()
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

    private fun startAlarm(c : Calendar) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
    }

    private fun cancelAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)

        alarmManager.cancel(pendingIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "smartAlarmChannel"
            val description = "Channel for Smart Alarm app"
            val notificationChannel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = description

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}

