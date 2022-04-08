package dankimo.smartalarm

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import dankimo.smartalarm.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)

        var title : TextView = activity!!.findViewById(R.id.toolbar_title)
        title.text = "Home"

        var times : HashMap<String, Int> = arguments!!.getSerializable("Times") as HashMap<String, Int>

        var currentTime = Date()
        currentTime.hours = times["currentHour"]!!
        currentTime.minutes = times["currentMinute"]!!
        var goalTime = Date()
        goalTime.hours = times["goalHour"]!!
        goalTime.minutes = times["goalMinute"]!!

        val dateFormat = SimpleDateFormat("HH:mm aa")
        val currentString = "Alarm Set For ${dateFormat.format(currentTime)}"
        val goalString = "Goal Time: ${dateFormat.format(goalTime)}"

        binding.textViewCurrentAlarm.text = currentString
        binding.textViewGoalTime.text = goalString

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true )
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }
}