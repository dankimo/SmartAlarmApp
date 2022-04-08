package dankimo.smartalarm

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceFragmentCompat
import dankimo.smartalarm.databinding.FragmentSettingsBinding

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        var title : TextView = activity!!.findViewById(R.id.toolbar_title)
        title.text = "Settings"

        addPreferencesFromResource(R.xml.root_preferences)
    }
}